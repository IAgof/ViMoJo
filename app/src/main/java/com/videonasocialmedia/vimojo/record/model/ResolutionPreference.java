package com.videonasocialmedia.vimojo.record.model;

/**
 * Created by alvaro on 14/11/17.
 */

public class ResolutionPreference {
  private String resolution;
  private boolean resolutionBack720pSupported;
  private boolean resolutionBack1080pSupported;
  private boolean resolutionBack2160pSupported;
  private boolean resolutionFront720pSupported;
  private boolean resolutionFront1080pSupported;
  private boolean resolutionFront2160pSupported;

  public ResolutionPreference(String resolution, boolean resolutionBack720pSupported,
                              boolean resolutionBack1080pSupported,
                              boolean resolutionBack2160pSupported,
                              boolean resolutionFront720pSupported,
                              boolean resolutionFront1080pSupported,
                              boolean resolutionFront2160pSupported) {
    this.resolution = resolution;
    this.resolutionBack720pSupported = resolutionBack720pSupported;
    this.resolutionBack1080pSupported = resolutionBack1080pSupported;
    this.resolutionBack2160pSupported = resolutionBack2160pSupported;
    this.resolutionFront720pSupported = resolutionFront720pSupported;
    this.resolutionFront1080pSupported = resolutionFront1080pSupported;
    this.resolutionFront2160pSupported = resolutionFront2160pSupported;
  }

  public String getResolution() {
    return resolution;
  }

  public boolean isResolutionBack720pSupported() {
    return resolutionBack720pSupported;
  }

  public boolean isResolutionBack1080pSupported() {
    return resolutionBack1080pSupported;
  }

  public boolean isResolutionBack2160pSupported() {
    return resolutionBack2160pSupported;
  }

  public boolean isResolutionFront720pSupported() {
    return resolutionFront720pSupported;
  }

  public boolean isResolutionFront1080pSupported() {
    return resolutionFront1080pSupported;
  }

  public boolean isResolutionFront2160pSupported() {
    return resolutionFront2160pSupported;
  }
}
