package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingViewModel;

import java.util.List;


public interface CameraSettingsView {
  void showCameraSettingsList(List<CameraSettingViewModel> list);
}
