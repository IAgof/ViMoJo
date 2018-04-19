package com.videonasocialmedia.vimojo.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
  shadows = {ShadowMultiDex.class}, packageName = "com.videonasocialmedia.vimojo.debug")
public class CreateDefaultProjectUseCaseTest {

  @Mock ProjectRepository mockedProjectRepository;
  @Mock ProfileRepository mockedProfileRepository;
  @Mock VimojoApplication mockedVimojoApplication;
  @Mock CameraSettingsRepository mockedCameraSettingsRepository;
  @InjectMocks CreateDefaultProjectUseCase injectedUseCase;
  private CameraSettings cameraSettings;
  private Project currentProject;
  @Mock Project mockedProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    initCameraPreferences();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);
    getAProject();
    when(mockedProjectRepository.getCurrentProject()).thenReturn(currentProject);
  }

  private void initCameraPreferences() {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, false);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, false);
    ResolutionSetting resolutionSetting = new ResolutionSetting(
        Constants.DEFAULT_CAMERA_SETTING_RESOLUTION, resolutionsSupportedMap);
    HashMap<Integer, Boolean> frameRateSupportedMap = new HashMap<>();
    frameRateSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, false);
    frameRateSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, false);
    frameRateSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, true);
    FrameRateSetting frameRateSetting = new FrameRateSetting(
        Constants.DEFAULT_CAMERA_SETTING_FRAME_RATE, frameRateSupportedMap);
    String quality = Constants.DEFAULT_CAMERA_SETTING_QUALITY;
    String interfaceSelected = Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
    int cameraIdSelected = Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED;
    cameraSettings = new CameraSettings(resolutionSetting,
            frameRateSetting, quality, interfaceSelected, cameraIdSelected);
  }

//  @Test
//  public void loadOrCreateProjectCallsGetCurrentProjectIfInstanceIsNull() {
//    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
//            -1, Profile.ProfileType.pro);
//    Project currentProject = new Project("title", "root/path", profile);
//    assert Project.INSTANCE == null;
//    doReturn(currentProject).when(mockedProjectRepository).getCurrentProject();
//
//    injectedUseCase.loadOrCreateProject("root/path");
//
//    verify(mockedProjectRepository).getCurrentProject();
//    assertThat(Project.INSTANCE, is(currentProject));
//    verify(mockedProjectRepository).update(currentProject);
//  }

  @Test
  public void loadOrCreateUpdatesProjectRepository() {
    CreateDefaultProjectUseCase createDefaultProjectUseCase = getInjectedUseCase();

    createDefaultProjectUseCase.loadOrCreateProject("root/path", "private/path",
        false);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void createProjectUpdatesProjectRepository() {
    CreateDefaultProjectUseCase createDefaultProjectUseCase = getInjectedUseCase();

    createDefaultProjectUseCase.createProject("root/path", "private/path", false);

    verify(mockedProjectRepository).update(any(Project.class));
  }

  @Ignore // TODO: 20/3/18 Study how to test project is set with watermark feature
  @Test
  public void createProjectActivatesWatermarkIfIsFeatured() {
    boolean isWatermarkFeatured = true;
    assertThat(currentProject.hasWatermark(), is(false));
    CreateDefaultProjectUseCase createDefaultProjectUseCase = getInjectedUseCase();

    createDefaultProjectUseCase.createProject("root/path", "private/path", isWatermarkFeatured);

    assertThat("Watermark is activated", currentProject.hasWatermark(), is(true));
  }

  @Ignore // TODO: 20/3/18 Study how to test project is set with watermark feature
  @Test
  public void loadOrCreateProjectActivatesWatermarkIfIsFeatured() {
    boolean isWatermarkFeatured = true;
    assertThat(currentProject.hasWatermark(), is(false));
    CreateDefaultProjectUseCase createDefaultProjectUseCase = getInjectedUseCase();

    createDefaultProjectUseCase.loadOrCreateProject("root/path",
        "private/path", isWatermarkFeatured);

    verify(mockedProject).setWatermarkActivated(isWatermarkFeatured);
    //assertThat("Watermark is activated", currentProject.hasWatermark(), is(true));
  }

  private CreateDefaultProjectUseCase getInjectedUseCase() {
    return new CreateDefaultProjectUseCase(mockedProjectRepository, mockedProfileRepository);
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
