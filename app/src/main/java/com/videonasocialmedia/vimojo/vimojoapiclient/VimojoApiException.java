package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 12/01/18.
 */

public class VimojoApiException extends Exception {
  public static final String UNKNOWN_ERROR = "Unknown Error";
  public static final String NETWORK_ERROR = "Network Error";
  public static final String UNAUTHORIZED = "Unauthorized";
  private final int httpCode;
  private final String apiErrorCode;

  public VimojoApiException(int httpCode, String apiErrorCode) {
    this.httpCode = httpCode;
    this.apiErrorCode = apiErrorCode;
  }

  public VimojoApiException() {
    this(-1, UNKNOWN_ERROR);
  }

  public int getHttpCode() {
    return httpCode;
  }

  public String getApiErrorCode() {
    return apiErrorCode;
  }

}
