package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsMapperSupportedListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionFrameRate;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionQuality;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionResolution;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.ConstantsTest;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 20/11/17.
 */

public class CameraSettingsPresenterTest {
  @Mock CameraSettingsView mockedCameraSettingsListView;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock GetCameraSettingsMapperSupportedListUseCase mockedGetSettingListUseCase;
  @Mock CameraSettingsDataSource mockedCameraSettingsRepository;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock UpdateComposition mockedUpdateComposition;
  private Project currentProject;
  @Mock SetCompositionQuality mockedSetCompositionQuality;
  @Mock SetCompositionFrameRate mockedSetCompositionFrameRate;
  @Mock SetCompositionResolution mockedSetCompositionResolution;
  private String DEFAULT_CAMERA_SETTING_RESOLUTION =
      ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_720;
  private boolean showCameraPro;
  private boolean allowSelectFrameRate;
  private boolean allowSelectResolution;
  private boolean amIAVerticalApp;
  private String defaultResolutionSetting;
  private VideoResolution.Resolution defaultVideoResolution;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    setAProject();
  }


  @Test
  public void constructorSetsUserTracker() {
    UserEventTracker userEventTracker = UserEventTracker.getInstance();
    CameraSettingsPresenter presenter = new CameraSettingsPresenter(
        mockedCameraSettingsListView, userEventTracker, mockedGetSettingListUseCase,
        mockedCameraSettingsRepository, mockedUpdateComposition, mockedProjectInstanceCache,
        mockedSetCompositionQuality, mockedSetCompositionFrameRate, mockedSetCompositionResolution,
        showCameraPro, allowSelectFrameRate, allowSelectResolution, amIAVerticalApp,
        defaultResolutionSetting, defaultVideoResolution);

    assertThat(presenter.userEventTracker, is(userEventTracker));
  }

  @Test
  public void setCameraInterfacePreferenceUpdateRepositoryAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int interfaceProSelectedId = Constants.CAMERA_SETTING_INTERFACE_PRO_ID;
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.setCameraInterfaceSetting(interfaceProSelectedId);

    verify(mockedCameraSettingsRepository)
            .setInterfaceSelected(cameraSettings, DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED);
    verify(mockedUserEventTracker)
            .trackChangeCameraInterface(DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED);
  }

  @Test
  public void setCameraResolutionPreferenceUpdateRepositoriesProjectAndTracking()
      throws InterruptedException {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int resolutionPreferenceId = ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.setCameraResolutionSetting(resolutionPreferenceId);

    verify(mockedCameraSettingsRepository).setResolutionSetting(any(CameraSettings.class), anyString());
    verify(mockedSetCompositionResolution).setResolution(any(Project.class), any(VideoResolution.Resolution.class));
    verify(mockedUserEventTracker).trackChangeResolution(anyString());
    Thread.sleep(ConstantsTest.SLEEP_MILLIS_FOR_TEST_BACKGROUND_TASKS);
    verify(mockedUpdateComposition).updateComposition(any(Project.class));
  }

  @Test
  public void setCameraFrameRatePreferenceUpdateRepositoriesProjectAndTracking()
      throws InterruptedException {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int frameRatePreferenceId = FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID;
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.setCameraFrameRateSetting(frameRatePreferenceId);

    verify(mockedCameraSettingsRepository).setFrameRateSetting(cameraSettings, "30 fps");
    verify(mockedSetCompositionFrameRate).updateFrameRate(currentProject,
        VideoFrameRate.FrameRate.FPS30);
    verify(mockedUserEventTracker).trackChangeFrameRate("30 fps");
    Thread.sleep(ConstantsTest.SLEEP_MILLIS_FOR_TEST_BACKGROUND_TASKS);
    verify(mockedUpdateComposition).updateComposition(currentProject);
  }


  @Test
  public void setCameraQualityPreferenceUpdateRepositoriesProjectAndTracking()
      throws InterruptedException {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int qualityPreferenceId = CameraSettings.CAMERA_SETTING_QUALITY_16_ID;
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.setCameraQualitySetting(qualityPreferenceId);

    verify(mockedCameraSettingsRepository).setQualitySetting(cameraSettings, "16 Mbps");
    verify(mockedSetCompositionQuality).setQuality(currentProject, VideoQuality.Quality.LOW);
    verify(mockedUserEventTracker).trackChangeQuality("16 Mbps");
    Thread.sleep(ConstantsTest.SLEEP_MILLIS_FOR_TEST_BACKGROUND_TASKS);
    verify(mockedUpdateComposition).updateComposition(currentProject);
  }

  private CameraSettingsPresenter getCameraSettingsPresenter() {
    CameraSettingsPresenter cameraSettingsPresenter = new CameraSettingsPresenter(
        mockedCameraSettingsListView, mockedUserEventTracker, mockedGetSettingListUseCase,
        mockedCameraSettingsRepository, mockedUpdateComposition, mockedProjectInstanceCache,
        mockedSetCompositionQuality, mockedSetCompositionFrameRate, mockedSetCompositionResolution,
        showCameraPro, allowSelectFrameRate, allowSelectResolution, amIAVerticalApp,
        defaultResolutionSetting, defaultVideoResolution);
    cameraSettingsPresenter.currentProject = currentProject;
    return cameraSettingsPresenter;
  }


  private CameraSettings getCameraSettings() {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, false);
    ResolutionSetting resolutionSetting = new ResolutionSetting(DEFAULT_CAMERA_SETTING_RESOLUTION,
        resolutionsSupportedMap);
    HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, true);
    FrameRateSetting frameRateSetting = new FrameRateSetting("30 fps", frameRatesSupportedMap);
    String quality = "16 Mbps";
    String interfaceSelected = DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
    int cameraIdSelected = Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED;
    return new CameraSettings(resolutionSetting,frameRateSetting, quality, interfaceSelected,
        cameraIdSelected);
  }

  public void setAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
