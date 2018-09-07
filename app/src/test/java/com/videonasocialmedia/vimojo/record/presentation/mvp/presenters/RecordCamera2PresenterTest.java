package com.videonasocialmedia.vimojo.record.presentation.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_RESOLUTION;
import static com.videonasocialmedia.vimojo.utils.Constants.FRONT_CAMERA_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by alvaro on 26/01/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class RecordCamera2PresenterTest {
  RecordCamera2Presenter presenter;

  @Mock RecordCamera2View mockedRecordView;
  @Mock Context mockedContext;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock SharedPreferences.Editor mockedEditor;
  @Mock AutoFitTextureView mockedTextureView;
  @Mock ApplyAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionUseCase;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock VideonaFormat mockedVideoFormat;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock Camera2WrapperListener mockedCamera2WrapperListener;
  @Mock private Activity mockedActivity;
  @Mock private Camera2Wrapper mockedCamera2Wrapper;
  @Mock private VideoDataSource mockedVideoRepository;
  @Mock private NewClipImporter mockedNewClipImporter;
  @Mock
  CameraSettingsDataSource mockedCameraSettingsRepository;
  @Mock CameraSettings mockedCameraSettings;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;

  @InjectMocks private RecordCamera2Presenter injectedPresenter;
  private Project currentProject;
  @Mock UpdateComposition mockedUpdateComposition;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(Log.class);
    setAProject();
    when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void constructorSetsUserTracker() {
    assertThat(injectedPresenter.userEventTracker, is(mockedUserEventTracker));
  }

  @Test
  public void initViewsWithControlsViewAndSettingsCameraViewSelectedCallsCorrectRecordView() {
    presenter = getRecordCamera2Presenter();
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.initViews();

    verify(mockedRecordView).hideRecordPointIndicator();
    verify(mockedRecordView).setCameraSettingSelected(anyString(), anyString(), anyString());
    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void initViewsDefaultInitializationCallsCorrectRecordView() {
    presenter = getRecordCamera2Presenter();
    CameraSettings cameraSettings = getCameraSettings();
    when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.initViews();

    verify(mockedRecordView).hideRecordPointIndicator();
    verify(mockedRecordView).setCameraSettingSelected(anyString(), anyString(), anyString());
    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void navigateEditOrGalleryButtonCallsGalleryIfThereIsNotVideos() {
    int numVideosInProject = currentProject.getVMComposition().getMediaTrack()
            .getNumVideosInProject();
    assertThat("There is not videos in project ", numVideosInProject, is(0));
    presenter = getRecordCamera2Presenter();

    presenter.navigateToEdit();

    verify(mockedRecordView).navigateTo(GalleryActivity.class);
  }

  @Test
  public void navigateEditOrGalleryCallsEditActivityIfThereAreVideosInProject()
      throws IllegalItemOnTrack {
    Video video = new Video("dcim/fakeVideo", Video.DEFAULT_VOLUME);
    MediaTrack track = currentProject.getMediaTrack();
    track.insertItem(video);
    track.insertItem(video);
    int numVideosInProject = currentProject.getVMComposition().getMediaTrack()
            .getNumVideosInProject();
    assertThat("There are videos in project", numVideosInProject, is(2));
    // TODO:(alvaro.martinez) 6/04/17 Assert also there are not videos pending to adapt, transcoding
    presenter = getRecordCamera2Presenter();

    presenter.navigateToEdit();

    verify(mockedRecordView).navigateTo(EditActivity.class);
  }

  @Test
  public void updateStatusBatteryCallsRecordViewShowBatteryStatus() {
    int statusBattery = 2;
    int levelBattery = 20;
    int scaleBattery = 100;
    presenter = getRecordCamera2Presenter();

    presenter.updateBatteryStatus(statusBattery, levelBattery, scaleBattery);

    verify(mockedRecordView).showBatteryStatus(Constants.BATTERY_STATUS.CHARGING, 20);
  }

  @Test
  public void getPercentBateryIfLevelBatteryIs2AndScaleBatteryIs10() {
    int levelBattery = 2;
    int scaleBattery = 10;
    int percentBattery;
    presenter = getRecordCamera2Presenter();

    percentBattery = presenter.getPercentLevel(levelBattery, scaleBattery);

    assertThat("percentLevel will be 20", percentBattery, is(20));

  }

  @Test
  public void getBatteryStatusreturnNoChargingIfStatusIsNot2() {
    int percentBattery = 20;
    int statusBattery = 3; // BatteryManager.BATTERY_STATUS_CHARGING
    Constants.BATTERY_STATUS status;
    presenter = getRecordCamera2Presenter();

    status = presenter.getBatteryStatus(statusBattery, percentBattery);

    assertNotEquals("Status is not charging", status, is(Constants.BATTERY_STATUS.CHARGING));
  }

  @Test
  public void getBatteryStatusreturnChargingIfStatusIs2() {
    int percentBattery = 20;
    int statusBattery = 2; // BatteryManager.BATTERY_STATUS_CHARGING
    Constants.BATTERY_STATUS status;
    presenter = getRecordCamera2Presenter();

    status = presenter.getBatteryStatus(statusBattery, percentBattery);

    assertThat("Status is charging", status, is(Constants.BATTERY_STATUS.CHARGING));
  }


  @Test
  public void getBatteryStatusreturnLowStatusIfLevelIsLessThan10AndStatusIsNotCharging() {
    int percentBattery = 10;
    Constants.BATTERY_STATUS status;
    presenter = getRecordCamera2Presenter();

    status = presenter.getStatusNotCharging(percentBattery);

    assertThat("Level will be CRITICAL", status, is(Constants.BATTERY_STATUS.CRITICAL));
  }

  @Test
  public void setMicrophoneStatusCallsRecordViewShowExternalMicrophoneConnected(){
    int stateHeadSetPlug = 1; // jack connected
    int microphone = 1; // microphone connected
    presenter = getRecordCamera2Presenter();

    presenter.setMicrophoneStatus(stateHeadSetPlug, microphone);

    verify(mockedRecordView).showExternalMicrophoneConnected();
  }

  @Test
  public void setMicrophoneStatusCallsRecordViewShowSmartPhoneMicrophoneConnected() {
    int stateHeadSetPlug = 0; // jack is not connected
    int microphone = 1; // microphone connected
    presenter = getRecordCamera2Presenter();

    presenter.setMicrophoneStatus(stateHeadSetPlug, microphone);

    verify(mockedRecordView).showSmartphoneMicrophoneWorking();
  }

  @Ignore // // TODO:(alvaro.martinez) 25/07/17 How to mock Handler post delayed.
  // java.lang.RuntimeException: Method postDelayed in android.os.Handler not mocked.
  @Test
  public void startRecordCallsTrackVideoStartRecording() {
    presenter = getRecordCamera2Presenter();
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        Camera2Wrapper.RecordStartedCallback listener = invocation.getArgument(0);
        listener.onRecordStarted();
        return null;
      }
    }).when(mockedCamera2Wrapper).startRecordingVideo(
            any(Camera2Wrapper.RecordStartedCallback.class));

    presenter.startRecord();

    verify(mockedUserEventTracker).trackVideoStartRecording();
  }

  @Test
  public void stopRecordCallsTrackVideoStopRecording() {
    presenter = getRecordCamera2Presenter();
    when(mockedSharedPreferences.getInt(anyString(), anyInt())).thenReturn(0);
    when(mockedEditor.commit()).thenReturn(true);
    when(mockedSharedPreferences.edit()).thenReturn(mockedEditor);

    presenter.stopRecord();

    verify(mockedUserEventTracker).trackVideoStopRecording();
  }

  @Test
  public void stopRecordCallsTrackTotalVideosRecordedSuperProperty() {
    presenter = getRecordCamera2Presenter();
    when(mockedSharedPreferences.getInt(anyString(), anyInt())).thenReturn(0);
    when(mockedEditor.commit()).thenReturn(true);
    when(mockedSharedPreferences.edit()).thenReturn(mockedEditor);

    presenter.stopRecord();

    verify(mockedUserEventTracker).trackTotalVideosRecordedSuperProperty();
  }

  @Test
  public void stopRecordCallsTrackVideoRecorded() {
    presenter = getRecordCamera2Presenter();
    when(mockedSharedPreferences.getInt(anyString(), anyInt())).thenReturn(0);
    when(mockedEditor.commit()).thenReturn(true);
    when(mockedSharedPreferences.edit()).thenReturn(mockedEditor);

    presenter.stopRecord();

    verify(mockedUserEventTracker).trackVideoRecorded(eq(currentProject), anyInt());
  }

  @Test
  public void stopRecordCallsTrackVideoRecordedUserTraits() {
    presenter = getRecordCamera2Presenter();
    when(mockedSharedPreferences.getInt(anyString(), anyInt())).thenReturn(0);
    when(mockedEditor.commit()).thenReturn(true);
    when(mockedSharedPreferences.edit()).thenReturn(mockedEditor);

    presenter.stopRecord();

    verify(mockedUserEventTracker).trackVideoRecordedUserTraits();
  }

  @Test
  public void changeCameraCallsTrackChangeCameraAndUpdateCameraSettingsRepository(){
    presenter = getRecordCamera2Presenter();
    int cameraIdSelected = FRONT_CAMERA_ID;
    CameraSettings cameraSettings = getCameraSettings();
    PowerMockito.when(mockedCameraSettingsRepository.getCameraSettings()).thenReturn(cameraSettings);

    presenter.switchCamera();

    verify(mockedUserEventTracker).trackChangeCamera(anyBoolean());
    verify(mockedCameraSettingsRepository).setCameraIdSelected(cameraSettings, cameraIdSelected);
  }


  @Test
  public void changeFlashStateCallsTrackFlashCamera(){
    presenter = getRecordCamera2Presenter();
    boolean isFlashSelected = false;

    presenter.toggleFlash(isFlashSelected);

    verify(mockedUserEventTracker).trackChangeFlashMode(anyBoolean());
  }

  private void setAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
    if(currentProject.getVMComposition().getProfile() == null){
      currentProject.setProfile(profile);
    }
  }

  @NonNull
  private RecordCamera2Presenter getRecordCamera2Presenter() {
    RecordCamera2Presenter recordCamera2Presenter = new RecordCamera2Presenter(mockedActivity,
            mockedRecordView, mockedUserEventTracker, mockedSharedPreferences,
            mockedAddVideoToProjectUseCase, mockedNewClipImporter, mockedCamera2Wrapper,
            mockedCameraSettingsRepository, mockedProjectInstanceCache, mockedUpdateComposition);
    recordCamera2Presenter.currentProject = currentProject;
    return recordCamera2Presenter;
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
    CameraSettings cameraSettings = new CameraSettings(resolutionSetting, frameRateSetting, quality,
        interfaceSelected, cameraIdSelected);
    return cameraSettings;
  }
}
