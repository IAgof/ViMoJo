package com.videonasocialmedia.vimojo.upload.repository.localsource;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.upload.model.Token;

/**
 * Created by alvaro on 28/11/17.
 */
// TODO:(alvaro.martinez) 28/11/17 Study Videona Cached Token and implement local source implementation, SharedPreferences ¿?
public class CachedToken {

  private CachedToken() {

  }

  public static Token getToken() {
    //String token = authPreference.getString("AUTH_TOKEN", null);
    // Token has to be form in this way Bearer + token
    return new Token("Bearer " + BuildConfig.VIDAY_TOKEN);
  }

  public static boolean hasToken() {
   // return authPreference.getBoolean("HAS_TOKEN", false);
    return true;
  }
}
