/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.domain.model;

/**
 * Created by alvaro on 30/8/18.
 */

/**
 * User features representation. Model what features have this user activated.
 * There are not any reference to pricing or plans activated.
 * Features added alphabetically.
 */
public class UserFeatures {

  private boolean forceWatermark;
  private boolean ftp;
  private boolean showAds;
  private boolean vimojoPlatform;
  private boolean vimojoStore;
  private boolean voiceOver;
  private boolean watermark;

  public UserFeatures(boolean forceWatermark, boolean ftp, boolean showAds, boolean vimojoPlatform,
                      boolean vimojoStore, boolean voiceOver, boolean watermark) {
    this.forceWatermark = forceWatermark;
    this.ftp = ftp;
    this.showAds = showAds;
    this.vimojoPlatform = vimojoPlatform;
    this.vimojoStore = vimojoStore;
    this.voiceOver = voiceOver;
    this.watermark = watermark;
  }

  public boolean isForceWatermark() {
    return forceWatermark;
  }

  public boolean isFtp() {
    return ftp;
  }

  public boolean isShowAds() {
    return showAds;
  }

  public boolean isVimojoPlatform() {
    return vimojoPlatform;
  }

  public boolean isVimojoStore() {
    return vimojoStore;
  }

  public boolean isVoiceOver() {
    return voiceOver;
  }

  public boolean isWatermark() {
    return watermark;
  }

}
