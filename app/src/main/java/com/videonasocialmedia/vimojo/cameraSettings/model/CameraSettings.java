/*
 * Copyright (C) 2017 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.cameraSettings.model;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraSettings {

  private ResolutionSetting resolutionSetting;
  private FrameRateSetting frameRateSetting;
  private boolean interfaceProSelected;
  private String quality;

  public CameraSettings(ResolutionSetting resolutionSetting, FrameRateSetting
          frameRateSetting, String quality, boolean interfaceProSelected) {
    this.resolutionSetting = resolutionSetting;
    this.frameRateSetting = frameRateSetting;
    this.quality = quality;
    this.interfaceProSelected = interfaceProSelected;
  }

  public boolean isInterfaceProSelected() {
    return interfaceProSelected;
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

  public void setInterfaceProSelected(boolean interfaceProSelected) {
    this.interfaceProSelected = interfaceProSelected;
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
}
