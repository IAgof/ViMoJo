package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsMapperSupportedListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

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
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_RESOLUTION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 20/11/17.
 */

public class CameraSettingsPresenterTest {

  @Mock CameraSettingsView mockedCameraSettingsListView;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock
  GetCameraSettingsMapperSupportedListUseCase mockedGetSettingListUseCase;
  @Mock CameraSettingsRepository mockedCameraSettingsRepository;
  @Mock CameraSettings mockedCameraSettings;
  @Mock ProjectRepository mockedProjectRepository;

  @Mock private MixpanelAPI mockedMixpanelAPI;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    Project.getInstance(null, null, null, null).clear();
  }

  @Test
  public void constructorSetsUserTracker() {
    UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
    CameraSettingsPresenter presenter = new CameraSettingsPresenter(
        mockedCameraSettingsListView, userEventTracker, mockedGetSettingListUseCase,
            mockedCameraSettingsRepository, mockedProjectRepository);

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
  public void setCameraResolutionPreferenceUpdateRepositoriesProjectAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int resolutionPreferenceId = ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
    Project project = getAProject();
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.setCameraResolutionSetting(resolutionPreferenceId);

    verify(mockedCameraSettingsRepository).setResolutionSetting(cameraSettings, "720p");
    verify(mockedProjectRepository).updateResolution(project, VideoResolution.Resolution.HD720);
    verify(mockedUserEventTracker).trackChangeResolution("720p");
  }

  @Test
  public void setCameraFrameRatePreferenceUpdateRepositoriesProjectAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int frameRatePreferenceId = FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID;
    Project project = getAProject();
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.setCameraFrameRateSetting(frameRatePreferenceId);

    verify(mockedCameraSettingsRepository).setFrameRateSetting(cameraSettings, "30 fps");
    verify(mockedProjectRepository).updateFrameRate(project, VideoFrameRate.FrameRate.FPS30);
    verify(mockedUserEventTracker).trackChangeFrameRate("30 fps");
  }


  @Test
  public void setCameraQualityPreferenceUpdateRepositoriesProjectAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int qualityPreferenceId = CameraSettings.CAMERA_SETTING_QUALITY_16_ID;
    Project project = getAProject();
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.setCameraQualitySetting(qualityPreferenceId);

    verify(mockedCameraSettingsRepository).setQualitySetting(cameraSettings, "16 Mbps");
    verify(mockedProjectRepository).updateQuality(project, VideoQuality.Quality.LOW);
    verify(mockedUserEventTracker).trackChangeQuality("16 Mbps");
  }

  private CameraSettingsPresenter getCameraSettingsPresenter() {
    return new CameraSettingsPresenter(mockedCameraSettingsListView,
        mockedUserEventTracker, mockedGetSettingListUseCase, mockedCameraSettingsRepository,
        mockedProjectRepository);
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

  public Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", "private/path", compositionProfile);
  }
}
