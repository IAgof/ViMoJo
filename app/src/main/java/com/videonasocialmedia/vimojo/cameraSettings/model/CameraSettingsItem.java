package com.videonasocialmedia.vimojo.settings.cameraSettings.model;

/**
 * Created by ruth on 15/11/17.
 */

public class CameraSettingsItem {

    private int id;
    private String titleCameraSettingsItem;

    public CameraSettingsItem(int id, String titleCameraSettingsItem) {
      this.id = id;
      this.titleCameraSettingsItem = titleCameraSettingsItem;
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

    public void setTitleCameraSettingsItem(String titleCameraSettingsItem) {
      this.titleCameraSettingsItem = titleCameraSettingsItem;
    }
  }


