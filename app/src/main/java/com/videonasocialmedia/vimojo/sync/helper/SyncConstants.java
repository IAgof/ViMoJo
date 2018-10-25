/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.helper;

import com.videonasocialmedia.vimojo.BuildConfig;

import static com.videonasocialmedia.vimojo.utils.Constants.BASE_PACKAGE_NAME;

/**
 * Created by alvaro on 21/2/18.
 */

public class SyncConstants {

  public static final String VIMOJO_CONTENT_AUTHORITY = getContentAuthority();

  private static String getContentAuthority() {
    if (BuildConfig.FLAVOR.equals("vimojo")) {
      return BASE_PACKAGE_NAME + ".main" + ".provider";
    }
    return BASE_PACKAGE_NAME + "." + BuildConfig.FLAVOR + ".provider";
  }

}
