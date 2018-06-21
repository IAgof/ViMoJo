/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

import com.videonasocialmedia.videonamediaframework.model.media.Media;

import java.util.Date;

/**
 * Created by alvaro on 21/6/18.
 */

public class AssetUpload {

  private final Media media;
  private String name;
  private String type;
  private String hash;
  private String date;
  private int numTries;
  public final static int MAX_NUM_TRIES_UPLOAD = 3;

  public AssetUpload(Media media) {
    this.media = media;
    this.numTries = 0;
  }

  public String getMediaPath() {
    return media.getMediaPath();
  }

  public String getName() {
    return media.getTitle();
  }

  public String getType() {
    // TODO: 21/6/18 Handle all types of media
    return "video";
  }

  public String getHash() {
    // TODO: 21/6/18 Generate hash
    return "asdsfdsgkñdfmkcñdlfglñksdlñ";
  }

  public String getDate() {
    // TODO: 21/6/18 Get date from system
    return new Date().toString();
  }

  public int getNumTries() {
    return numTries;
  }

  public void incrementNumTries() {
    numTries++;
  }

  public boolean isAcceptedUploadMobileNetwork() {
    // TODO: 21/6/18 Implement mobile network upload politic
    return false;
  }
}
