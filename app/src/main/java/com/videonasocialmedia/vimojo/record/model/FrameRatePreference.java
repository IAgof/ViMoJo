package com.videonasocialmedia.vimojo.record.model;

/**
 * Created by alvaro on 14/11/17.
 */

public class FrameRatePreference {

  private String frameRate;
  private boolean frameRate24FpsSupported;
  private boolean frameRate25FpsSupported;
  private boolean frameRate30FpsSupported;

  public FrameRatePreference(String frameRate, boolean frameRate24FpsSupported, boolean
                             frameRate25FpsSupported, boolean frameRate30FpsSupported) {
    this.frameRate = frameRate;
    this.frameRate24FpsSupported = frameRate24FpsSupported;
    this.frameRate25FpsSupported = frameRate25FpsSupported;
    this.frameRate30FpsSupported = frameRate30FpsSupported;
  }


  public boolean isFrameRate24FpsSupported() {
    return frameRate24FpsSupported;
  }

  public boolean isFrameRate25FpsSupported() {
    return frameRate25FpsSupported;
  }

  public boolean isFrameRate30FpsSupported() {
    return frameRate30FpsSupported;
  }

  public String getFrameRate() {
    return frameRate;
  }

  public void setFrameRatePreference(String frameRatePreference) {
    this.frameRate = frameRatePreference;
  }
}
