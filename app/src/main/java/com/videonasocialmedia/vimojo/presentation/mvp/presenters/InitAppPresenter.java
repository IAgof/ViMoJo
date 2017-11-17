package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;

import com.videonasocialmedia.camera.utils.Camera2Settings;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.domain.CameraPreferencesUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.inject.Inject;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private final Context context;
  private final CameraPreferencesUseCase addCameraPreferenceUseCase;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;

  @Inject
  public InitAppPresenter(Context context, CreateDefaultProjectUseCase
          createDefaultProjectUseCase, CameraPreferencesUseCase cameraPreferencesUseCase) {
    this.context = context;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.addCameraPreferenceUseCase = cameraPreferencesUseCase;
  }

  public void startLoadingProject(String rootPath, String privatePath,
                                  boolean isWatermarkFeatured) {
    createDefaultProjectUseCase.loadOrCreateProject(rootPath, privatePath, isWatermarkFeatured);
  }

  public void checkCamera2ResolutionSupported() {

      Camera2Settings camera2Settings = null;
      try {
          camera2Settings = new Camera2Settings(context);
      } catch (CameraAccessException e) {
          e.printStackTrace();
          // TODO: 15/11/2017 Manage Error
          return;
      }

    String defaultResolution = Constants.DEFAULT_CAMERA_PREF_RESOLUTION;
    boolean resolutionBack720pSupported = false;
    boolean resolutionBack1080pSupported = false;
    boolean resolutionBack2160pSupported = false;
    boolean resolutionFront720pSupported = false;
    boolean resolutionFront1080pSupported = false;
    boolean resolutionFront2160pSupported = false;

    if(camera2Settings.isBackCamera720pSupported())
      resolutionBack720pSupported = true;
    if(camera2Settings.isBackCamera1080pSupported())
      resolutionBack1080pSupported = true;
    if(camera2Settings.isBackCamera2160pSupported())
      resolutionBack2160pSupported = true;

    if(camera2Settings.isFrontCamera720pSupported())
      resolutionFront720pSupported = true;
    if(camera2Settings.isFrontCamera1080pSupported())
      resolutionFront1080pSupported = true;
    if(camera2Settings.isFrontCamera2160pSupported())
      resolutionFront2160pSupported = true;

    ResolutionPreference resolutionPreference = new ResolutionPreference(defaultResolution,
            resolutionBack720pSupported, resolutionBack1080pSupported,
            resolutionBack2160pSupported, resolutionFront720pSupported,
            resolutionFront1080pSupported, resolutionFront2160pSupported);

    addCameraPreferenceUseCase.setResolutionPreferencesSupported(resolutionPreference);

  }

  public void checkCamera2FrameRateSupported() {
      Camera2Settings camera2Settings = null;
      try {
          camera2Settings = new Camera2Settings(context);
      } catch (CameraAccessException e) {
          e.printStackTrace();
          // TODO: 15/11/2017 Manage Error
          return;
      }

      String defaultFrameRate = Constants.DEFAULT_CAMERA_PREF_FRAME_RATE;
      boolean frameRate24FpsSupported = false;
      boolean frameRate25FpsSupported = false;
      boolean frameRate30FpsSupported = false;

      if (camera2Settings.isFrameRateSupported()) {
          if (camera2Settings.isFrameRate24fpsSupported()) {
              frameRate24FpsSupported = true;
          }
          if (camera2Settings.isFrameRate25fpsSupported()) {
              frameRate25FpsSupported = true;
          }
          if(camera2Settings.isFrameRate30fpsSupported()) {
              frameRate30FpsSupported = true;
          }
      }

      FrameRatePreference frameRatePreference = new FrameRatePreference(defaultFrameRate,
              frameRate24FpsSupported, frameRate25FpsSupported, frameRate30FpsSupported);

      addCameraPreferenceUseCase.setFrameRatePreferencesSupported(frameRatePreference);
  }
}
