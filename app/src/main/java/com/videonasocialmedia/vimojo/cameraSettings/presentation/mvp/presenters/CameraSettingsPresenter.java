package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsMapperSupportedListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingViewModel;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.*;

public class CameraSettingsPresenter {
  private CameraSettings cameraSettings;
  private CameraSettingsView cameraSettingsListView;
  protected UserEventTracker userEventTracker;
  private GetCameraSettingsMapperSupportedListUseCase getSettingListUseCase;
  private CameraSettingsRepository cameraSettingsRepository;
  private ProjectRepository projectRepository;
  private HashMap<Integer, String> resolutionNames;
  private HashMap<Integer, VideoResolution.Resolution> videoResolutionValues;
  private HashMap<Integer, String> frameRateNames;
  private HashMap<Integer, VideoFrameRate.FrameRate> frameRateValues;
  private HashMap<Integer, String> qualityNames;
  private HashMap<Integer, VideoQuality.Quality> qualityValues;

  private HashMap<Integer, String> proInterfaceNames;

  @Inject
  public CameraSettingsPresenter(CameraSettingsView cameraSettingsListView,
                                 UserEventTracker userEventTracker,
                                 GetCameraSettingsMapperSupportedListUseCase getSettingListUseCase,
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
    setupProInterfaceMappers();
  }

  private void setupProInterfaceMappers() {
    proInterfaceNames = new HashMap<Integer, String>();
    proInterfaceNames.put(CAMERA_SETTING_INTERFACE_PRO_ID, CAMERA_SETTING_INTERFACE_PRO);
    proInterfaceNames.put(CAMERA_SETTING_INTERFACE_BASIC_ID, CAMERA_SETTING_INTERFACE_BASIC);
  }

