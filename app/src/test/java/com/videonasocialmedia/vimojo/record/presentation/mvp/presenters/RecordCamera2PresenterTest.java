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
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
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
  @Mock
  ApplyAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionUseCase;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock VideonaFormat mockedVideoFormat;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock Camera2WrapperListener mockedCamera2WrapperListener;
  @Mock private Activity mockedActivity;
  @Mock private Camera2Wrapper mockedCamera2Wrapper;
  @Mock private VideoRepository mockedVideoRepository;
  @Mock private NewClipImporter mockedNewClipImporter;


  @InjectMocks private RecordCamera2Presenter injectedPresenter;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(Log.class);
    getAProject();
  }

  @After
  public void tearDown() {
    Project.getInstance(null, null, null, null).clear();
  }

  @Test
  public void constructorSetsUserTracker() {
    assertThat(injectedPresenter.userEventTracker, is(mockedUserEventTracker));
  }

  @Test
  public void initViewsWithControlsViewAndSettingsCameraViewSelectedCallsCorrectRecordView() {
    presenter = getRecordCamera2Presenter();

    presenter.initViews();

    verify(mockedRecordView).hideRecordPointIndicator();
    verify(mockedRecordView).setResolutionSelected(720);
    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void initViewsDefaultInitializationCallsCorrectRecordView() {
    presenter = getRecordCamera2Presenter();

    presenter.initViews();

    verify(mockedRecordView).hideRecordPointIndicator();
    verify(mockedRecordView).setResolutionSelected(720);
    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void navigateEditOrGalleryButtonCallsGalleryIfThereIsNotVideos() {
    getAProject().clear();
    int numVideosInProject = getAProject().getVMComposition().getMediaTrack()
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
    Project project = getAProject();
    MediaTrack track = project.getMediaTrack();
    track.insertItem(video);
    track.insertItem(video);
    int numVideosInProject = getAProject().getVMComposition().getMediaTrack()
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

    verify(mockedUserEventTracker).trackVideoRecorded(eq(getAProject()), anyInt());
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
  public void changeCameraCallsTrackChangeCamera(){
    presenter = getRecordCamera2Presenter();

    presenter.switchCamera();

    verify(mockedUserEventTracker).trackChangeCamera(anyBoolean());
  }

  @Test
  public void changeFlashStateCallsTrackFlashCamera(){
    presenter = getRecordCamera2Presenter();
    boolean isFlashSelected = false;

    presenter.toggleFlash(isFlashSelected);

    verify(mockedUserEventTracker).trackChangeFlashMode(anyBoolean());
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    Project project = Project.getInstance("title", "/path", "private/path", profile);
    if(project.getVMComposition().getProfile() == null){
      project.setProfile(profile);
    }
    return project;
  }

  @NonNull
  private RecordCamera2Presenter getRecordCamera2Presenter() {
    return new RecordCamera2Presenter(mockedActivity,
            mockedRecordView, mockedUserEventTracker, mockedSharedPreferences,
            mockedAddVideoToProjectUseCase, mockedNewClipImporter, mockedCamera2Wrapper);
  }
}
