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
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.auth0.accountmanager.AccountConstants;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;


/**
 * Created by alvaro on 2/7/18.
 * <p>
 * Aux class to unify auth0 configuration.
 * Get perform login/register, accestToken, sign out and is user logged.
 * Callback manage in presenters or classes with implement these methods.
 */

public class UserAuth0Helper {

  private String LOG_TAG = UserAuth0Helper.class.getCanonicalName();
  private Auth0 account;
  private AuthenticationAPIClient authenticator;
  private SecureCredentialsManager manager;
  private Context context;
  private final UserApiClient userApiClient;

  public UserAuth0Helper(UserApiClient userApiClient) {
    this.context = VimojoApplication.getAppContext();
    account = new Auth0(context);
    //Configure the account in OIDC conformant mode
    account.setOIDCConformant(true);
    authenticator = new AuthenticationAPIClient(account);
    manager = new SecureCredentialsManager(context, authenticator,
        new SharedPreferencesStorage(context));
    this.userApiClient = userApiClient;
  }

  public void signOut() {
    manager.clearCredentials();
  }

  public boolean isLogged() {
    return manager.hasValidCredentials();
  }

  public void performLogin(Activity activity, String auth0Domain, AuthCallback authCallback) {
    //Use the account in the API clients
    WebAuthProvider.init(account)
        .withScheme("https")
        .withScope("openid offline_access profile email")
        .withAudience(String.format("https://vimojo.auth/api", auth0Domain))
        .start(activity, authCallback);
  }

  public void saveCredentials(Credentials credentials) {
    // save credentials, user logged
    manager.saveCredentials(credentials);

    // save account, save platform user id
    getUserProfile(credentials.getAccessToken(),
        new BaseCallback<UserProfile, AuthenticationException>() {
          @Override
          public void onFailure(AuthenticationException error) {

          }

          @Override
          public void onSuccess(UserProfile userProfile) {
            // UserId
            String userId = null;
            try {
              userId = userApiClient.getUserId(credentials.getAccessToken()).getId();
              registerAccount(userProfile.getEmail(), "fakePassword",
                  "fakeToken", userId);
            } catch (VimojoApiException vimojoApiException) {
              Log.d(LOG_TAG, "vimojoApiException " + vimojoApiException.getApiErrorCode());
              Crashlytics.log("Error process getting UserId vimojoApiException");
              Crashlytics.logException(vimojoApiException);
            }
          }
        });

  }

  public void getUserProfile(String accessToken,
                             BaseCallback<UserProfile, AuthenticationException> baseCallBack) {
    authenticator.userInfo(accessToken)
        .start(baseCallBack);
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

}
