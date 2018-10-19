package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;

/**
 * Created by alvaro on 13/12/17.
 */

public class GetCameraSettingsUseCase {

  private CameraSettingsDataSource cameraSettingsRepository;

  public GetCameraSettingsUseCase(CameraSettingsDataSource cameraSettingsRepository) {
    this.cameraSettingsRepository = cameraSettingsRepository;
  }

  public int getCameraIdSelected() {
    return cameraSettingsRepository.getCameraSettings().getCameraIdSelected();
  }

}
