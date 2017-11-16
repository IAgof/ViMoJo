package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.camerapref.CameraPrefRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/11/17.
 */

public class GetCameraPreferencesUseCase {

  protected CameraPrefRepository cameraPrefRepository;

  @Inject
  public GetCameraPreferencesUseCase(CameraPrefRepository cameraPrefRepository) {
    this.cameraPrefRepository = cameraPrefRepository;
  }

  public ResolutionPreference getResolutionPreference() {
    return cameraPrefRepository.getCameraPreferences().getResolutionPreference();
  }

  public FrameRatePreference getFrameRatePreference() {
    return cameraPrefRepository.getCameraPreferences().getFrameRatePreference();
  }

  public String getQualityPreference() {
    return cameraPrefRepository.getCameraPreferences().getQuality();
  }

  public boolean isInterfaceProSelected() {
    return cameraPrefRepository.getCameraPreferences().isInterfaceProSelected();
  }
}
