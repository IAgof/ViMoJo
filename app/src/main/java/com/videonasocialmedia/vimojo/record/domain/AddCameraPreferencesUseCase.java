package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.camera.CameraRepository;

/**
 * Created by alvaro on 14/11/17.
 */

public class AddCameraPreferencesUseCase {

  private CameraRepository cameraRepository;

  public AddCameraPreferencesUseCase(CameraRepository cameraRepository) {
    this.cameraRepository = cameraRepository;
  }

  public void setResolutionPreference(ResolutionPreference resolutionPreference) {
    CameraPreferences cameraPreferences = cameraRepository.getCameraPreferences();
    cameraPreferences.setResolutionPreference(resolutionPreference);
    cameraRepository.update(cameraPreferences);
  }

  public void setFrameRatePreference(FrameRatePreference frameRatePreference) {
    CameraPreferences cameraPreferences = cameraRepository.getCameraPreferences();
    cameraPreferences.setFrameRatePreferences(frameRatePreference);
    cameraRepository.update(cameraPreferences);
  }
}
