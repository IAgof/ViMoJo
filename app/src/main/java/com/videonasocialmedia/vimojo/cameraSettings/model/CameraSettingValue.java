package com.videonasocialmedia.vimojo.cameraSettings.model;

/**
 * Created by ruth on 15/11/17.
 */
public class CameraSettingValue {
  private boolean isSelected;
  private int id;
  private String name;

  public CameraSettingValue(int id, String name, boolean isSelected) {
    this.id = id;
    this.name = name;
    this.isSelected = isSelected;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public boolean isSelected() {
    return isSelected;
  }
}