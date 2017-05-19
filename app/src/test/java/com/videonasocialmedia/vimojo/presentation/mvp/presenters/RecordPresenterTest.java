package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.RecordView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 22/03/17.
 */

public class RecordPresenterTest {

  @Mock
  Context mockedContext;
  @Mock
  RecordView mockedRecordView;
  @Mock
  UserEventTracker mockedUserEventTracker;
  @Mock
  GLCameraView mockedGLCameraview;
  @Mock
  SharedPreferences mockedSharedPreferences;
  @Mock
  AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock
  UpdateVideoRepositoryUseCase mockedUpdateVideoRepositoryUseCase;
  @Mock
  GetVideonaFormatFromCurrentProjectUseCase mockedGetVideonaFormatFromCurrentProjectUseCase;
  @Mock
  LaunchTranscoderAddAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionsUseCase;
  boolean externalIntent;

  private RecordPresenter recordPresenter;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void constructorSetsCurrentProject() {

    recordPresenter = new RecordPresenter(mockedContext, mockedRecordView, mockedUserEventTracker,
        mockedGLCameraview, mockedSharedPreferences, externalIntent, mockedAddVideoToProjectUseCase,
        mockedUpdateVideoRepositoryUseCase, mockedGetVideonaFormatFromCurrentProjectUseCase,
        mockedLaunchTranscoderAddAVTransitionsUseCase);

    Project project = getAProject();

    assertThat(recordPresenter.currentProject, is(project));
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

  @Test
  public void videoToLaunchAVTransitionTempFileUpdateVideoTempPath(){
    Project project = getAProject();
    project.setAudioFadeTransitionActivated(true);
    String path = "media/path";
    assertThat("Audio transition is activated ", project.isAudioFadeTransitionActivated(), is(true));

    recordPresenter = new RecordPresenter(mockedContext, mockedRecordView, mockedUserEventTracker,
        mockedGLCameraview, mockedSharedPreferences, externalIntent, mockedAddVideoToProjectUseCase,
        mockedUpdateVideoRepositoryUseCase, mockedGetVideonaFormatFromCurrentProjectUseCase,
        mockedLaunchTranscoderAddAVTransitionsUseCase);

    Video video = new Video(path);
    String tempPath = video.getTempPath();

    recordPresenter.videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());

    assertNotEquals("Update tempPath ", tempPath, video.getTempPath());
  }

  @Test
  public void updateStatusBatteryCallsRecordViewShowBatteryStatus(){
    int statusBattery= 2;
    int levelBattery=20;
    recordPresenter = new RecordPresenter(mockedContext, mockedRecordView, mockedUserEventTracker,
        mockedGLCameraview, mockedSharedPreferences, externalIntent, mockedAddVideoToProjectUseCase,
        mockedUpdateVideoRepositoryUseCase, mockedGetVideonaFormatFromCurrentProjectUseCase,
        mockedLaunchTranscoderAddAVTransitionsUseCase);

    recordPresenter.updateBatteryStatus(statusBattery, levelBattery);

    verify(mockedRecordView).showBatteryStatus(Constants.BATTERY_STATUS_ENUM.CHARGING);
  }

  @Test
  public void getBatteryStatusreturnLowStatusIfLevelIsLessThan15(){
    int levelBattery=10;
    int statusBattery= 1;
    Constants.BATTERY_STATUS_ENUM status;
    recordPresenter = new RecordPresenter(mockedContext, mockedRecordView, mockedUserEventTracker,
        mockedGLCameraview, mockedSharedPreferences, externalIntent, mockedAddVideoToProjectUseCase,
        mockedUpdateVideoRepositoryUseCase, mockedGetVideonaFormatFromCurrentProjectUseCase,
        mockedLaunchTranscoderAddAVTransitionsUseCase);

   status = recordPresenter.getBatteryStatus(statusBattery, levelBattery);

    assertThat("Level will be low", status, is(Constants.BATTERY_STATUS_ENUM.LOW));
  }

  @Test
  public void getBatteryStatusreturnChargingIfStatusIs2(){
    int levelBattery=10;
    int statusBattery= 2; // BatteryManager.BATTERY_STATUS_CHARGING
    Constants.BATTERY_STATUS_ENUM status;
    recordPresenter = new RecordPresenter(mockedContext, mockedRecordView, mockedUserEventTracker,
        mockedGLCameraview, mockedSharedPreferences, externalIntent, mockedAddVideoToProjectUseCase,
        mockedUpdateVideoRepositoryUseCase, mockedGetVideonaFormatFromCurrentProjectUseCase,
        mockedLaunchTranscoderAddAVTransitionsUseCase);

    status = recordPresenter.getBatteryStatus(statusBattery, levelBattery);

    assertThat("Status is charging", status, is(Constants.BATTERY_STATUS_ENUM.CHARGING));
  }


}
