package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.util.Range;
import android.util.Size;

import com.videonasocialmedia.camera.utils.Camera2Settings;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.HashMap;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.BACK_CAMERA_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_FRAME_RATE_30_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.FRONT_CAMERA_ID;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private final Context context;
  private final CameraSettingsRepository cameraSettingsRepository;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;


  @Inject
  public InitAppPresenter(Context context, CreateDefaultProjectUseCase
          createDefaultProjectUseCase, CameraSettingsRepository cameraSettingsRepository) {
    this.context = context;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.cameraSettingsRepository = cameraSettingsRepository;
  }

  public void startLoadingProject(String rootPath, String privatePath,
                                  boolean isWatermarkFeatured) {
    createDefaultProjectUseCase.loadOrCreateProject(rootPath, privatePath, isWatermarkFeatured);
  }

  public void checkCamera2FrameRateAndResolutionSupported() {
    Camera2Settings camera2Settings = null;
    try {
      camera2Settings = new Camera2Settings(context);
      checkCamera2FrameRateSupported(camera2Settings.getFPSRange(BACK_CAMERA_ID));
      checkCamera2ResolutionSupported(camera2Settings);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      // TODO: 15/11/2017 Manage Error
      return;
    }

  }

  private void checkCamera2ResolutionSupported(Camera2Settings camera2Settings)
          throws CameraAccessException {

    String defaultResolution = Constants.DEFAULT_CAMERA_PREF_RESOLUTION;
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();

    boolean resolutionBack720pSupported = false;
    boolean resolutionBack1080pSupported = false;
    boolean resolutionBack2160pSupported = false;
    boolean resolutionFront720pSupported = false;
    boolean resolutionFront1080pSupported = false;
    boolean resolutionFront2160pSupported = false;

    for(Size sizeBackCamera: camera2Settings.getSupportedVideoSizes(BACK_CAMERA_ID)){
      if(sizeBackCamera.getWidth() == 1280 && sizeBackCamera.getHeight() == 720){
        resolutionBack720pSupported = true;
        }
      if(sizeBackCamera.getWidth() == 1920 && sizeBackCamera.getHeight() == 1080){
        resolutionBack1080pSupported = true;
        }
      if(sizeBackCamera.getWidth() == 3840 && sizeBackCamera.getHeight() == 2160){
        resolutionBack2160pSupported = true;
        }
    }

    if(camera2Settings.hasFrontCamera()) {
      for(Size sizeFrontCamera: camera2Settings.getSupportedVideoSizes(FRONT_CAMERA_ID)){
        if(sizeFrontCamera.getWidth() == 1280 && sizeFrontCamera.getHeight() == 720){
          resolutionFront720pSupported = true;
        }
        if(sizeFrontCamera.getWidth() == 1920 && sizeFrontCamera.getHeight() == 1080){
          resolutionFront1080pSupported = true;
        }
        if(sizeFrontCamera.getWidth() == 3840 && sizeFrontCamera.getHeight() == 2160){
          resolutionFront2160pSupported = true;
        }
      }
    }

    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_720_BACK_ID, resolutionBack720pSupported);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_1080_BACK_ID, resolutionBack1080pSupported);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_2160_BACK_ID, resolutionBack2160pSupported);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_720_FRONT_ID, resolutionFront720pSupported);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_1080_FRONT_ID, resolutionFront1080pSupported);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_2160_FRONT_ID, resolutionFront2160pSupported);

    ResolutionSetting resolutionSetting = new ResolutionSetting(defaultResolution,
            resolutionsSupportedMap);

    cameraSettingsRepository.setResolutionSettingSupported(resolutionSetting);

  }

  private void checkCamera2FrameRateSupported(Range<Integer>[] fpsRange)
          throws CameraAccessException {

    HashMap<Integer, Boolean> frameRateMap = new HashMap<>();
    boolean frameRate24FpsSupported = false;
    boolean frameRate25FpsSupported = false;
    boolean frameRate30FpsSupported = false;
    String defaultFrameRate = Constants.DEFAULT_CAMERA_PREF_FRAME_RATE;

    for(Range<Integer> fps: fpsRange) {
      if (fps.getLower().equals(fps.getUpper())) {
        if (fps.getLower() == 24) {
          frameRate24FpsSupported = true;
        } else {
          if (fps.getLower() == 25) {
            frameRate25FpsSupported = true;
          } else {
            if (fps.getLower() == 30) {
              frameRate30FpsSupported = true;
            }
          }
        }
      }
    }

    frameRateMap.put(CAMERA_PREF_FRAME_RATE_24_ID, frameRate24FpsSupported);
    frameRateMap.put(CAMERA_PREF_FRAME_RATE_25_ID, frameRate25FpsSupported);
    frameRateMap.put(CAMERA_PREF_FRAME_RATE_30_ID, frameRate30FpsSupported);

    FrameRateSetting frameRateSetting = new FrameRateSetting(defaultFrameRate, frameRateMap);

    cameraSettingsRepository.setFrameRateSettingSupported(frameRateSetting);
  }
}
