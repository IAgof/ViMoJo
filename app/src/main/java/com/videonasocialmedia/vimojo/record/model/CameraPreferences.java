package com.videonasocialmedia.vimojo.record.model;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraPreferences {

  private ResolutionPreference resolutionPreference;
  private FrameRatePreference frameRatePreference;
  private boolean interfaceProSelected;
  private String quality;

  public CameraPreferences(ResolutionPreference resolutionPreference, FrameRatePreference
      frameRatePreference, String quality, boolean interfaceProSelected) {
    this.resolutionPreference = resolutionPreference;
    this.frameRatePreference = frameRatePreference;
    this.quality = quality;
    this.interfaceProSelected = interfaceProSelected;
  }

  public boolean isInterfaceProSelected() {
    return interfaceProSelected;
  }

  public String getQuality() {
    return quality;
  }

  public ResolutionPreference getResolutionPreference() {
    return resolutionPreference;
  }

  public void setResolutionPreference(ResolutionPreference resolutionPreference) {
    this.resolutionPreference = resolutionPreference;
  }

  public FrameRatePreference getFrameRatePreference() {
    return frameRatePreference;
  }

  public void setFrameRatePreferences(FrameRatePreference frameRatePreference) {
    this.frameRatePreference = frameRatePreference;
  }

  public void setInterfaceProSelected(boolean interfaceProSelected) {
    this.interfaceProSelected = interfaceProSelected;
  }

  public void setQuality(String quality) {
    this.quality = quality;
  }
}
