package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 28/11/17.
 */

public class AuthToken {
  private final String access_token;

  public AuthToken(String token) {
    this.access_token = token;
  }

  public String getToken() {
    return access_token;
  }
}
