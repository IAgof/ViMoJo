package com.videonasocialmedia.vimojo.settings.cameraSettings.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 14/11/17.
 */

public class CameraSettingsItem {

  private String  titlePreferencePackage;
  private List<String> preferencesList;

  public CameraSettingsItem(String titlePreferencePackage, List<String> optionList) {
    this.titlePreferencePackage = titlePreferencePackage;
    this.preferencesList = optionList;
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

}
