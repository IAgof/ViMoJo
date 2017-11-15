package com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.mvp.views;

import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsItem;

import java.util.List;


public interface CameraSettingsView {
  void showCameraSettingsList(List<CameraSettingsItem> list);
}
