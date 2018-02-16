/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.model;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

/**
 * Model for enqueue video uploads to vimojo platform.
 * AuthToken, mediaPath, description info needed for video api service
 * NumTries, to manage a politic of maximum number of tries to upload a video.
 */
public class VideoUpload {
  private String authToken;
  private String mediaPath;
  private String title;
  private String productTypeList;
  private String description;
  private int numTries;
  public final static int MAX_NUM_TRIES_UPLOAD = 3;

  public VideoUpload(String authToken, String mediaPath, String title, String description,
                     String productTypeList) {
    this.authToken = authToken;
    this.mediaPath = mediaPath;
    this.title = title;
    this.description = description;
    this.productTypeList = productTypeList;
    this.numTries = 0;
  }

  public String getAuthToken() {
    return authToken;
  }

  public String getMediaPath() {
    return mediaPath;
  }

  public String getTitle() {
    return title;
  }

  public String getProductTypeList() {
    return productTypeList;
  }

  public String getDescription() {
    return description;
  }

  public int getNumTries() {
    return numTries;
  }

  public void incrementNumTries() {
    numTries++;
  }
}
