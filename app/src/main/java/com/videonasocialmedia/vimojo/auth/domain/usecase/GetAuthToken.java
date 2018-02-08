package com.videonasocialmedia.vimojo.auth.domain.usecase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.auth.AccountConstants;
import com.videonasocialmedia.vimojo.auth.util.UserAccountUtil;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
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
   * @return the auth token string stored in Android Account Manager
   */
  public AuthToken getAuthToken(Context context) {
    AccountManager accountManager = AccountManager.get(context);
    Account account = UserAccountUtil.getAccount(context);
    String token = "";
    try {
      token = accountManager.blockingGetAuthToken(account,
              AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, true);
    } catch (OperationCanceledException | AuthenticatorException | IOException authException) {
      authException.printStackTrace();
      Crashlytics.log("Error accessing Account manager auth token");
      Crashlytics.logException(authException);
    }
    String id = accountManager.getUserData(account, AccountConstants.USER_ID);

    return new AuthToken(token, id);
  }
}
