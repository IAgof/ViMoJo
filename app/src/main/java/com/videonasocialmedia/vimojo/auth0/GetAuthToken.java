/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.auth0;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth.AccountConstants;
import com.videonasocialmedia.vimojo.auth.util.UserAccountUtil;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by jliarte on 18/01/18.
 */

/**
 * Use case for getting auth token from Android Account Manager.
 */
public class GetAuthToken {
  @Inject
  public GetAuthToken() {
  }

  /**
   * Returns the auth token stored in Android Account Manager.
   *
   * @param context the app context
   * @return AuthToken object with token string and user id stored in Android Account Manager or
   * empty token and user id if no account present
   */
  public AuthToken getAuthToken(Context context) {
    String token = "";
    String id = "";
    AccountManager accountManager = AccountManager.get(context);
    Account account = UserAccountUtil.getAccount(context);
    if (account != null) {
      try {
        token = accountManager.blockingGetAuthToken(account,
                AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, true);
      } catch (OperationCanceledException | AuthenticatorException | IOException authException) {
        if (BuildConfig.DEBUG) {
          authException.printStackTrace();
        }
        Crashlytics.log("Error accessing Account manager auth token");
        Crashlytics.logException(authException);
      }
      id = accountManager.getUserData(account, AccountConstants.USER_ID);
    }
    return new AuthToken(token, id);
  }
}
