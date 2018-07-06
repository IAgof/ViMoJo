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

import com.videonasocialmedia.vimojo.auth0.accountmanager.AccountConstants;
import com.videonasocialmedia.vimojo.auth0.utils.UserAccountUtil;
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
   * @return UserId object with user id stored in Android Account Manager
   */
  public UserId getUserId(Context context) {
    String id = "";
    AccountManager accountManager = AccountManager.get(context);
    Account account = UserAccountUtil.getAccount(context);
    if (account != null) {
      id = accountManager.getUserData(account, AccountConstants.USER_ID);
    }
    return new UserId(id);
  }
}