package com.videonasocialmedia.vimojo.cameraSettings.model;

/**
 * Created by ruth on 15/11/17.
 */

public class CameraSettingsItem {

  private boolean isSelected;
  private int id;
  private String titleCameraSettingsItem;

  public CameraSettingsItem(int id, String titleCameraSettingsItem, boolean isSelected) {
    this.id = id;
    this.titleCameraSettingsItem = titleCameraSettingsItem;
    this.isSelected = isSelected;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitleCameraSettingsItem() {
    return titleCameraSettingsItem;
  }

  public boolean isSelected() {
    return isSelected;
  }
}


