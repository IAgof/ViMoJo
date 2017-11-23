/*
 * Copyright (C) 2017 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.cameraSettings.model;

import java.util.HashMap;

/**
 * Created by alvaro on 14/11/17.
 */

public class FrameRateSetting {

  private String frameRate;
  private HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();

  public FrameRateSetting(String frameRate, HashMap<Integer, Boolean> frameRatesSupportedMap) {
    this.frameRate = frameRate;
    this.frameRatesSupportedMap = frameRatesSupportedMap;
  }

  public String getFrameRate() {
    return frameRate;
  }

  public void setFrameRateSetting(String frameRateSetting) {
    this.frameRate = frameRateSetting;
  }

  public HashMap<Integer, Boolean> getFrameRatesSupportedMap() {
    return frameRatesSupportedMap;
  }
}
