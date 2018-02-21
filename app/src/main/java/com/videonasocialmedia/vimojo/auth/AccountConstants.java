package com.videonasocialmedia.vimojo.auth;

/**
 * Created by jliarte on 18/01/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;

/**
 * Constants values for Vimojo Account.
 */
public class AccountConstants {

  // (jliarte): 18/01/18 has to be the same value that one defined in @string/account_type
  public static final String VIMOJO_ACCOUNT_TYPE = BuildConfig.APPLICATION_ID + ".auth";
  public static final String VIMOJO_AUTH_TOKEN_TYPE = "full_access";
  public static final String USER_ID = "user_id";

}
