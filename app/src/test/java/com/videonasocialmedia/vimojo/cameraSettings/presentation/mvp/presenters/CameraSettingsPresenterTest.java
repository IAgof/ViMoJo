package com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    int interfaceProSelectedId = Constants.CAMERA_PREF_INTERFACE_PRO_ID;

    presenter.setCameraInterfacePreference(interfaceProSelectedId);

    verify(mockedCameraSettingsRepository).setInterfaceProSelected(true);
    verify(mockedUserEventTracker).trackChangeCameraInterface(true);
  }

  @Test
  public void setCameraResolutionPreferenceUpdateRepositoriesAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int resolutionPreferenceId = Constants.CAMERA_PREF_RESOLUTION_720_BACK_ID;

    presenter.setCameraResolutionPreference(resolutionPreferenceId);

    verify(mockedCameraSettingsRepository).setResolutionPreference("720p");
    verify(mockedProjectRepository).updateResolution(VideoResolution.Resolution.HD720);
    verify(mockedUserEventTracker).trackChangeResolution("720p");
  }

  @Test
  public void setCameraFrameRatePreferenceUpdateRepositoriesAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int frameRatePreferenceId = Constants.CAMERA_PREF_FRAME_RATE_30_ID;

    presenter.setCameraFrameRatePreference(frameRatePreferenceId);

    verify(mockedCameraSettingsRepository).setFrameRatePreference("30 fps");
    verify(mockedProjectRepository).updateFrameRate(VideoFrameRate.FrameRate.FPS30);
    verify(mockedUserEventTracker).trackChangeFrameRate("30 fps");
  }


  @Test
  public void setCameraQualityPreferenceUpdateRepositoriesProjectAndTracking() {
    CameraSettingsPresenter presenter = getCameraSettingsPresenter();
    int qualityPreferenceId = Constants.CAMERA_PREF_QUALITY_16_ID;

    presenter.setCameraQualityPreference(qualityPreferenceId);

    verify(mockedCameraSettingsRepository).setQualityPreference("16 Mbps");
    verify(mockedProjectRepository).updateQuality(VideoQuality.Quality.LOW);
    verify(mockedUserEventTracker).trackChangeQuality("16 Mbps");
  }

  private CameraSettingsPresenter getCameraSettingsPresenter() {
    return new CameraSettingsPresenter(mockedCameraSettingsListView,
        mockedUserEventTracker, mockedGetSettingListUseCase, mockedCameraSettingsRepository,
        mockedProjectRepository);
  }
}
