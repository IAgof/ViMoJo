package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraPrefRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 20/11/17.
 */

public class GetCameraPreferencesUseCase {

  private CameraPrefRepository cameraPrefRepository;

  @Inject
  public GetCameraPreferencesUseCase(CameraPrefRepository cameraPrefRepository) {
    this.cameraPrefRepository = cameraPrefRepository;
  }

  public boolean isInterfaceProSelected() {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    return cameraPreferences.isInterfaceProSelected();
  }
}
