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
 * Configure for camera interface selected and camera recording parameters.
 * User preferences saved on device.
 * Resolution and frame rate need to be checked if are compatible with hardware user device.
 */

public class CameraSettings {

  private ResolutionSetting resolutionSetting;
  private FrameRateSetting frameRateSetting;
  private String quality;
  private String interfaceSelected;

  public CameraSettings(ResolutionSetting resolutionSetting, FrameRateSetting
          frameRateSetting, String quality, String interfaceSelected) {
    this.resolutionSetting = resolutionSetting;
    this.frameRateSetting = frameRateSetting;
    this.quality = quality;
    this.interfaceSelected = interfaceSelected;
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

  public void setFrameRatePreferences(FrameRateSetting frameRateSetting) {
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
}
