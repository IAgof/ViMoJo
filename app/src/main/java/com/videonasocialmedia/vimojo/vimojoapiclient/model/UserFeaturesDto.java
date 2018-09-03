/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 30/8/18.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Model class for UserFeatures vimojo API calls.
 */
public class UserFeaturesDto {

  @SerializedName("forceWatermark") public boolean forceWatermark;
  @SerializedName("ftp") public boolean ftp;
  @SerializedName("showAds") public boolean showAds;
  @SerializedName("vimojoPlatform") public boolean vimojoPlatform;
  @SerializedName("vimojoStore") public boolean vimojoStore;
  @SerializedName("voiceOver") public boolean voiceOver;
  @SerializedName("watermark") public boolean watermark;

  public UserFeaturesDto() {

  }

  public boolean isFtp() {
    return ftp;
  }

  public boolean isVoiceOver() {
    return voiceOver;
  }

  public boolean isWatermark() {
    return watermark;
  }

  public boolean isForceWatermark() {
    return forceWatermark;
  }

  public boolean isVimojoStore() {
    return vimojoStore;
  }

  public boolean isVimojoPlatform() {
    return vimojoPlatform;
  }

  public boolean isShowAds() {
    return showAds;
  }

  @Override
  public String toString() {
    return "UserFeaturesDto{"
        + "forceWatermark='"
        + forceWatermark
        + '\''
        + ", ftp='"
        + ftp
        + '\''
        + ", showAds='"
        + showAds
        + '\''
        + ", vimojoPlatform='"
        + vimojoPlatform
        + '\''
        + ", vimojoStore='"
        + vimojoStore
        + '\''
        + ", voiceOver='"
        + voiceOver
        + '\''
        + ", watermark='"
        + watermark
        +
        '}';
  }



}
