/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.init.presentation.mvp.views;

import android.net.Uri;

/**
 * Created by alvaro on 24/8/18.
 */
public interface InitRegisterLoginView {

  void showErrorAuth0(int errorAuth0);

  void navigateToRecordCamera2();

  void setVideoOnLoop(Uri videoUri);

  void pauseVideo();
}
