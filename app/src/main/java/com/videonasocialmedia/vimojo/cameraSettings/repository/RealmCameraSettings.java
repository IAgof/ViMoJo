package com.videonasocialmedia.vimojo.cameraSettings.repository;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alvaro on 14/11/17.
 */

public class RealmCameraSettings extends RealmObject {

  @PrimaryKey
  @Required
  public String cameraSettingsId = "RealmCameraSettings";

  public boolean interfaceProSelected;
  @Required
  public String resolution;
  @Required
  public String quality;
  @Required
  public String frameRate;

  public boolean resolutionBack720pSupported;
  public boolean resolutionBack1080pSupported;
  public boolean resolutionBack2160pSupported;
  public boolean resolutionFront720pSupported;
  public boolean resolutionFront1080pSupported;
  public boolean resolutionFront2160pSupported;

  public boolean frameRate24FpsSupported;
  public boolean frameRate25FpsSupported;
  public boolean frameRate30FpsSupported;

  public RealmCameraSettings() {

  }

  public RealmCameraSettings(String cameraSettingsId, boolean interfaceProSelected, String resolution,
                             String quality, String frameRate, boolean resolutionBack720pSupported,
                             boolean resolutionBack1080pSupported, boolean resolutionBack2160pSupported,
                             boolean resolutionFront720pSupported, boolean resolutionFront1080pSupported,
                             boolean resolutionFront2160pSupported, boolean frameRate24FpsSupported,
                             boolean frameRate25FpsSupported, boolean frameRate30FpsSupported) {

    this.cameraSettingsId = cameraSettingsId;
    this.interfaceProSelected = interfaceProSelected;
    this.resolution = resolution;
    this.quality = quality;
    this.frameRate = frameRate;
    this.resolutionBack720pSupported = resolutionBack720pSupported;
    this.resolutionBack1080pSupported = resolutionBack1080pSupported;
    this.resolutionBack2160pSupported = resolutionBack2160pSupported;
    this.resolutionFront720pSupported = resolutionFront720pSupported;
    this.resolutionFront1080pSupported = resolutionFront1080pSupported;
    this.resolutionFront2160pSupported = resolutionFront2160pSupported;
    this.frameRate24FpsSupported = frameRate24FpsSupported;
    this.frameRate25FpsSupported = frameRate25FpsSupported;
    this.frameRate30FpsSupported = frameRate30FpsSupported;
  }
}
