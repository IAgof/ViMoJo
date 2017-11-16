package com.videonasocialmedia.vimojo.cameraSettings.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 14/11/17.
 */

public class CameraSettingsPackage {

  private String titleCameraSettingsPackage;
  private List<CameraSettingsItem> cameraSettingsItemList;
  private boolean isAvailable;

  public CameraSettingsPackage(String titleCameraSettingsPackage, List<CameraSettingsItem>
      optionList, boolean isAvailable) {
    this.titleCameraSettingsPackage = titleCameraSettingsPackage;
    this.cameraSettingsItemList = optionList;
    this.isAvailable = isAvailable;
  }

  public List<CameraSettingsItem> getPreferencesList() {
    return cameraSettingsItemList;
  }

  public String getTitleCameraSettingsPackage() {
    return titleCameraSettingsPackage;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

}
