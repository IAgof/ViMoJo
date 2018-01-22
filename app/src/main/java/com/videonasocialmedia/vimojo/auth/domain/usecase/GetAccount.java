package com.videonasocialmedia.vimojo.auth.domain.usecase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.videonasocialmedia.vimojo.auth.util.UserAccountUtil;

import javax.inject.Inject;

/**
 * Created by jliarte on 18/01/18.
 */

/**
 * Use case for getting current vimojo user account in Android Account Manager.
 */
public class GetAccount {
  @Inject
  public GetAccount() {
  }

  /**
   * Returns the current user account stored in Android Account Manager.
   *
   * @param context the app context
   * @return the current user account stored in Android Account Manager
   */
  public Account getCurrentAccount(Context context) {
    AccountManager accountManager = AccountManager.get(context);
    Account account = UserAccountUtil.getAccount(context);
    return account;
  }
}
