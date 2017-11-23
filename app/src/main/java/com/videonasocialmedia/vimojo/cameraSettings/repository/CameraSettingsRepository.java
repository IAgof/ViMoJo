package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.repository.Repository;

/**
 * Created by alvaro on 14/11/17.
 */

public interface CameraSettingsRepository extends Repository<CameraSettings> {

  void update(CameraSettings item);

  CameraSettings getCameraSettings();

  void setResolutionSettingSupported(ResolutionSetting resolutionSetting);

  void setFrameRateSettingSupported(FrameRateSetting frameRateSetting);

  void setInterfaceProSelected(boolean interfaceProSelected);

  void setResolutionSetting(String resolution);

  void setFrameRateSetting(String frameRate);

  void setQualitySetting(String quality);

  void createCameraPref(CameraSettings defaultCameraSettings);
}
