package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsMapperSupportedListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingViewModel;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionFrameRate;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionQuality;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionResolution;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

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

public class CameraSettingsPresenter extends VimojoPresenter {
  private final CameraSettingsView cameraSettingsListView;
  protected UserEventTracker userEventTracker;
  private GetCameraSettingsMapperSupportedListUseCase getSettingListUseCase;
  private CameraSettingsDataSource cameraSettingsRepository;
  private final ProjectInstanceCache projectInstanceCache;
  private CameraSettings cameraSettings;
  private UpdateComposition updateComposition;
  private SetCompositionQuality setCompositionQuality;
  private SetCompositionFrameRate setCompositionFrameRate;
  private SetCompositionResolution setCompositionResolution;

  private HashMap<Integer, String> resolutionNames;
  private HashMap<Integer, VideoResolution.Resolution> videoResolutionValues;
  private HashMap<Integer, String> frameRateNames;
  private HashMap<Integer, VideoFrameRate.FrameRate> frameRateValues;
  private HashMap<Integer, String> qualityNames;

  private HashMap<Integer, VideoQuality.Quality> qualityValues;
  private HashMap<Integer, String> proInterfaceNames;
  protected Project currentProject;

  @Inject
  public CameraSettingsPresenter(
          CameraSettingsView cameraSettingsListView, UserEventTracker userEventTracker,
          GetCameraSettingsMapperSupportedListUseCase getSettingListUseCase,
          CameraSettingsDataSource cameraSettingsRepository,
          UpdateComposition updateComposition, ProjectInstanceCache projectInstanceCache,
          SetCompositionQuality setCompositionQuality,
          SetCompositionFrameRate setCompositionFrameRate,
          SetCompositionResolution setCompositionResolution) {
    this.cameraSettingsListView = cameraSettingsListView;
    this.userEventTracker = userEventTracker;
    this.getSettingListUseCase = getSettingListUseCase;
    this.cameraSettingsRepository = cameraSettingsRepository;
    this.projectInstanceCache = projectInstanceCache;
    this.cameraSettings = cameraSettingsRepository.getCameraSettings();
    this.setCompositionQuality = setCompositionQuality;
    this.updateComposition = updateComposition;
    this.setCompositionFrameRate = setCompositionFrameRate;
    this.setCompositionResolution = setCompositionResolution;
    setupResolutionMappers();
    setupFrameRateMappers();
    setupQualityMappers();
    setupProInterfaceMappers();
  }

  public void updatePresenter() {
    this.currentProject = projectInstanceCache.getCurrentProject();
    if (BuildConfig.FEATURE_VERTICAL_VIDEOS) {
      cameraSettingsListView.screenOrientationPortrait();
    } else {
      cameraSettingsListView.screenOrientationLandscape();
    }
  }

  private void setupProInterfaceMappers() {
    proInterfaceNames = new HashMap<Integer, String>();
    proInterfaceNames.put(CAMERA_SETTING_INTERFACE_PRO_ID, CAMERA_SETTING_INTERFACE_PRO);
    proInterfaceNames.put(CAMERA_SETTING_INTERFACE_BASIC_ID, CAMERA_SETTING_INTERFACE_BASIC);
  }

  private void setupResolutionMappers() {
    if (BuildConfig.FEATURE_VERTICAL_VIDEOS) {
      setupVerticalResolutions();
    } else {
      setupHorizontalResolutions();
    }
  }

  private void setupVerticalResolutions() {
    resolutionNames = new HashMap<Integer, String>();
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_720);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_1080);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_2160);
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_720);
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_1080);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_2160);

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

  private void setupHorizontalResolutions() {
    resolutionNames = new HashMap<Integer, String>();
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID,
            ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_720);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID,
            ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_1080);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID,
            ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_2160);
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_720);
    resolutionNames.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_1080);
    resolutionNames.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID,
        ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_2160);

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
    String resolution = resolutionNames.get(resolutionSelectedId);
    if (resolution == null) { resolution = DEFAULT_CAMERA_SETTING_RESOLUTION; }
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    cameraSettingsRepository.setResolutionSetting(cameraSettings, resolution);
    userEventTracker.trackChangeResolution(resolution);
    VideoResolution.Resolution videoResolution = videoResolutionValues.get(resolutionSelectedId);
    if (videoResolution == null) { videoResolution = DEFAULT_CAMERA_SETTING_VIDEO_RESOLUTION; }
    setCompositionResolution.setResolution(currentProject, videoResolution);
    executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
  }

  public void setCameraFrameRateSetting(int frameRateSelectedId) {
    String frameRate = frameRateNames.get(frameRateSelectedId);
    if (frameRate == null) { frameRate = DEFAULT_CAMERA_SETTING_FRAME_RATE; }
    VideoFrameRate.FrameRate videoFrameRate = frameRateValues.get(frameRateSelectedId);
    if (videoFrameRate == null) { videoFrameRate = DEFAULT_CAMERA_SETTING_VIDEO_FRAME_RATE; }
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    cameraSettingsRepository.setFrameRateSetting(cameraSettings, frameRate);
    setCompositionFrameRate.updateFrameRate(currentProject, videoFrameRate);
    executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
    userEventTracker.trackChangeFrameRate(frameRate);
  }

  public void setCameraQualitySetting(int qualitySelectedId) {
    String quality = qualityNames.get(qualitySelectedId);
    if (quality == null) { quality = DEFAULT_CAMERA_SETTING_QUALITY; }
    VideoQuality.Quality videoQuality = qualityValues.get(qualitySelectedId);
    if (videoQuality == null) { videoQuality = DEFAULT_CAMERA_SETTING_VIDEO_QUALITY; }
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    cameraSettingsRepository.setQualitySetting(cameraSettings, quality);
    setCompositionQuality.setQuality(currentProject, videoQuality);
    executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
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
