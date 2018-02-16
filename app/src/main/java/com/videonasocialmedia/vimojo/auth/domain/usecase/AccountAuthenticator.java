package com.videonasocialmedia.vimojo.auth.domain.usecase;

/**
 * Created by jliarte on 11/01/18.
 */

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.AccountConstants;
import com.videonasocialmedia.vimojo.auth.presentation.view.activity.UserAuthActivity;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.AuthApiClient;

import java.lang.ref.WeakReference;

/**
 * Class for Account Manager Auth Service. This class uses Android Account Manager to cache
 * user auth credentials into Android Account Manager and handles sign in calls when a token is
 * not available.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {
  private WeakReference<Context> contextWeakReference;
  private Context context;

  public AccountAuthenticator(Context context) {
    super(context);
    contextWeakReference = new WeakReference<>(context);
    this.context = context;
  }

  @Override
  public Bundle editProperties(
          AccountAuthenticatorResponse accountAuthenticatorResponse, String accountType) {
    return null;
  }

  @Override
  public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                           String authTokenType, String[] requiredFeatures,
                           Bundle options) throws NetworkErrorException {
    final Intent intent = new Intent(getContext(), UserAuthActivity.class);
    intent.putExtra(context.getString(R.string.account_type), accountType);
    intent.putExtra(AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, authTokenType);
    intent.putExtra("is_adding_new_account", true);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    final Bundle bundle = new Bundle();
    bundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return bundle;
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                   Account account, Bundle options) throws NetworkErrorException {
    return null;
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                             String authTokenType, Bundle options) throws NetworkErrorException {
    AccountManager accountManager = AccountManager.get(getContext());
    String authToken = accountManager.peekAuthToken(account, authTokenType);
    if (TextUtils.isEmpty(authToken)) {
      AuthApiClient authApiClient = new AuthApiClient();
      try {
        authToken = authApiClient.signIn(
                account.name, accountManager.getPassword(account)).getToken();
      } catch (VimojoApiException getAuthTokenException) {
        if (BuildConfig.DEBUG) {
          getAuthTokenException.printStackTrace();
        }
      }
    }
    if (!TextUtils.isEmpty(authToken)) {
      final Bundle result = new Bundle();
      result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
      result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
      result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
      return result;
    }
    // If you reach here, person needs to login again. or sign up
    // If we get here, then we couldn't access the user's password - so we
    // need to re-prompt them for their credentials. We do that by creating
    // an intent to display our AuthenticatorActivity which is the AccountsActivity in my case.
    final Intent intent = new Intent(getContext(), UserAuthActivity.class);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    intent.putExtra(context.getString(R.string.account_type), account.type);
    intent.putExtra(AccountConstants.VIMOJO_AUTH_TOKEN_TYPE, authTokenType);
    Bundle retBundle = new Bundle();
    retBundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return retBundle;
  }

  @Override
  public String getAuthTokenLabel(String authTokenType) {
    return null;
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                  Account account, String authTokenType, Bundle options)
          throws NetworkErrorException {
    return null;
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse,
                            Account account, String[] strings) throws NetworkErrorException {
    return null;
  }

  public Context getContext() {
    return contextWeakReference.get();
  }

}
