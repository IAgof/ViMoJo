package com.videonasocialmedia.vimojo.cameraSettings.model;

import java.util.List;

/**
 * Created by ruth on 14/11/17.
 *
 * View model for a camera setting shown in camera preferences activity {@link com.videonasocialmedia.vimojo.cameraSettings.presentation.view.activity.CameraSettingsActivity}.
 * This model represents each setting a user can change for
 */
public class CameraSettingViewModel {
  private String settingTitle;
  private List<CameraSettingValue> cameraSettingValueList;
  private boolean isAvailable;

  public CameraSettingViewModel(String settingTitle, List<CameraSettingValue> optionList,
                                boolean isAvailable) {
    this.settingTitle = settingTitle;
    this.cameraSettingValueList = optionList;
    this.isAvailable = isAvailable;
  }

  public List<CameraSettingValue> getSettingsList() {
    return cameraSettingValueList;
  }

  public String getSettingTitle() {
    return settingTitle;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }
}
