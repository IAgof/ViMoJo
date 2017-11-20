/*
 * Copyright (C) 2017 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraPrefRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/11/17.
 */

public class UpdateCameraPreferencesUseCase {

  protected CameraPrefRepository cameraPrefRepository;

  @Inject
  public UpdateCameraPreferencesUseCase(CameraPrefRepository cameraPrefRepository) {
    this.cameraPrefRepository = cameraPrefRepository;
  }

  public void createCameraPref(CameraPreferences cameraPreferences) {
    cameraPrefRepository.update(cameraPreferences);
  }

  public void setResolutionPreferencesSupported(ResolutionPreference resolutionPreference) {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    cameraPreferences.setResolutionPreference(resolutionPreference);
    cameraPrefRepository.update(cameraPreferences);
  }

  public void setFrameRatePreferencesSupported(FrameRatePreference frameRatePreference) {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    cameraPreferences.setFrameRatePreferences(frameRatePreference);
    cameraPrefRepository.update(cameraPreferences);
  }

  public void setInterfaceProSelected(boolean interfaceProSelected) {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    cameraPreferences.setInterfaceProSelected(interfaceProSelected);
    cameraPrefRepository.update(cameraPreferences);
  }

  public void setQualityPreference(String qualityPreference) {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    cameraPreferences.setQuality(qualityPreference);
    cameraPrefRepository.update(cameraPreferences);
  }

  public void setResolutionPreference(String resolutionPreference) {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    cameraPreferences.getResolutionPreference().setResolutionPreference(resolutionPreference);
    cameraPrefRepository.update(cameraPreferences);
  }

  public void setFrameRatePreference(String frameRatePreference) {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    cameraPreferences.getFrameRatePreference().setFrameRatePreference(frameRatePreference);
    cameraPrefRepository.update(cameraPreferences);
  }
}
