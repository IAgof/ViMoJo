/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.share.presentation.views.utils;

import android.text.TextUtils;

import javax.inject.Inject;

/**
 * Created by alvaro on 9/2/18.
 */

public class LoggedValidator {
  @Inject
  public LoggedValidator() {

  }

  public boolean loggedValidate(String authToken) {
    return !TextUtils.isEmpty(authToken);
  }
}
