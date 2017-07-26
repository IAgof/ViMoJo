package com.videonasocialmedia.vimojo.record.presentation.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

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
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToVideoFormatUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 26/01/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecordCamera2PresenterTest {
  RecordCamera2Presenter presenter;

  @Mock RecordCamera2View mockedRecordView;
  @Mock Context mockedContext;
  @Mock AutoFitTextureView mockedTextureView;
  String directorySaveVideos;
  @Mock UpdateVideoRepositoryUseCase mockedUpdateVideoRepositoryUseCase;
  @Mock LaunchTranscoderAddAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionUseCase;
  @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideoFormatFromCurrentProjectUseCase;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock AdaptVideoRecordedToVideoFormatUseCase mockedAdaptVideoRecordedToVideoFormatUseCase;
  @Mock VideonaFormat mockedVideoFormat;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock Camera2WrapperListener mockedCamera2WrapperListener;
  int rotation = 0;
  Drawable fadeTransition;
  boolean isFadeActivated;
  @Mock private Activity mockedActivity;
  @Mock private Camera2Wrapper mockedCamera2Wrapper;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @After
  public void tearDown() {
    Project.getInstance(null, null, null).clear();
  }

  @Test
  public void initViewsWithControlsViewAndSettingsCameraViewSelectedCallsCorrectRecordView() {
    presenter = getRecordCamera2Presenter();

    presenter.initViews();

    verify(mockedRecordView).hideChronometer();
    verify(mockedRecordView).setResolutionSelected(720);
    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void initViewsDefaultInitializationCallsCorrectRecordView() {
    presenter = getRecordCamera2Presenter();

    presenter.initViews();

    verify(mockedRecordView).hideChronometer();
    verify(mockedRecordView).setResolutionSelected(720);
    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void navigateEditOrGalleryButtonCallsGalleryIfThereIsNotVideos() {
    getAProject().clear();
    int numVideosInProject = getAProject().getVMComposition().getMediaTrack().getNumVideosInProject();
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
    int numVideosInProject = getAProject().getVMComposition().getMediaTrack().getNumVideosInProject();
    assertThat("There are videos in project", numVideosInProject, is(2));
    // TODO:(alvaro.martinez) 6/04/17 Assert also there are not videos pending to adapt, transcoding
    presenter = getRecordCamera2Presenter();

    presenter.navigateToEdit();

    verify(mockedRecordView).navigateTo(EditActivity.class);
  }

  @Ignore
  @Test
  public void navigateEditOrGalleryCallsShowProgressAdaptingVideoIfThereAreVideosPendingToAdapt()
      throws IllegalItemOnTrack, IOException {
    // TODO:(alvaro.martinez) 6/04/17  Prepare this test, i don't know how to mock adapting video process and fake futures.isDone to false.
    Video video = new Video("dcim/fakeVideo", Video.DEFAULT_VOLUME);
    Project project = getAProject();
    MediaTrack track = project.getMediaTrack();
    track.insertItem(video);
    track.insertItem(video);
    int numVideos = getAProject().getVMComposition().getMediaTrack().getNumVideosInProject();
    assertThat("There are videos in project", numVideos, is(2));
    mockedAdaptVideoRecordedToVideoFormatUseCase.adaptVideo(video, mockedVideoFormat,
        directorySaveVideos, rotation, fadeTransition, isFadeActivated,
        mockedTranscoderHelperListener);
    presenter = getRecordCamera2Presenter();

    presenter.navigateToEdit();

    verify(mockedRecordView).showProgressAdaptingVideo();
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
  public void setMicrophoneStatusCallsRecordViewShowSmartPhoneMicrophoneConnected(){
    int stateHeadSetPlug = 0; // jack is not connected
    int microphone = 1; // microphone connected
    presenter = getRecordCamera2Presenter();

    presenter.setMicrophoneStatus(stateHeadSetPlug, microphone);

    verify(mockedRecordView).showSmartphoneMicrophoneWorking();
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path",
        Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25));
  }

  @NonNull
  private RecordCamera2Presenter getRecordCamera2Presenter() {
    return new RecordCamera2Presenter(mockedActivity,
            mockedRecordView, mockedUpdateVideoRepositoryUseCase,
            mockedLaunchTranscoderAddAVTransitionUseCase,
            mockedGetVideoFormatFromCurrentProjectUseCase,
            mockedAddVideoToProjectUseCase, mockedAdaptVideoRecordedToVideoFormatUseCase,
            mockedCamera2Wrapper);
  }

}
