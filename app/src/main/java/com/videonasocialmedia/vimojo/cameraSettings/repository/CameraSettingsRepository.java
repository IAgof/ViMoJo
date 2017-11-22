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

  CameraSettings getCameraPreferences();

  void setResolutionPreferencesSupported(ResolutionSetting resolutionSetting);

  void setFrameRatePreferencesSupported(FrameRateSetting frameRateSetting);

  void setInterfaceProSelected(boolean interfaceProSelected);

  void setResolutionPreference(String resolution);

  void setFrameRatePreference(String frameRate);

  void setQualityPreference(String quality);

  void createCameraPref(CameraSettings defaultCameraSettings);
}
