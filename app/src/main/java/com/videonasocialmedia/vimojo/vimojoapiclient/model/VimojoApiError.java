package com.videonasocialmedia.vimojo.vimojoapiclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jliarte on 12/01/18.
 */

public class VimojoApiError {
  @SerializedName("code") private String code;
  @SerializedName("error") private String error;
  @SerializedName("status") private String status = "";

  public String getCode() {
    return code;
  }

  public String getError() {
    return error;
  }

  public String getStatus() {
    return status;
  }
}
