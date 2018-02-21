package com.videonasocialmedia.vimojo.vimojoapiclient.model;

import com.google.gson.Gson;

/**
 * Created by jliarte on 12/01/18.
 */

/**
 * Model class for user API calls.
 */
public class User {
  private final String username;
  private final String email;
  private final String _id;
  public User(String username, String email, String _id) {
    this.username = username;
    this.email = email;
    this._id = _id;
  }

  public String toJson() {
    return new Gson().toJson(this);
  }

  public String getEmail() {
    return email;
  }

  public String get_id() {
    return _id;
  }

  public String getUsername() {
    return username;
  }
}
