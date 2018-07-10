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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Service for Android Account Authenticator, used to register our own Authenticator with the
 * system.
 */
public class AuthenticatorService extends Service {
  private AccountAuthenticator accountAuthenticator;

  @Override
  public void onCreate() {
    accountAuthenticator = new AccountAuthenticator(this);
  }

  /*
  * When the system binds to this Service to make the RPC call
  * return the authenticator's IBinder.
  */
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return accountAuthenticator.getIBinder();
  }
}