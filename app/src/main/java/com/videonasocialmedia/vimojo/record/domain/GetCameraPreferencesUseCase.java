package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.camera.CameraRepository;

/**
 * Created by alvaro on 14/11/17.
 */

public class GetCameraPreferencesUseCase {

  private final CameraRepository cameraRepository;

  public GetCameraPreferencesUseCase(CameraRepository cameraRepository) {
    this.cameraRepository = cameraRepository;
  }

  public ResolutionPreference getResolutionPreference() {
    return cameraRepository.getCameraPreferences().getResolutionPreference();
  }

  public FrameRatePreference getFrameRatePreference() {
    return cameraRepository.getCameraPreferences().getFrameRatePreference();
  }

  public String getQualityPreference() {
    return cameraRepository.getCameraPreferences().getQuality();
  }

  public boolean isInterfaceProSelected() {
    return cameraRepository.getCameraPreferences().isInterfaceProSelected();
  }
}
