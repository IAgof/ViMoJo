/*
 * Copyright (C) 2017 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.cameraSettings.model;

/**
 * Created by alvaro on 14/11/17.
 *
 * Model Camera settings.
 * This model stores user preferences for camera interface selected and camera recording parameters
 * and its intended for use in recording area.
 * User preferences are saved on device in settings repository.
 * Resolution and frame rate need to be checked if are compatible with hardware user device.
 */
public class CameraSettings {
  public static final String CAMERA_SETTING_QUALITY_16 = "16 Mbps";
  public static final String CAMERA_SETTING_QUALITY_32 = "32 Mbps";
  public static final String CAMERA_SETTING_QUALITY_50 = "50 Mbps";
  public static final int CAMERA_SETTING_QUALITY_16_ID = 16;
  public static final int CAMERA_SETTING_QUALITY_32_ID = 32;
  public static final int CAMERA_SETTING_QUALITY_50_ID = 50;
  private ResolutionSetting resolutionSetting;
  private FrameRateSetting frameRateSetting;
  private String quality;
  private String interfaceSelected;
  private int cameraIdSelected;

  public CameraSettings(ResolutionSetting resolutionSetting, FrameRateSetting
          frameRateSetting, String quality, String interfaceSelected, int cameraIdSelected) {
    this.resolutionSetting = resolutionSetting;
    this.frameRateSetting = frameRateSetting;
    this.quality = quality;
    this.interfaceSelected = interfaceSelected;
    this.cameraIdSelected = cameraIdSelected;
  }

  public String getInterfaceSelected() {
    return interfaceSelected;
  }

  public String getQuality() {
    return quality;
  }

  public ResolutionSetting getResolutionSetting() {
    return resolutionSetting;
  }

  public void setResolutionSetting(ResolutionSetting resolutionSetting) {
    this.resolutionSetting = resolutionSetting;
  }

  public FrameRateSetting getFrameRateSetting() {
    return frameRateSetting;
  }

  public void setFrameRateSetting(FrameRateSetting frameRateSetting) {
    this.frameRateSetting = frameRateSetting;
  }

  public void setQuality(String quality) {
    this.quality = quality;
  }

  public String getResolutionSettingValue() {
    return resolutionSetting.getResolution();
  }

  public String getFrameRateSettingValue() {
    return frameRateSetting.getFrameRate();
  }

  public void setInterfaceSelected(String interfaceSelected) {
    this.interfaceSelected = interfaceSelected;
  }

  public int getCameraIdSelected() {
    return cameraIdSelected;
  }

  public void setCameraIdSelected(int cameraIdSelected) {
    this.cameraIdSelected = cameraIdSelected;
  }
}
