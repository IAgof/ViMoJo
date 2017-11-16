package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;


import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingsPackage;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.record.domain.AddCameraPreferencesUseCase;
import com.videonasocialmedia.vimojo.record.domain.UpdateVideoFrameRateToProjectUseCase;
import com.videonasocialmedia.vimojo.record.domain.UpdateVideoQualityToProjectUseCase;
import com.videonasocialmedia.vimojo.record.domain.UpdateVideoResolutionToProjectUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.*;

public class CameraSettingsPresenter {
  private Context context;
  private List<CameraSettingsPackage> cameraSettingsPackageList;
  private CameraSettingsView cameraSettingsListView;
  private GetCameraSettingsListUseCase getSettingListUseCase;
  private AddCameraPreferencesUseCase addCameraPreferencesUseCase;
  private UpdateVideoFrameRateToProjectUseCase updateVideoFrameRateToProjectUseCase;
  private UpdateVideoResolutionToProjectUseCase updateVideoResolutionToProjectUseCase;
  private UpdateVideoQualityToProjectUseCase updateVideoQualityToProjectUseCase;

  @Inject
  public CameraSettingsPresenter(CameraSettingsView cameraSettingsListView, Context context,
                                 GetCameraSettingsListUseCase getSettingListUseCase,
                                 AddCameraPreferencesUseCase addCameraPreferencesUseCase,
                                 UpdateVideoResolutionToProjectUseCase
                                     updateVideoResolutionToProjectUseCase,
                                 UpdateVideoFrameRateToProjectUseCase
                                       updateVideoFrameRateToProjectUseCase,
                                 UpdateVideoQualityToProjectUseCase
                                       updateVideoQualityToProjectUseCase) {
    this.context = context;
    this.getSettingListUseCase = getSettingListUseCase;
    this.cameraSettingsListView = cameraSettingsListView;
    this.addCameraPreferencesUseCase = addCameraPreferencesUseCase;
    this.updateVideoResolutionToProjectUseCase = updateVideoResolutionToProjectUseCase;
    this.updateVideoFrameRateToProjectUseCase = updateVideoFrameRateToProjectUseCase;
    this.updateVideoQualityToProjectUseCase = updateVideoQualityToProjectUseCase;
  }

  public void getCameraSettingsList() {
    cameraSettingsPackageList = getSettingListUseCase.getCameraSettingsList();
    cameraSettingsListView.showCameraSettingsList(cameraSettingsPackageList);
  }

  public void setCameraInterfacePreference(int interfaceProId) {
    boolean interfaceProSelected;
    switch (interfaceProId) {
      case CAMERA_PREF_INTERFACE_BASIC_ID:
        interfaceProSelected = false;
        break;
      case CAMERA_PREF_INTERFACE_PRO_ID:
        interfaceProSelected = true;
        break;
      default:
        interfaceProSelected = Constants.DEFAULT_CAMERA_PREF_INTERFACE_PRO_SELECTED;
    }
    addCameraPreferencesUseCase.setInterfaceProSelected(interfaceProSelected);
  }

  public void setCameraResolutionPreference(int resolutionSelectedId) {
    String resolution;
    VideoResolution.Resolution videoResolution;
    switch (resolutionSelectedId) {
      case CAMERA_PREF_RESOLUTION_720_ID:
        resolution = CAMERA_PREF_RESOLUTION_720;
        videoResolution = VideoResolution.Resolution.HD720;
        break;
      case CAMERA_PREF_RESOLUTION_1080_ID:
        resolution = CAMERA_PREF_RESOLUTION_1080;
        videoResolution = VideoResolution.Resolution.HD1080;
        break;
      case CAMERA_PREF_RESOLUTION_2160_ID:
        resolution = CAMERA_PREF_RESOLUTION_2160;
        videoResolution = VideoResolution.Resolution.HD4K;
        break;
      default:
        resolution = DEFAULT_CAMERA_PREF_RESOLUTION;
        videoResolution = VideoResolution.Resolution.HD1080;
    }
    addCameraPreferencesUseCase.setResolutionPreference(resolution);
    updateVideoResolutionToProjectUseCase.updateResolution(videoResolution);
  }
  public void setCameraFrameRatePreference(int frameRateSelected) {
    String frameRate;
    VideoFrameRate.FrameRate videoFrameRate;
    switch (frameRateSelected) {
      case CAMERA_PREF_FRAME_RATE_24_ID:
        frameRate = CAMERA_PREF_FRAME_RATE_24;
        videoFrameRate = VideoFrameRate.FrameRate.FPS24;
        break;
      case CAMERA_PREF_FRAME_RATE_25_ID:
        frameRate = CAMERA_PREF_FRAME_RATE_25;
        videoFrameRate = VideoFrameRate.FrameRate.FPS25;
        break;
      case CAMERA_PREF_FRAME_RATE_30_ID:
        frameRate = CAMERA_PREF_FRAME_RATE_30;
        videoFrameRate = VideoFrameRate.FrameRate.FPS30;
        break;
      default:
        frameRate = DEFAULT_CAMERA_PREF_FRAME_RATE;
        videoFrameRate = VideoFrameRate.FrameRate.FPS30;
    }
    addCameraPreferencesUseCase.setFrameRatePreference(frameRate);
    updateVideoFrameRateToProjectUseCase.updateFrameRate(videoFrameRate);
  }

  public void setCameraQualityPreference(int qualitySelectedId) {
    String quality;
    VideoQuality.Quality videoQuality;
    switch (qualitySelectedId) {
      case CAMERA_PREF_QUALITY_16_ID:
        quality = CAMERA_PREF_QUALITY_16;
        videoQuality = VideoQuality.Quality.LOW;
        break;
      case CAMERA_PREF_QUALITY_32_ID:
        quality = CAMERA_PREF_QUALITY_32;
        videoQuality = VideoQuality.Quality.GOOD;
        break;
      case CAMERA_PREF_QUALITY_50_ID:
        quality = CAMERA_PREF_QUALITY_50;
        videoQuality = VideoQuality.Quality.HIGH;
        break;
      default:
        quality = DEFAULT_CAMERA_PREF_QUALITY;
        videoQuality = VideoQuality.Quality.LOW;
    }
    addCameraPreferencesUseCase.setQualityPreference(quality);
    updateVideoQualityToProjectUseCase.updateQuality(videoQuality);
  }
}
