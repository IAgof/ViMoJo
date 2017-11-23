package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.HashMap;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.*;

public class CameraSettingsPresenter {

  private CameraSettingsView cameraSettingsListView;
  protected UserEventTracker userEventTracker;
  private GetCameraSettingsListUseCase getSettingListUseCase;
  private CameraSettingsRepository cameraSettingsRepository;
  private ProjectRepository projectRepository;
  private HashMap<Integer, String> resolutionNames;
  private HashMap<Integer, VideoResolution.Resolution> videoResolutionValues;
  private HashMap<Integer, String> frameRateNames;
  private HashMap<Integer, VideoFrameRate.FrameRate> frameRateValues;

  @Inject
  public CameraSettingsPresenter(CameraSettingsView cameraSettingsListView,
                                 UserEventTracker userEventTracker,
                                 GetCameraSettingsListUseCase getSettingListUseCase,
                                 CameraSettingsRepository cameraSettingsRepository,
                                 ProjectRepository
                                           projectRepository) {
    this.getSettingListUseCase = getSettingListUseCase;
    this.cameraSettingsListView = cameraSettingsListView;
    this.userEventTracker = userEventTracker;
    this.cameraSettingsRepository = cameraSettingsRepository;
    this.projectRepository = projectRepository;

    setupResolutionMappers();
    setupFrameRateMappers();
    // TODO:(alvaro.martinez) 23/11/17 setupQualityMappers, setupInterfaceMappers
  }

  private void setupResolutionMappers() {
    resolutionNames = new HashMap<Integer, String>();
    resolutionNames.put(CAMERA_PREF_RESOLUTION_720_BACK_ID, CAMERA_PREF_RESOLUTION_720);
    resolutionNames.put(CAMERA_PREF_RESOLUTION_1080_BACK_ID, CAMERA_PREF_RESOLUTION_1080);
    resolutionNames.put(CAMERA_PREF_RESOLUTION_2160_BACK_ID, CAMERA_PREF_RESOLUTION_2160);

    videoResolutionValues = new HashMap<Integer, VideoResolution.Resolution>();
    videoResolutionValues.put(CAMERA_PREF_RESOLUTION_720_BACK_ID, VideoResolution.Resolution.HD720);
    videoResolutionValues.put(CAMERA_PREF_RESOLUTION_1080_BACK_ID, VideoResolution.Resolution.HD1080);
    videoResolutionValues.put(CAMERA_PREF_RESOLUTION_2160_BACK_ID, VideoResolution.Resolution.HD4K);
  }

  private void setupFrameRateMappers() {
    frameRateNames = new HashMap<Integer, String>();
    frameRateNames.put(CAMERA_PREF_FRAME_RATE_24_ID, CAMERA_PREF_FRAME_RATE_24);
    frameRateNames.put(CAMERA_PREF_FRAME_RATE_25_ID, CAMERA_PREF_FRAME_RATE_25);
    frameRateNames.put(CAMERA_PREF_FRAME_RATE_30_ID, CAMERA_PREF_FRAME_RATE_30);

    frameRateValues = new HashMap<Integer, VideoFrameRate.FrameRate>();
    frameRateValues.put(CAMERA_PREF_FRAME_RATE_24_ID, VideoFrameRate.FrameRate.FPS24);
    frameRateValues.put(CAMERA_PREF_FRAME_RATE_25_ID, VideoFrameRate.FrameRate.FPS25);
    frameRateValues.put(CAMERA_PREF_FRAME_RATE_30_ID, VideoFrameRate.FrameRate.FPS30);
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public void getCameraSettingsList() {
    cameraSettingsListView.showCameraSettingsList(getSettingListUseCase.checkCameraSettingsList());
  }

  public void setCameraInterfaceSetting(int interfaceProId) {
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
    cameraSettingsRepository.setInterfaceProSelected(interfaceProSelected);
    userEventTracker.trackChangeCameraInterface(interfaceProSelected);
  }

  public void setCameraResolutionSetting(int resolutionSelectedId) {
    Project currentProject = loadCurrentProject();
    String resolution = resolutionNames.get(resolutionSelectedId);
    if (resolution == null) { resolution = DEFAULT_CAMERA_PREF_RESOLUTION; }
    cameraSettingsRepository.setResolutionSetting(resolution);
    userEventTracker.trackChangeResolution(resolution);
    VideoResolution.Resolution videoResolution = videoResolutionValues.get(resolutionSelectedId);
    if (videoResolution == null) { videoResolution = VideoResolution.Resolution.HD1080; }
    projectRepository.updateResolution(videoResolution);
    currentProject.getProfile().setResolution(videoResolution);
  }

  public void setCameraFrameRateSetting(int frameRateSelectedId) {
    String frameRate = frameRateNames.get(frameRateSelectedId);
    if(frameRate == null) { frameRate = DEFAULT_CAMERA_PREF_FRAME_RATE; }
    VideoFrameRate.FrameRate videoFrameRate = frameRateValues.get(frameRateSelectedId);
    if(videoFrameRate == null) { videoFrameRate = VideoFrameRate.FrameRate.FPS30; }
    Project currentProject = loadCurrentProject();
    cameraSettingsRepository.setFrameRateSetting(frameRate);
    projectRepository.updateFrameRate(videoFrameRate);
    currentProject.getProfile().setFrameRate(videoFrameRate);
    userEventTracker.trackChangeFrameRate(frameRate);
  }

  public void setCameraQualitySetting(int qualitySelectedId) {
    String quality;
    VideoQuality.Quality videoQuality;
    Project currentProject = loadCurrentProject();
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
    cameraSettingsRepository.setQualitySetting(quality);
    projectRepository.updateQuality(videoQuality);
    currentProject.getProfile().setQuality(videoQuality);
    userEventTracker.trackChangeQuality(quality);
  }

}
