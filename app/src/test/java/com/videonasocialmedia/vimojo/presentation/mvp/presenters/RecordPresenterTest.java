package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.RecordView;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
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
  @Mock Context mockedContext;
  @Mock RecordView mockedRecordView;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock GLCameraView mockedGLCameraview;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock LaunchTranscoderAddAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionsUseCase;
  @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideonaFormatFromCurrentProjectUseCase;
  @Mock private VideoRepository mockedVideoRepository;

  boolean externalIntent;
  private RecordPresenter recordPresenter;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void constructorSetsCurrentProject() {
    recordPresenter = getRecordPresenter();

    Project project = getAProject();

    assertThat(recordPresenter.currentProject, is(project));
  }

  @NonNull
  private RecordPresenter getRecordPresenter() {
    return new RecordPresenter(mockedContext, mockedRecordView, mockedUserEventTracker,
        mockedGLCameraview, mockedSharedPreferences, externalIntent, mockedAddVideoToProjectUseCase,
        mockedVideoRepository, mockedLaunchTranscoderAddAVTransitionsUseCase,
        mockedGetVideonaFormatFromCurrentProjectUseCase);
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

  @Test
  public void videoToLaunchAVTransitionTempFileUpdateVideoTempPath(){
    getAProject().clear();
    Project project = getAProject();
    project.getVMComposition().setAudioFadeTransitionActivated(true);
    String path = "media/path";
    assertThat("Audio transition is activated ",
            project.getVMComposition().isAudioFadeTransitionActivated(), is(true));
    recordPresenter = getRecordPresenter();
    Video video = new Video(path, 1f);
    String tempPath = video.getTempPath();

    recordPresenter.videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());

    assertNotEquals("Update tempPath ", tempPath, video.getTempPath());
  }

  @Test
  public void updateStatusBatteryCallsRecordViewShowBatteryStatus() {
    int statusBattery = 2;
    int levelBattery = 20;
    int scaleBattery = 100;
    recordPresenter = getRecordPresenter();

    recordPresenter.updateBatteryStatus(statusBattery, levelBattery, scaleBattery);

    verify(mockedRecordView).showBatteryStatus(Constants.BATTERY_STATUS.CHARGING, 20);
  }

  @Test
  public void getPercentBateryIfLevelBatteryIs2AndScaleBatteryIs10() {
    int levelBattery = 2;
    int scaleBattery = 10;
    int percentBattery;
    recordPresenter = getRecordPresenter();

    percentBattery = recordPresenter.getPercentLevel(levelBattery, scaleBattery);

    assertThat("percentLevel will be 20", percentBattery, is(20));

  }

  @Test
  public void getBatteryStatusreturnNoChargingIfStatusIsNot2() {
    int percentBattery = 20;
    int statusBattery = 3; // BatteryManager.BATTERY_STATUS_CHARGING
    Constants.BATTERY_STATUS status;
    recordPresenter = getRecordPresenter();

    status = recordPresenter.getBatteryStatus(statusBattery, percentBattery);

    assertNotEquals("Status is not charging", status, is(Constants.BATTERY_STATUS.CHARGING));
  }

  @Test
  public void getBatteryStatusreturnChargingIfStatusIs2() {
    int percentBattery = 20;
    int statusBattery = 2; // BatteryManager.BATTERY_STATUS_CHARGING
    Constants.BATTERY_STATUS status;
    recordPresenter = getRecordPresenter();

    status = recordPresenter.getBatteryStatus(statusBattery, percentBattery);

    assertThat("Status is charging", status, is(Constants.BATTERY_STATUS.CHARGING));
  }


  @Test
  public void getBatteryStatusreturnLowStatusIfLevelIsLessThan10AndStatusIsNotCharging() {
    int percentBattery = 10;
    Constants.BATTERY_STATUS status;
    recordPresenter = getRecordPresenter();

    status = recordPresenter.getStatusNotCharging(percentBattery);

    assertThat("Level will be CRITICAL", status, is(Constants.BATTERY_STATUS.CRITICAL));
  }
}
