package com.videonasocialmedia.vimojo.upload.repository.localsource;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;

/**
 * Created by alvaro on 28/11/17.
 */
// TODO:(alvaro.martinez) 28/11/17 Study Videona Cached AuthToken and implement local source implementation, SharedPreferences ¿?
public class CachedToken {

  private CachedToken() {

  }

  public static AuthToken getToken() {
    //String token = authPreference.getString("AUTH_TOKEN", null);
    // AuthToken has to be form in this way Bearer + token
    return new AuthToken("Bearer " + BuildConfig.VIDAY_TOKEN);
  }

  public static boolean hasToken() {
   // return authPreference.getBoolean("HAS_TOKEN", false);
    return true;
  }
}
