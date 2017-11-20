package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraPrefRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 20/11/17.
 */

public class UpdateCameraPreferencesUseCaseTest {

  @InjectMocks UpdateCameraPreferencesUseCase injectedUpdateCameraPreferencesUseCase;
  @Mock CameraPrefRepository mockedCameraPrefRepository;

  CameraPreferences cameraPreferences;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    cameraPreferences = mockedCameraPrefRepository.getCameraPreferences();
  }

  @Test
  public void setResolutionPreferencesSupportedUpdateRepository() {
    String defaultResolution = Constants.DEFAULT_CAMERA_PREF_RESOLUTION;
    boolean resolutionBack720pSupported = true;
    boolean resolutionBack1080pSupported = true;
    boolean resolutionBack2160pSupported = false;
    boolean resolutionFront720pSupported = true;
    boolean resolutionFront1080pSupported = true;
    boolean resolutionFront2160pSupported = false;
    ResolutionPreference resolutionPreference = new ResolutionPreference(defaultResolution,
        resolutionBack720pSupported, resolutionBack1080pSupported,
        resolutionBack2160pSupported, resolutionFront720pSupported,
        resolutionFront1080pSupported, resolutionFront2160pSupported);
    injectedUpdateCameraPreferencesUseCase.setResolutionPreferencesSupported(resolutionPreference);

    verify(mockedCameraPrefRepository).update(cameraPreferences);
  }

  @Test
  public void setResolutionPreferencesUpdateRepository() {
    String resolution = Constants.DEFAULT_CAMERA_PREF_RESOLUTION;

    injectedUpdateCameraPreferencesUseCase.setResolutionPreference(resolution);

    verify(mockedCameraPrefRepository).update(cameraPreferences);
  }

  @Test
  public void setFrameRatePreferencesSupportedUpdateRepository() {
    String defaultFrameRate = Constants.DEFAULT_CAMERA_PREF_FRAME_RATE;
    boolean frameRate24FpsSupported = false;
    boolean frameRate25FpsSupported = false;
    boolean frameRate30FpsSupported = true;
    FrameRatePreference frameRatePreference = new FrameRatePreference(defaultFrameRate,
        frameRate24FpsSupported, frameRate25FpsSupported, frameRate30FpsSupported);

    injectedUpdateCameraPreferencesUseCase.setFrameRatePreferencesSupported(frameRatePreference);

    verify(mockedCameraPrefRepository).update(cameraPreferences);
  }

  @Test
  public void setFrameRatePreferenceUpdateRepository() {
    String frameRate = Constants.DEFAULT_CAMERA_PREF_FRAME_RATE;

    injectedUpdateCameraPreferencesUseCase.setFrameRatePreference(frameRate);

    verify(mockedCameraPrefRepository).update(cameraPreferences);
  }

  @Test
  public void setInterfaceProSelectedUpdateRepository() {
    boolean interfaceProSelected = false;

    injectedUpdateCameraPreferencesUseCase.setInterfaceProSelected(interfaceProSelected);

    verify(mockedCameraPrefRepository).update(cameraPreferences);
  }

  @Test
  public void setQualityPreferenceUpdateRepository() {
    String quality = Constants.DEFAULT_CAMERA_PREF_QUALITY;

    injectedUpdateCameraPreferencesUseCase.setQualityPreference(quality);

    verify(mockedCameraPrefRepository).update(cameraPreferences);
  }

  @Test
  public void createCameraPreferenceUpdateRepository() {
    String defaultResolution = Constants.DEFAULT_CAMERA_PREF_RESOLUTION;
    boolean resolutionBack720pSupported = true;
    boolean resolutionBack1080pSupported = true;
    boolean resolutionBack2160pSupported = false;
    boolean resolutionFront720pSupported = true;
    boolean resolutionFront1080pSupported = true;
    boolean resolutionFront2160pSupported = false;
    ResolutionPreference resolutionPreference = new ResolutionPreference(defaultResolution,
        resolutionBack720pSupported, resolutionBack1080pSupported,
        resolutionBack2160pSupported, resolutionFront720pSupported,
        resolutionFront1080pSupported, resolutionFront2160pSupported);
    String defaultFrameRate = Constants.DEFAULT_CAMERA_PREF_FRAME_RATE;
    boolean frameRate24FpsSupported = false;
    boolean frameRate25FpsSupported = false;
    boolean frameRate30FpsSupported = true;
    FrameRatePreference frameRatePreference = new FrameRatePreference(defaultFrameRate,
        frameRate24FpsSupported, frameRate25FpsSupported, frameRate30FpsSupported);
    String quality = Constants.DEFAULT_CAMERA_PREF_QUALITY;
    boolean interfaceProSelected = false;
    CameraPreferences cameraPreferences = new CameraPreferences(resolutionPreference,
        frameRatePreference, quality, interfaceProSelected);

    injectedUpdateCameraPreferencesUseCase.createCameraPref(cameraPreferences);

    verify(mockedCameraPrefRepository).update(cameraPreferences);
  }
}
