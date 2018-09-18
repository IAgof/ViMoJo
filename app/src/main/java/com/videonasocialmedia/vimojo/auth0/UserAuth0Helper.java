/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.auth0;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth0.accountmanager.AccountConstants;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by alvaro on 2/7/18.
 * <p>
 * Aux class to unify auth0 configuration.
 * Get perform login/register, accestToken, sign out and is user logged.
 * Callback manage in presenters or classes with implement these methods.
 */

public class UserAuth0Helper {
  private String LOG_TAG = UserAuth0Helper.class.getCanonicalName();
  private final String AUTH0_PARAMETER_MAIN_COLOR = "main_color";
  private final String AUTH0_PARAMETER_FLAVOUR = "flavour";
  private final String AUTH0_PREHISTERIC_USER = "prehisteric_user";
  private Auth0 account;
  private AuthenticationAPIClient authenticator;
  private SecureCredentialsManager manager;
  private Context context;
  private final UserApiClient userApiClient;
  private SharedPreferences sharedPreferences;
  private UserEventTracker userEventTracker;

  @Inject
  public UserAuth0Helper(UserApiClient userApiClient, SharedPreferences sharedPreferences,
                         UserEventTracker userEventTracker) {
    this.context = VimojoApplication.getAppContext();
    account = new Auth0(context);
    // Configure the account in OIDC conformant mode
    account.setOIDCConformant(true);
    authenticator = new AuthenticationAPIClient(account);
    manager = new SecureCredentialsManager(context, authenticator,
        new SharedPreferencesStorage(context));
    this.userApiClient = userApiClient;
    this.sharedPreferences = sharedPreferences;
    this.userEventTracker = userEventTracker;
  }

  public void signOut() {
    manager.clearCredentials();
  }

  public boolean isLogged() {
    return manager.hasValidCredentials();
  }

  public void performLogin(Activity activity, UserAuth0Helper.AuthCallback authCallback) {
    String domain = context.getString(R.string.com_auth0_domain);
    // TODO: 10/7/18 Study how to overwrite from build.gradle debug these string_debug, platform_base included
    String audience = BuildConfig.DEBUG ? context.getString(R.string.com_auth0_audience_debug)
        : context.getString(R.string.com_auth0_audience);
    HashMap<String, Object> extraConfigParams = new HashMap<>();
    extraConfigParams.put(AUTH0_PARAMETER_MAIN_COLOR, String.format("#%06x",
        ContextCompat.getColor(context, R.color.colorAccent) & 0xffffff));
    extraConfigParams.put(AUTH0_PARAMETER_FLAVOUR, BuildConfig.FLAVOR);
    // TODO: 29/8/18 Move prehisteric user check to initRegisterLoginPresenter when we only have one access to perform login
    extraConfigParams.put(AUTH0_PREHISTERIC_USER,
        sharedPreferences.getBoolean(ConfigPreferences.PREHISTERIC_USER, false));
    //Use the account in the API clients
    WebAuthProvider.init(account)
        .withScheme("https")
        .withScope("openid offline_access profile email")
        .withAudience(String.format(audience.toString(), domain))
        .withParameters(extraConfigParams)
        .start(activity, new com.auth0.android.provider.AuthCallback() {
          @Override
          public void onFailure(@NonNull Dialog dialog) {
            Log.d(LOG_TAG, "Error performLogin onFailure ");
            authCallback.onFailure(new AuthenticationException("Unknown reason"));
          }

          @Override
          public void onFailure(AuthenticationException exception) {
            Log.d(LOG_TAG, "Error performLogin AuthenticationException "
                    + exception.getMessage());
            Crashlytics.log("Error performLogin AuthenticationException: " + exception);
            authCallback.onFailure(exception);
          }

          @Override
          public void onSuccess(@NonNull Credentials credentials) {
            Log.d(LOG_TAG, "Logged in: " + credentials.getAccessToken());
            saveCredentials(credentials);
            authCallback.onSuccess(credentials);
          }
        });
  }

  public void saveCredentials(Credentials credentials) {
    // save credentials, user logged
    manager.saveCredentials(credentials);
    // TODO: 29/8/18 Move tracking to initRegisterLoginPresenter when we only have one access to perform login
    userEventTracker.trackUserLoggedIn(false);
    userEventTracker.trackUserAuth0Id(credentials.getIdToken());
    // save account, save platform user id
    getUserProfile(credentials.getAccessToken(),
        new BaseCallback<UserProfile, AuthenticationException>() {
          @Override
          public void onFailure(AuthenticationException error) {
            // TODO(jliarte): 10/07/18 handle this error!

            Log.d(LOG_TAG, "onFailure getting user profile AuthenticationException "
                + error.getMessage());
            Crashlytics.log("Failure getting user profile AuthenticationException "
                + error.getMessage());
          }

          @Override
          public void onSuccess(UserProfile userProfile) {
            // UserId
            String userId = null;
            try {
              userId = userApiClient.getUserId(credentials.getAccessToken()).getId();
              registerAccount(userProfile.getEmail(), "fakePassword",
                  "fakeToken", userId);
              // TODO: 29/8/18 Move tracking to initRegisterLoginPresenter when we only have one access to perform login
              userEventTracker.trackUserEmailSet(userProfile.getEmail());
              userEventTracker.trackUserId(userId);
              boolean userAliased = sharedPreferences.getBoolean(ConfigPreferences.USER_ALIASED,
                  false);
              if (!userAliased) {
                userEventTracker.aliasUser(userProfile.getEmail());
                sharedPreferences.edit().putBoolean(ConfigPreferences.USER_ALIASED, true).apply();
              }

            } catch (VimojoApiException vimojoApiException) {
              // TODO(jliarte): 10/07/18 notify user an error authenticating!!!
              Log.d(LOG_TAG, "vimojoApiException " + vimojoApiException.getApiErrorCode());
              Crashlytics.log("Error process getting UserId vimojoApiException");
              Crashlytics.logException(vimojoApiException);
            }
          }
        });
  }

  public void getUserProfile(String accessToken, BaseCallback<UserProfile,
          AuthenticationException> baseCallBack) {
    authenticator.userInfo(accessToken).start(baseCallBack);
  }

  public void registerAccount(String email, String fakePassword, String accessToken, String id) {
    Account account = new Account(email, AccountConstants.VIMOJO_ACCOUNT_TYPE);
    AccountManager am = AccountManager.get(context);
    final Bundle extraData = new Bundle();
    extraData.putString(AccountConstants.USER_ID, id);
    am.addAccountExplicitly(account, fakePassword, extraData);
    am.setAuthToken(account, AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, accessToken);
  }

  public void getAccessToken(BaseCallback<Credentials, CredentialsManagerException> baseCallback) {
    manager.getCredentials(baseCallback);
  }

  public interface AuthCallback {
    void onSuccess(Credentials credentials);

    void onFailure(AuthenticationException exception);
  }
}
