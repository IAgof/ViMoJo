package com.videonasocialmedia.vimojo.repository.camera;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alvaro on 14/11/17.
 */

public class RealmCamera extends RealmObject {

  @PrimaryKey
  public String cameraPreferenceId = "CameraPreferences";

  public boolean interfaceProSelected;
  public String resolution;
  public String quality;
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

  public RealmCamera (String cameraPreferenceId, boolean interfaceProSelected, String resolution,
                      String quality, String frameRate, boolean resolutionBack720pSupported,
                      boolean resolutionBack1080pSupported, boolean resolutionBack2160pSupported,
                      boolean resolutionFront720pSupported, boolean resolutionFront1080pSupported,
                      boolean resolutionFront2160pSupported, boolean frameRate24FpsSupported,
                      boolean frameRate25FpsSupported, boolean frameRate30FpsSupported){

    this.cameraPreferenceId = cameraPreferenceId;
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