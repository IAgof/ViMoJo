package com.videonasocialmedia.vimojo.upload.domain;

/**
 * Created by alvaro on 28/11/17.
 */

public interface OnUploadVideoListener {

  void onUploadVideoError(Causes causes);

  void onUploadVideoSuccess();

  enum Causes {
    NETWORK_ERROR, CREDENTIALS_EXPIRED, UNKNOWN_ERROR, CREDENTIALS_UNKNOWN
  }
}
