package com.videonasocialmedia.vimojo.auth.domain.usecase;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.videonasocialmedia.vimojo.auth.view.activity.UserAuthActivity;

import java.lang.ref.WeakReference;

/**
 * Created by jliarte on 11/01/18.
 */

public class AccountAutenticator extends AbstractAccountAuthenticator {
  private static final String VIMOJO_ACCOUNT_TYPE = "com.videonasocialmedia.vimojo.auth";
  private WeakReference<Context> contextWeakReference;

  public AccountAutenticator(Context context) {
    super(context);
    contextWeakReference = new WeakReference<>(context);
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
    return null;
  }

  @Override
  public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                           String authTokenType, String[] requiredFeatures,
                           Bundle options) throws NetworkErrorException {
    final Intent intent = new Intent(getContext(), UserAuthActivity.class);
    intent.putExtra(VIMOJO_ACCOUNT_TYPE, accountType);
    intent.putExtra("full_access", authTokenType);
    intent.putExtra("is_adding_new_account", true);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    final Bundle bundle = new Bundle();
    bundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return bundle;
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
    return null;
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
    return null;
  }

  @Override
  public String getAuthTokenLabel(String s) {
    return null;
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
    return null;
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
    return null;
  }

  public Context getContext() {
    return contextWeakReference.get();
  }


}
