package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 28/11/17.
 */

public class AuthToken {
  private final String token;
  private final String _id;

  public AuthToken(String token, String _id) {
    this.token = token;
    this._id = _id;
  }

  public String getToken() {
    return token;
  }

  public String getId() { return _id; }
}
