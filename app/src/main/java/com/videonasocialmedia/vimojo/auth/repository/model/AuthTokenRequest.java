package com.videonasocialmedia.vimojo.auth.repository.model;

import com.google.gson.Gson;

/**
 * Created by jliarte on 11/01/18.
 */

/**
 * Model class for token request auth API calls
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
