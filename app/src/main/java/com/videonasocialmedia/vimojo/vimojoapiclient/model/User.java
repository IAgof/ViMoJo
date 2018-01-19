package com.videonasocialmedia.vimojo.vimojoapiclient.model;

import com.google.gson.Gson;

/**
 * Created by jliarte on 12/01/18.
 */

public class User {
  private final String email;
  private final String _id;

  public User(String email, String _id) {
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
}
