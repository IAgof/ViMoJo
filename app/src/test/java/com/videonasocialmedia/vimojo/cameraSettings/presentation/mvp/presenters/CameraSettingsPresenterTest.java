package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 20/11/17.
 */

public class CameraSettingsPresenterTest {

  @Mock CameraSettingsView mockedCameraSettingsListView;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock GetCameraSettingsListUseCase mockedGetSettingListUseCase;
  @Mock
  CameraSettingsRepository mockedCameraSettingsRepository;
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

    presenter.setCameraInterfaceSetting(interfaceProSelectedId);

    verify(mockedCameraSettingsRepository)
            .setInterfaceSelected(DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED);
    verify(mockedUserEventTracker)
            .trackChangeCameraInterface(DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED);
  }

  @Test
  public void setCameraResolutionPreferenceUpdateRepositoriesProjectAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int resolutionPreferenceId = Constants.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
    Project project = getAProject();

    presenter.setCameraResolutionSetting(resolutionPreferenceId);

    verify(mockedCameraSettingsRepository).setResolutionSetting("720p");
    verify(mockedProjectRepository).updateResolution(VideoResolution.Resolution.HD720);
    verify(mockedUserEventTracker).trackChangeResolution("720p");
    assertThat(project.getProfile().getResolution(), is(VideoResolution.Resolution.HD720));
  }

  @Test
  public void setCameraFrameRatePreferenceUpdateRepositoriesProjectAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int frameRatePreferenceId = Constants.CAMERA_SETTING_FRAME_RATE_30_ID;
    Project project = getAProject();

    presenter.setCameraFrameRateSetting(frameRatePreferenceId);

    verify(mockedCameraSettingsRepository).setFrameRateSetting("30 fps");
    verify(mockedProjectRepository).updateFrameRate(VideoFrameRate.FrameRate.FPS30);
    verify(mockedUserEventTracker).trackChangeFrameRate("30 fps");
    assertThat(project.getProfile().getFrameRate(), is(VideoFrameRate.FrameRate.FPS30));
  }


  @Test
  public void setCameraQualityPreferenceUpdateRepositoriesProjectAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int qualityPreferenceId = Constants.CAMERA_SETTING_QUALITY_16_ID;
    Project project = getAProject();

    presenter.setCameraQualitySetting(qualityPreferenceId);

    verify(mockedCameraSettingsRepository).setQualitySetting("16 Mbps");
    verify(mockedProjectRepository).updateQuality(VideoQuality.Quality.LOW);
    verify(mockedUserEventTracker).trackChangeQuality("16 Mbps");
    assertThat(project.getProfile().getQuality(), is(VideoQuality.Quality.LOW));
  }

  private CameraSettingsPresenter getCameraSettingsPresenter() {
    return new CameraSettingsPresenter(mockedCameraSettingsListView,
        mockedUserEventTracker, mockedGetSettingListUseCase, mockedCameraSettingsRepository,
        mockedProjectRepository);
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD1080, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25));
  }
}
