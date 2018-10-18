package com.videonasocialmedia.vimojo.repository;

/**
 * Created by jliarte on 10/08/18.
 */

/**
 * Value to specify modifiers over the retrieval operations on repositories and data sources.
 */
public enum DeletePolicy {
  LOCAL_ONLY,
  API_ONLY,
  DELETE_ALL;

  public boolean useLocal() {
    return this == LOCAL_ONLY || this == DELETE_ALL;
  }

  public boolean useRemote() {
    return this == API_ONLY || this == DELETE_ALL;
  }

}
