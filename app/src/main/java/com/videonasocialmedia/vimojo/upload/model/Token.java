package com.videonasocialmedia.vimojo.upload.model;

/**
 * Created by alvaro on 28/11/17.
 */

public class Token {
  private final String access_token;

  public Token(String token) {
    this.access_token = token;
  }

  public String getToken() {
    return access_token;
  }
}
