package com.videonasocialmedia.vimojo.cameraSettings.model;

import java.util.List;

/**
 * Created by ruth on 14/11/17.
 */

public class CameraSettingSelectable {

  private String titleCameraSettingsSelectable;
  private List<CameraSettingItems> cameraSettingItemsList;
  private boolean isAvailable;

  public CameraSettingSelectable(String titleCameraSettingsSelectable, List<CameraSettingItems>
          optionList, boolean isAvailable) {
    this.titleCameraSettingsSelectable = titleCameraSettingsSelectable;
    this.cameraSettingItemsList = optionList;
    this.isAvailable = isAvailable;
  }

  public List<CameraSettingItems> getPreferencesList() {
    return cameraSettingItemsList;
  }

  public String getTitleCameraSettingsSelectable() {
    return titleCameraSettingsSelectable;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

}
