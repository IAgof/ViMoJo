package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 28/11/17.
 */

import com.google.gson.Gson;

/**
 * Model class for token request auth API calls.
 */
public class AuthTokenRequest {
  private final String email;
  private final String password;

  public AuthTokenRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String toJson() {
    return new Gson().toJson(this);
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}