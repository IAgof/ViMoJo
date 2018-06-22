/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

import com.videonasocialmedia.videonamediaframework.model.media.*;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.Date;

/**
 * Created by alvaro on 21/6/18.
 */

public class AssetUpload {

  private final String projectId;
  private final String mediaPath;
  private String name;
  private String type;
  private String hash;
  private String date;
  private int numTries;
  public final static int MAX_NUM_TRIES_UPLOAD = 3;

  public AssetUpload(String projectId, Media media) {
    this.projectId = projectId;
    this.mediaPath = media.getMediaPath();
    this.name = media.getTitle();
    if (media instanceof Video) {
      this.type = "video";
    } else {
      if (media instanceof Music) {
        if (media.getTitle().equals(com.videonasocialmedia.vimojo.utils.Constants
            .MUSIC_AUDIO_VOICEOVER_TITLE)) {
          this.type = "voiceOver";
        } else {
          this.type = "music";
        }
      }
    }
    this.numTries = 0;
  }

  public String getMediaPath() {
    return mediaPath;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    // TODO: 22/6/18 Handle error
    if (type == null) {
      return "video";
    }
    return type;
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

  public String getId() {
    return projectId;
  }
}
