package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingsPackage;

import java.util.List;


public interface CameraSettingsView {
  void showCameraSettingsList(List<CameraSettingsPackage> list);

  void showError(String error);
}
