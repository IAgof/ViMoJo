package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 28/11/17.
 */

public class AuthToken {
  private final String token;

  public AuthToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
