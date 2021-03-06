/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.auth0.utils;

/**
 * Created by jliarte on 18/01/18.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v13.app.ActivityCompat;
import android.util.Log;

import static com.videonasocialmedia.vimojo.auth0.accountmanager.AccountConstants.VIMOJO_ACCOUNT_TYPE;

/**
 * Utility class for getting current Vimojo Android Account.
 */
public class UserAccountUtil {
  private static final String TAG = UserAccountUtil.class.getSimpleName();


  /**
   * Returns current vimojo account stored in Android Account Manager.
   *
   * @param context the app context.
   * @return current vimojo account stored in Android Account Manager.
   */
  public static Account getAccount(Context context) {
    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS)
            != PackageManager.PERMISSION_GRANTED) {
      Log.d(TAG, "permission GET_ACCOUNTS not present.");
    }

    AccountManager accountManager = AccountManager.get(context);
    Account[] accounts = accountManager.getAccountsByType(VIMOJO_ACCOUNT_TYPE);
    if (accounts.length > 0) {
      return accounts[0];
    } else {
      return null;
    }
  }

}
