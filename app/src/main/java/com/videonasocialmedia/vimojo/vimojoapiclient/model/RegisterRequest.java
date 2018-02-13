package com.videonasocialmedia.vimojo.vimojoapiclient.model;

import com.google.gson.Gson;

/**
 * Created by jliarte on 11/01/18.
 */

/**
 * Model class for user registration API calls.
 */
public class RegisterRequest {
  private final String username;
  private final String email;
  private final String password;
  private final boolean checkBoxAcceptTermChecked;

  /**
   * Register Request constructor
   * @param username user name for user account
   * @param email email for user account. This will be the account identificator.
   * @param password password for user account.
   * @param checkBoxAcceptTermChecked user acceptance of privacy and policy terms.
   */
  public RegisterRequest(String username, String email, String password,
                         boolean checkBoxAcceptTermChecked) {
    this.username = username;
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

  public String getUserName() { return username; }
}
