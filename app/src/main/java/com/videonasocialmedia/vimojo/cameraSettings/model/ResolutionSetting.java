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
 * ResolutionSetting
 * Model for video resolution selected by user and values supported by hardware device
 * This model is intended for record area.
 */
public class ResolutionSetting {
  public static final String CAMERA_SETTING_RESOLUTION_H_720 = "720p";
  public static final String CAMERA_SETTING_RESOLUTION_H_1080 = "1080p";
  public static final String CAMERA_SETTING_RESOLUTION_H_2160 = "4k";
  public static final String CAMERA_SETTING_RESOLUTION_V_720 = "V_720p";
  public static final String CAMERA_SETTING_RESOLUTION_V_1080 = "V_1080p";
  public static final String CAMERA_SETTING_RESOLUTION_V_2160 = "V_4K";
  public static final int CAMERA_SETTING_RESOLUTION_720_BACK_ID = 720;
  public static final int CAMERA_SETTING_RESOLUTION_720_FRONT_ID = 721;
  public static final int CAMERA_SETTING_RESOLUTION_1080_BACK_ID = 1080;
  public static final int CAMERA_SETTING_RESOLUTION_1080_FRONT_ID = 1081;
  public static final int CAMERA_SETTING_RESOLUTION_2160_BACK_ID = 2160;
  public static final int CAMERA_SETTING_RESOLUTION_2160_FRONT_ID = 2161;
  private String resolution;
  private HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();

  private HashMap<String, Integer> backCameraResolutionIdsMap;

  public ResolutionSetting(String resolution, HashMap<Integer, Boolean> resolutionsSupportedMap) {
    this.resolution = resolution;
    this.resolutionsSupportedMap = resolutionsSupportedMap;
    initResolutionIdsMap();
  }

  private void initResolutionIdsMap() {
    this.backCameraResolutionIdsMap = new HashMap<>();
    backCameraResolutionIdsMap.put(CAMERA_SETTING_RESOLUTION_H_720,
            CAMERA_SETTING_RESOLUTION_720_BACK_ID);
    backCameraResolutionIdsMap.put(CAMERA_SETTING_RESOLUTION_H_1080,
            CAMERA_SETTING_RESOLUTION_1080_BACK_ID);
    backCameraResolutionIdsMap.put(CAMERA_SETTING_RESOLUTION_H_2160,
            CAMERA_SETTING_RESOLUTION_2160_BACK_ID);
    backCameraResolutionIdsMap.put(CAMERA_SETTING_RESOLUTION_V_720,
        CAMERA_SETTING_RESOLUTION_720_BACK_ID);
    backCameraResolutionIdsMap.put(CAMERA_SETTING_RESOLUTION_V_1080,
        CAMERA_SETTING_RESOLUTION_1080_BACK_ID);
    backCameraResolutionIdsMap.put(CAMERA_SETTING_RESOLUTION_V_2160,
        CAMERA_SETTING_RESOLUTION_2160_BACK_ID);
  }

  public String getResolution() {
    return resolution;
  }

  public boolean isSupportedByDeviceBackFacingCamera() {
    Integer resolutionId = backCameraResolutionIdsMap.get(resolution);
    return resolutionId == null ? false : resolutionsSupportedMap.get(resolutionId);
  }

  public void setResolutionSetting(String resolutionSetting) {
    this.resolution = resolutionSetting;
  }

  public HashMap<Integer, Boolean> getResolutionsSupportedMap() {
    return resolutionsSupportedMap;
  }

  public boolean deviceSupports(int resolutionId) {
    return resolutionsSupportedMap.get(resolutionId);
  }

  public HashMap<String, Integer> getBackCameraResolutionIdsMap() {
    return backCameraResolutionIdsMap;
  }
}
