package com.videonasocialmedia.vimojo.auth.repository.model;

import com.google.gson.Gson;

/**
 * Created by jliarte on 11/01/18.
 */

/**
 * Model class for user registration API calls
 */
public class RegisterRequest {
  private final String email;
  private final String password;
  private final boolean checkBoxAcceptTermChecked;

  public RegisterRequest(String email, String password, boolean checkBoxAcceptTermChecked) {
    this.email = email;
    this.password = password;
    this.checkBoxAcceptTermChecked = checkBoxAcceptTermChecked;
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
