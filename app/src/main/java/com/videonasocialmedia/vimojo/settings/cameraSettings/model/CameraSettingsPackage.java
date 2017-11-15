package com.videonasocialmedia.vimojo.settings.cameraSettings.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 14/11/17.
 */

public class CameraSettingsPackage {

  private String  titlePreferencePackage;
  private List<String> preferencesList;
  private boolean isAvailable;

  public CameraSettingsPackage(String titlePreferencePackage, List<String> optionList, boolean isAvailable) {
    this.titlePreferencePackage = titlePreferencePackage;
    this.preferencesList = optionList;
    this.isAvailable = isAvailable;
  }

  public List<String> getPreferencesList() {
    return preferencesList;
  }

  public void setPreferencesList(ArrayList<String> preferencesList) {
    this.preferencesList = preferencesList;
  }

  public String getTitlePreferencePackage() {
    return titlePreferencePackage;
  }

  public void setTitlePreferencePackage(String titlePreferencePackage) {
    this.titlePreferencePackage = titlePreferencePackage;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

}
