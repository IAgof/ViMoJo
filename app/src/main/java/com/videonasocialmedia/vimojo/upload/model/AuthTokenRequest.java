package com.videonasocialmedia.vimojo.upload.model;

import com.google.gson.Gson;

/**
 * Created by alvaro on 28/11/17.
 */

public class AuthTokenRequest {
  private final String username;
  private final String password;

  public AuthTokenRequest(String userName, String password) {
    this.username = userName;
    this.password = password;
  }

  public String toJson() {
    return new Gson().toJson(this);
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}