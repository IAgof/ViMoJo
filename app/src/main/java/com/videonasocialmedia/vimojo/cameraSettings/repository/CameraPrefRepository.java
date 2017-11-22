package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.Repository;

/**
 * Created by alvaro on 14/11/17.
 */

public interface CameraPrefRepository extends Repository<CameraPreferences> {

  void update(CameraPreferences item);

  CameraPreferences getCameraPreferences();

  void setResolutionPreferencesSupported(ResolutionPreference resolutionPreference);

  void setFrameRatePreferencesSupported(FrameRatePreference frameRatePreference);

  void setInterfaceProSelected(boolean interfaceProSelected);

  void setResolutionPreference(String resolution);

  void setFrameRatePreference(String frameRate);

  void setQualityPreference(String quality);

  void createCameraPref(CameraPreferences defaultCameraPreferences);
}
