/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.auth0.accountmanager;

/**
 * Created by jliarte on 18/01/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;

import static com.videonasocialmedia.vimojo.utils.Constants.BASE_PACKAGE_NAME;

/**
 * Constants values for Vimojo Account.
 */
public class AccountConstants {

  // (jliarte): 18/01/18 has to be the same value that one defined in @string/account_type

  public static final String VIMOJO_ACCOUNT_TYPE = getAccountType();
  public static final String VIMOJO_AUTH_TOKEN_TYPE = "full_access";
  public static final String USER_ID = "user_id";

  private static String getAccountType() {
    if(BuildConfig.FLAVOR.equals("vimojo")) {
      return BASE_PACKAGE_NAME + ".main" + ".auth0";
    }
    return BASE_PACKAGE_NAME + "." + BuildConfig.FLAVOR + ".auth0";
  }

}
