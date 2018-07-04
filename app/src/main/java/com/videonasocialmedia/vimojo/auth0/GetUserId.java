/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.auth0;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserId;


import javax.inject.Inject;

/**
 * Created by alvaro on 4/7/18.
 */

public class GetUserId {
  @Inject
  public GetUserId() {
  }

  /**
   * Returns the auth token stored in Android Account Manager.
   *
   * @param context the app context
   * @return AuthToken object with token string and user id stored in Android Account Manager or
   * empty token and user id if no account present
   */
  public UserId getUserId(Context context) {
    String token = "";
    String id = "";
    AccountManager accountManager = AccountManager.get(context);
    Account account = UserAccountUtil.getAccount(context);
    if (account != null) {
      id = accountManager.getUserData(account, AccountConstants.USER_ID);
      /* Get token from auth0 Credentials, manage renew token expire by itself.
      try {
        token = accountManager.blockingGetAuthToken(account,
            AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, true);
      } catch (OperationCanceledException | AuthenticatorException | IOException authException) {
        if (BuildConfig.DEBUG) {
          authException.printStackTrace();
        }
        Crashlytics.log("Error accessing Account manager auth token");
        Crashlytics.logException(authException);
      } */
    }
    return new UserId(id);
  }
}