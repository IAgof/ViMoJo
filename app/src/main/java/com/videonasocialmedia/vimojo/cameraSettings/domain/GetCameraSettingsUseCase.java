package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;

/**
 * Created by alvaro on 13/12/17.
 */

public class GetCameraSettingsUseCase {

  private CameraSettingsRepository cameraSettingsRepository;

  public GetCameraSettingsUseCase(CameraSettingsRepository cameraSettingsRepository) {
    this.cameraSettingsRepository = cameraSettingsRepository;
  }

  public int getCameraIdSelected() {
    return cameraSettingsRepository.getCameraSettings().getCameraIdSelected();
  }

}
