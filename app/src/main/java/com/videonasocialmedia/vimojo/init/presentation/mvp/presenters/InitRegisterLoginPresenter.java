/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.init.presentation.mvp.presenters;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.result.Credentials;
import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.init.presentation.mvp.views.InitRegisterLoginView;
import com.videonasocialmedia.vimojo.init.presentation.views.activity.InitRegisterLoginActivity;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

/**
 * Created by alvaro on 24/8/18.
 */

public class InitRegisterLoginPresenter extends VimojoPresenter {

  private String LOG_TAG = InitRegisterLoginActivity.class.getCanonicalName();
  private InitRegisterLoginView initRegisterLoginView;
  private UserAuth0Helper userAuth0Helper;
  private Context context;

  public InitRegisterLoginPresenter(Context context, InitRegisterLoginView initRegisterLoginView,
                                    UserAuth0Helper userAuth0Helper) {
    this.context = context;
    this.initRegisterLoginView = initRegisterLoginView;
    this.userAuth0Helper = userAuth0Helper;
  }

  public void init() {
    Uri videoUri = Uri.parse("android.resource://" + context.getPackageName()
        + "/" + R.raw.initregisterloginvideo);
    initRegisterLoginView.setVideoOnLoop(videoUri);
  }

  public void performLogin(InitRegisterLoginActivity initRegisterLoginActivity) {
    userAuth0Helper.performLogin(initRegisterLoginActivity, new AuthCallback() {
      @Override
      public void onFailure(@NonNull Dialog dialog) {
        Log.d(LOG_TAG, "Error performLogin onFailure ");
        initRegisterLoginView.showErrorAuth0(R.string.auth0_error_login_failure);
      }

      @Override
      public void onFailure(AuthenticationException exception) {
        Log.d(LOG_TAG, "Error performLogin AuthenticationException "
            + exception.getMessage());
        Crashlytics.log("Error performLogin AuthenticationException: " + exception);
        initRegisterLoginView.showErrorAuth0(R.string.auth0_error_authentication);
      }

      @Override
      public void onSuccess(@NonNull Credentials credentials) {
        Log.d(LOG_TAG, "Logged in: " + credentials.getAccessToken());
        initRegisterLoginView.pauseVideo();
        userAuth0Helper.saveCredentials(credentials);
        initRegisterLoginView.navigateToRecordCamera2();
      }
    });
  }
}
