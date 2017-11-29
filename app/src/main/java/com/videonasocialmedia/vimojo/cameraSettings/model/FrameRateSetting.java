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
 *
 * FrameRateSetting
 * Model for frame rate selected by user and values supported by hardware device
 */
public class FrameRateSetting {
  public static final String CAMERA_SETTING_FRAME_RATE_24 = "24 fps";
  public static final String CAMERA_SETTING_FRAME_RATE_25 = "25 fps";
  public static final String CAMERA_SETTING_FRAME_RATE_30 = "30 fps";
  public static final int CAMERA_SETTING_FRAME_RATE_24_ID = 24;
  public static final int CAMERA_SETTING_FRAME_RATE_25_ID = 25;
  public static final int CAMERA_SETTING_FRAME_RATE_30_ID = 30;
  private String frameRate;
  private HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();
  private HashMap<String, Integer> frameRateIdsMap;

  public FrameRateSetting(String frameRate, HashMap<Integer, Boolean> frameRatesSupportedMap) {
    this.frameRate = frameRate;
    this.frameRatesSupportedMap = frameRatesSupportedMap;
    initFrameRateIdsMap();
  }

  private void initFrameRateIdsMap() {
    this.frameRateIdsMap = new HashMap<>();
    this.frameRateIdsMap.put(CAMERA_SETTING_FRAME_RATE_24, CAMERA_SETTING_FRAME_RATE_24_ID);
    this.frameRateIdsMap.put(CAMERA_SETTING_FRAME_RATE_25, CAMERA_SETTING_FRAME_RATE_25_ID);
    this.frameRateIdsMap.put(CAMERA_SETTING_FRAME_RATE_30, CAMERA_SETTING_FRAME_RATE_30_ID);
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

  public boolean isSupportedByDevice() {
    Integer frameRateId = frameRateIdsMap.get(frameRate);
    return frameRateId == null ? false : frameRatesSupportedMap.get(frameRateId);
  }

  public boolean deviceSupports(int frameRateId) {
    return frameRatesSupportedMap.get(frameRateId);
  }
}
