/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.auth0.accountmanager;

/**
 * Created by alvaro on 5/7/18.
 */

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;


/**
 * Class for Account Manager Auth Service. This class uses Android Account Manager to cache
 * user auth credentials into Android Account Manager and handles sign in calls when a token is
 * not available. Empty class needed to follow account workflow.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

  public AccountAuthenticator(Context context) {
    super(context);
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
    return null;
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                   Account account, Bundle options) throws NetworkErrorException {
    return null;
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                             String authTokenType, Bundle options) throws NetworkErrorException {
    return null;
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

}