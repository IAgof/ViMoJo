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

public class ResolutionSetting {
  private String resolution;
  private HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();

  public ResolutionSetting(String resolution, HashMap<Integer, Boolean> resolutionsSupportedMap) {
    this.resolution = resolution;
    this.resolutionsSupportedMap = resolutionsSupportedMap;
  }

  public String getResolution() {
    return resolution;
  }

  public void setResolutionPreference(String resolutionPreference) {
    this.resolution = resolutionPreference;
  }

  public HashMap<Integer, Boolean> getResolutionsSupportedMap() {
    return resolutionsSupportedMap;
  }
}
