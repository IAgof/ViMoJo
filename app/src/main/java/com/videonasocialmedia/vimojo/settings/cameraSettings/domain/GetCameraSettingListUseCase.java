package com.videonasocialmedia.vimojo.settings.cameraSettings.domain;

import android.content.Context;

import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 14/11/17.
 */

public class GetCameraSettingListUseCase {
  private Context context;

  @Inject
  public GetCameraSettingListUseCase(Context context) {
    this.context = context;
  }

  public List<CameraSettingsItem> getCameraSettingsList() {

    List <CameraSettingsItem> preferenceList = new ArrayList();

    List<String> optionList = new ArrayList();
    optionList.add("Camera Pro");
    optionList.add("Camera Basic");
    preferenceList.add(new CameraSettingsItem ("Camera Pro/Basic", optionList));

    optionList = new ArrayList();
    optionList.add("720");
    optionList.add("1080");
    optionList.add("4K");

    preferenceList.add(new CameraSettingsItem ("Resolution", optionList));

    optionList = new ArrayList();
    optionList.add("24");
    optionList.add("25");
    optionList.add("30");

    preferenceList.add(new CameraSettingsItem ("FrameRate", optionList));

    return preferenceList;
  }



}
