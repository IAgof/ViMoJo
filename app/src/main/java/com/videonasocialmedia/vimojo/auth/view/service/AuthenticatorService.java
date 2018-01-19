package com.videonasocialmedia.vimojo.auth.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.videonasocialmedia.vimojo.auth.domain.usecase.AccountAuthenticator;

/**
 * Created by jliarte on 18/01/18.
 */

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