  private void setupResolutionMappers() {
    resolutionNames = new HashMap<Integer, String>();
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID,
            ResolutionSetting.CAMERA_SETTING_RESOLUTION_720);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID,
            ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID,
            ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160);
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_720);
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160);

    videoResolutionValues = new HashMap<Integer, VideoResolution.Resolution>();
    videoResolutionValues.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID,
            VideoResolution.Resolution.HD720);
    videoResolutionValues.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID,
            VideoResolution.Resolution.HD1080);
    videoResolutionValues.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID,
            VideoResolution.Resolution.HD4K);
    videoResolutionValues.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID,
        VideoResolution.Resolution.HD720);
    videoResolutionValues.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID,
        VideoResolution.Resolution.HD1080);
    videoResolutionValues.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID,
        VideoResolution.Resolution.HD4K);
  }

  private void setupFrameRateMappers() {
    frameRateNames = new HashMap<Integer, String>();
    frameRateNames.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID,
            FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24);
    frameRateNames.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID,
            FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25);
    frameRateNames.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID,
            FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30);

    frameRateValues = new HashMap<Integer, VideoFrameRate.FrameRate>();
    frameRateValues.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID,
            VideoFrameRate.FrameRate.FPS24);
    frameRateValues.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID,
            VideoFrameRate.FrameRate.FPS25);
    frameRateValues.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID,
            VideoFrameRate.FrameRate.FPS30);
  }

  private void setupQualityMappers() {
    qualityNames = new HashMap<Integer, String>();
    qualityNames.put(CameraSettings.CAMERA_SETTING_QUALITY_16_ID,
            CameraSettings.CAMERA_SETTING_QUALITY_16);
    qualityNames.put(CameraSettings.CAMERA_SETTING_QUALITY_25_ID,
            CameraSettings.CAMERA_SETTING_QUALITY_25);
    qualityNames.put(CameraSettings.CAMERA_SETTING_QUALITY_50_ID,
            CameraSettings.CAMERA_SETTING_QUALITY_50);

    qualityValues = new HashMap<Integer, VideoQuality.Quality>();
    qualityValues.put(CameraSettings.CAMERA_SETTING_QUALITY_16_ID, VideoQuality.Quality.LOW);
    qualityValues.put(CameraSettings.CAMERA_SETTING_QUALITY_25_ID, VideoQuality.Quality.GOOD);
    qualityValues.put(CameraSettings.CAMERA_SETTING_QUALITY_50_ID, VideoQuality.Quality.HIGH);
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public void getCameraSettingsList() {
    List<CameraSettingViewModel> cameraSettingViewModels =
            getSettingListUseCase.getCameraSettingsList(resolutionNames, qualityNames,
                    frameRateNames, proInterfaceNames);
    cameraSettingsListView.showCameraSettingsList(cameraSettingViewModels);
  }

  public void setCameraInterfaceSetting(int interfaceProId) {
    String interfaceSelected = proInterfaceNames.get(interfaceProId);
    if (interfaceSelected == null) {
      interfaceSelected = DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
    }
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    cameraSettingsRepository.setInterfaceSelected(cameraSettings, interfaceSelected);
    userEventTracker.trackChangeCameraInterface(interfaceSelected);
  }

  public void setCameraResolutionSetting(int resolutionSelectedId) {
    Project currentProject = loadCurrentProject();
    String resolution = resolutionNames.get(resolutionSelectedId);
    if (resolution == null) { resolution = DEFAULT_CAMERA_SETTING_RESOLUTION; }
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    cameraSettingsRepository.setResolutionSetting(cameraSettings, resolution);
    userEventTracker.trackChangeResolution(resolution);
    VideoResolution.Resolution videoResolution = videoResolutionValues.get(resolutionSelectedId);
    if (videoResolution == null) { videoResolution = DEFAULT_CAMERA_SETTING_VIDEO_RESOLUTION; }
    projectRepository.updateResolution(currentProject, videoResolution);
  }

  public void setCameraFrameRateSetting(int frameRateSelectedId) {
    String frameRate = frameRateNames.get(frameRateSelectedId);
    if (frameRate == null) { frameRate = DEFAULT_CAMERA_SETTING_FRAME_RATE; }
    VideoFrameRate.FrameRate videoFrameRate = frameRateValues.get(frameRateSelectedId);
    if (videoFrameRate == null) { videoFrameRate = DEFAULT_CAMERA_SETTING_VIDEO_FRAME_RATE; }
    Project currentProject = loadCurrentProject();
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    cameraSettingsRepository.setFrameRateSetting(cameraSettings, frameRate);
    projectRepository.updateFrameRate(currentProject, videoFrameRate);
    userEventTracker.trackChangeFrameRate(frameRate);
  }

  public void setCameraQualitySetting(int qualitySelectedId) {
    String quality = qualityNames.get(qualitySelectedId);
    if (quality == null) { quality = DEFAULT_CAMERA_SETTING_QUALITY; }
    VideoQuality.Quality videoQuality = qualityValues.get(qualitySelectedId);
    if (videoQuality == null) { videoQuality = DEFAULT_CAMERA_SETTING_VIDEO_QUALITY; }
    Project currentProject = loadCurrentProject();
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    cameraSettingsRepository.setQualitySetting(cameraSettings, quality);
    projectRepository.updateQuality(currentProject, videoQuality);
    userEventTracker.trackChangeQuality(quality);
  }

  public void settingChanged(int settingId) {
    if (resolutionNames.containsKey(settingId)) {
      if(isResolutionSupportedInBackFrontCamera(settingId)) {
        setCameraResolutionSetting(settingId);
      } else {
        cameraSettingsListView.showDialogResolutionNotSupportedInBothCameras(settingId);
      }
      return;
    }
    if (frameRateNames.containsKey(settingId)) {
      setCameraFrameRateSetting(settingId);
      return;
    }
    if (qualityNames.containsKey(settingId)) {
      setCameraQualitySetting(settingId);
      return;
    }
    if (proInterfaceNames.containsKey(settingId)) {
      setCameraInterfaceSetting(settingId);
      return;
    }
  }

  private boolean isResolutionSupportedInBackFrontCamera(int settingId) {
    boolean isResolutionSupportedInBackFrontCamera = false;
    switch (settingId) {
      case CAMERA_SETTING_RESOLUTION_720_BACK_ID:
        if(cameraSettings.getResolutionSetting()
                .deviceSupports(CAMERA_SETTING_RESOLUTION_720_FRONT_ID))
          isResolutionSupportedInBackFrontCamera = true;
        break;
      case CAMERA_SETTING_RESOLUTION_1080_BACK_ID:
        if(cameraSettings.getResolutionSetting()
                .deviceSupports(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID))
          isResolutionSupportedInBackFrontCamera = true;
        break;
      case CAMERA_SETTING_RESOLUTION_2160_BACK_ID:
        if(cameraSettings.getResolutionSetting()
                .deviceSupports(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID))
          isResolutionSupportedInBackFrontCamera = true;
        break;
      case CAMERA_SETTING_RESOLUTION_720_FRONT_ID:
        if(cameraSettings.getResolutionSetting()
                .deviceSupports(CAMERA_SETTING_RESOLUTION_720_BACK_ID))
          isResolutionSupportedInBackFrontCamera = true;
        break;
      case CAMERA_SETTING_RESOLUTION_1080_FRONT_ID:
        if(cameraSettings.getResolutionSetting()
                .deviceSupports(CAMERA_SETTING_RESOLUTION_1080_BACK_ID))
          isResolutionSupportedInBackFrontCamera = true;
        break;
      case CAMERA_SETTING_RESOLUTION_2160_FRONT_ID:
        if(cameraSettings.getResolutionSetting()
                .deviceSupports(CAMERA_SETTING_RESOLUTION_2160_BACK_ID))
          isResolutionSupportedInBackFrontCamera = true;
        break;
    }
    return isResolutionSupportedInBackFrontCamera;
  }
}
