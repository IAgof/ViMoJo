package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingSelectable;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.*;

public class CameraSettingsPresenter {

  private CameraSettings cameraSettings;
  private CameraSettingsView cameraSettingsListView;
  protected UserEventTracker userEventTracker;
  private GetCameraSettingsListUseCase getSettingListUseCase;
  private CameraSettingsRepository cameraSettingsRepository;
  private ProjectRepository projectRepository;
  private HashMap<Integer, String> resolutionNames;
  private HashMap<Integer, VideoResolution.Resolution> videoResolutionValues;
  private HashMap<Integer, String> frameRateNames;
  private HashMap<Integer, VideoFrameRate.FrameRate> frameRateValues;
  private HashMap<Integer, String> qualityNames;
  private HashMap<Integer, VideoQuality.Quality> qualityValues;

  private HashMap<Integer, String> interfaceNames;

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
    this.cameraSettings = cameraSettingsRepository.getCameraSettings();
    this.projectRepository = projectRepository;

    setupResolutionMappers();
    setupFrameRateMappers();
    setupQualityMappers();
    setupInterfaceMappers();
  }

  private void setupInterfaceMappers() {
    interfaceNames = new HashMap<Integer, String>();
    interfaceNames.put(CAMERA_SETTING_INTERFACE_PRO_ID, CAMERA_SETTING_INTERFACE_PRO);
    interfaceNames.put(CAMERA_SETTING_INTERFACE_BASIC_ID, CAMERA_SETTING_INTERFACE_BASIC);
  }

  private void setupResolutionMappers() {
    resolutionNames = new HashMap<Integer, String>();
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, CAMERA_SETTING_RESOLUTION_720);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, CAMERA_SETTING_RESOLUTION_1080);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, CAMERA_SETTING_RESOLUTION_2160);

    videoResolutionValues = new HashMap<Integer, VideoResolution.Resolution>();
    videoResolutionValues.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, VideoResolution.Resolution.HD720);
    videoResolutionValues.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, VideoResolution.Resolution.HD1080);
    videoResolutionValues.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, VideoResolution.Resolution.HD4K);
  }

  private void setupFrameRateMappers() {
    frameRateNames = new HashMap<Integer, String>();
    frameRateNames.put(CAMERA_SETTING_FRAME_RATE_24_ID, CAMERA_SETTING_FRAME_RATE_24);
    frameRateNames.put(CAMERA_SETTING_FRAME_RATE_25_ID, CAMERA_SETTING_FRAME_RATE_25);
    frameRateNames.put(CAMERA_SETTING_FRAME_RATE_30_ID, CAMERA_SETTING_FRAME_RATE_30);

    frameRateValues = new HashMap<Integer, VideoFrameRate.FrameRate>();
    frameRateValues.put(CAMERA_SETTING_FRAME_RATE_24_ID, VideoFrameRate.FrameRate.FPS24);
    frameRateValues.put(CAMERA_SETTING_FRAME_RATE_25_ID, VideoFrameRate.FrameRate.FPS25);
    frameRateValues.put(CAMERA_SETTING_FRAME_RATE_30_ID, VideoFrameRate.FrameRate.FPS30);
  }

  private void setupQualityMappers() {
    qualityNames = new HashMap<Integer, String>();
    qualityNames.put(CAMERA_SETTING_QUALITY_16_ID, CAMERA_SETTING_QUALITY_16);
    qualityNames.put(CAMERA_SETTING_QUALITY_32_ID, CAMERA_SETTING_QUALITY_32);
    qualityNames.put(CAMERA_SETTING_QUALITY_50_ID, CAMERA_SETTING_QUALITY_50);

    qualityValues = new HashMap<Integer, VideoQuality.Quality>();
    qualityValues.put(CAMERA_SETTING_QUALITY_16_ID, VideoQuality.Quality.LOW);
    qualityValues.put(CAMERA_SETTING_QUALITY_32_ID, VideoQuality.Quality.GOOD);
    qualityValues.put(CAMERA_SETTING_QUALITY_50_ID, VideoQuality.Quality.HIGH);
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public void getCameraSettingsList() {
    List<CameraSettingSelectable> cameraSettingSelectables =
            getSettingListUseCase.checkCameraSettingsList(resolutionNames, qualityNames,
                    frameRateNames, interfaceNames);
    cameraSettingsListView.showCameraSettingsList(cameraSettingSelectables);
  }

  public void setCameraInterfaceSetting(int interfaceProId) {
    String interfaceSelected = interfaceNames.get(interfaceProId);
    if(interfaceSelected == null) { interfaceSelected = DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED; }
    cameraSettingsRepository.setInterfaceSelected(cameraSettings, interfaceSelected);
    userEventTracker.trackChangeCameraInterface(interfaceSelected);
  }

  public void setCameraResolutionSetting(int resolutionSelectedId) {
    Project currentProject = loadCurrentProject();
    String resolution = resolutionNames.get(resolutionSelectedId);
    if (resolution == null) { resolution = DEFAULT_CAMERA_SETTING_RESOLUTION; }
    cameraSettingsRepository.setResolutionSetting(cameraSettings, resolution);
    userEventTracker.trackChangeResolution(resolution);
    VideoResolution.Resolution videoResolution = videoResolutionValues.get(resolutionSelectedId);
    if (videoResolution == null) { videoResolution = DEFAULT_CAMERA_SETTING_VIDEO_RESOLUTION; }
    projectRepository.updateResolution(currentProject, videoResolution);
  }

  public void setCameraFrameRateSetting(int frameRateSelectedId) {
    String frameRate = frameRateNames.get(frameRateSelectedId);
    if(frameRate == null) { frameRate = DEFAULT_CAMERA_SETTING_FRAME_RATE; }
    VideoFrameRate.FrameRate videoFrameRate = frameRateValues.get(frameRateSelectedId);
    if(videoFrameRate == null) { videoFrameRate = DEFAULT_CAMERA_SETTING_VIDEO_FRAME_RATE; }
    Project currentProject = loadCurrentProject();
    cameraSettingsRepository.setFrameRateSetting(cameraSettings, frameRate);
    projectRepository.updateFrameRate(currentProject, videoFrameRate);
    userEventTracker.trackChangeFrameRate(frameRate);
  }

  public void setCameraQualitySetting(int qualitySelectedId) {
    String quality = qualityNames.get(qualitySelectedId);
    if(quality == null) { quality = DEFAULT_CAMERA_SETTING_QUALITY; }
    VideoQuality.Quality videoQuality = qualityValues.get(qualitySelectedId);
    if(videoQuality == null) { videoQuality = DEFAULT_CAMERA_SETTING_VIDEO_QUALITY; }
    Project currentProject = loadCurrentProject();
    cameraSettingsRepository.setQualitySetting(cameraSettings, quality);
    projectRepository.updateQuality(currentProject, videoQuality);
    userEventTracker.trackChangeQuality(quality);
  }

  public HashMap<Integer, String> getResolutionNames() {
    return resolutionNames;
  }

  public HashMap<Integer, String> getFrameRateNames() {
    return frameRateNames;
  }

  public HashMap<Integer, String> getQualityNames() {
    return qualityNames;
  }

  public HashMap<Integer, String> getInterfaceNames() {
    return interfaceNames;
  }
}
