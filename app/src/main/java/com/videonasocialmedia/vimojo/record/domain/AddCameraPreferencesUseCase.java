package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.camerapref.CameraPrefRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/11/17.
 */

public class AddCameraPreferencesUseCase {

  protected CameraPrefRepository cameraPrefRepository;

  @Inject
  public AddCameraPreferencesUseCase(CameraPrefRepository cameraPrefRepository) {
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
