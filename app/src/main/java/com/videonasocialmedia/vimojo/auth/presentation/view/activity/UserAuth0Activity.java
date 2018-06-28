/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.auth.presentation.view.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.AccountConstants;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;

/**
 * Activity for creating and signing in users into Vimojo web platform.
 */
public class UserAuth0Activity extends VimojoActivity {
  private static final String TAG = UserAuth0Activity.class.getCanonicalName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(R.style.VideonaThemeUserAuth);
    setContentView(R.layout.activity_user_auth0);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    setStatusBarTransparent();
    performAuth0LogIn();
  }

  private void setStatusBarTransparent() {
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }


  public void performAuth0LogIn() {
    //Auth0 account = new Auth0("{YOUR_CLIENT_ID}", "{YOUR_DOMAIN}");
    Auth0 account = new Auth0(this);
    //Configure the account in OIDC conformant mode
    account.setOIDCConformant(true);
    AuthenticationAPIClient apiClient = new AuthenticationAPIClient(account);
    SecureCredentialsManager manager = new SecureCredentialsManager(this, apiClient,
        new SharedPreferencesStorage(this));
    //Use the account in the API clients
    WebAuthProvider.init(account)
        .withScheme("https")
        .withScope("openid profile email")
        .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
        .start(UserAuth0Activity.this, new AuthCallback() {
          @Override
          public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                dialog.show();
              }
            });
          }

          @Override
          public void onFailure(final AuthenticationException exception) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(UserAuth0Activity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
              }
            });
          }

          @Override
          public void onSuccess(@NonNull final Credentials credentials) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Log.d(TAG, "Logged in: " + credentials.getAccessToken());
                manager.saveCredentials(credentials);
                String token = credentials.getAccessToken();
                getUserInfo(token);

              }
            });
          }
        });
  }

  private void getUserInfo(String accessToken) {
    UserApiClient userApiClient = new UserApiClient(this);
    userApiClient.getAuthenticator().userInfo(accessToken)
        .start(new BaseCallback<UserProfile, AuthenticationException>() {
          @Override
          public void onSuccess(UserProfile userinfo) {
            // Display the user profile
            registerAccount(userinfo.getEmail(), "fakePassword", accessToken,
                userinfo.getId());
            setResult(RESULT_OK);
            finish();
          }

          @Override
          public void onFailure(AuthenticationException error) {
            // Show error
          }
        });
  }


  private void registerAccount(String email, String password, String authToken, String id) {
    Account account = new Account(email, AccountConstants.VIMOJO_ACCOUNT_TYPE);
    AccountManager am = AccountManager.get(this);
    final Bundle extraData = new Bundle();
    extraData.putString(AccountConstants.USER_ID, id);
    am.addAccountExplicitly(account, password, extraData);
    am.setAuthToken(account, AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, authToken);
  }

}
