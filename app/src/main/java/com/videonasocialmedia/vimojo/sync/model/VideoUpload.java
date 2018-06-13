/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.model;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;

import java.util.UUID;

import retrofit2.Call;

/**
 * Model for enqueue video uploads to vimojo platform.
 * AuthToken, mediaPath, description info needed for video api service
 * NumTries, to manage a politic of maximum number of tries to upload a video.
 */
public class VideoUpload {

  private String uuid = UUID.randomUUID().toString();
  private final int id;
  private String mediaPath;
  private String title;
  private String productTypeList;
  private String description;
  private int numTries;
  private boolean isAcceptedUploadMobileNetwork;
  public final static int MAX_NUM_TRIES_UPLOAD = 3;
  private boolean isUploading;

  public VideoUpload(int id, String mediaPath, String title, String description,
                     String productTypeList, boolean isAcceptedUploadMobileNetwork,
                     boolean isUploading) {
    this.id = id;
    this.mediaPath = mediaPath;
    this.title = title;
    this.description = description;
    this.productTypeList = productTypeList;
    this.numTries = 0;
    this.isAcceptedUploadMobileNetwork = isAcceptedUploadMobileNetwork;
    this.isUploading = isUploading;
  }

  public int getId() {
    return id;
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

  public boolean isAcceptedUploadMobileNetwork() {
    return isAcceptedUploadMobileNetwork;
  }

  public boolean isUploading() {
    return isUploading;
  }

  public void setUploading(boolean uploading) {
    isUploading = uploading;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

}
