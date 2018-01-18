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

  void setResolutionSettingSupported(CameraSettings cameraSettings,
                                     ResolutionSetting resolutionSetting);

  void setFrameRateSettingSupported(CameraSettings cameraSettings,
                                    FrameRateSetting frameRateSetting);

  void setResolutionSetting(CameraSettings cameraSettings, String resolution);

  void setFrameRateSetting(CameraSettings cameraSettings, String frameRate);

  void setQualitySetting(CameraSettings cameraSettings, String quality);

  void createCameraSetting(CameraSettings defaultCameraSettings);

  void setInterfaceSelected(CameraSettings cameraSettings, String interfaceSelected);

  void setCameraIdSelected(CameraSettings cameraSettings, int cameraIdSelected);
}
