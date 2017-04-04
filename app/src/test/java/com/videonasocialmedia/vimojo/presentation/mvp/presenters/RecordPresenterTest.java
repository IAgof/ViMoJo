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
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.RecordView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

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
        mockedUpdateVideoRepositoryUseCase, mockedLaunchTranscoderAddAVTransitionsUseCase);

    Project project = getAProject();

    assertThat(recordPresenter.currentProject, is(project));
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

  @Test
  public void videoToLaunchAVTransitionTempFileUpdateVideoTempPath(){
    getAProject().clear();
    Project project = getAProject();
    project.setAudioFadeTransitionActivated(true);
    String path = "media/path";
    assertThat("Audio transition is activated ", project.isAudioFadeTransitionActivated(), is(true));

    recordPresenter = new RecordPresenter(mockedContext, mockedRecordView, mockedUserEventTracker,
        mockedGLCameraview, mockedSharedPreferences, externalIntent, mockedAddVideoToProjectUseCase,
        mockedUpdateVideoRepositoryUseCase, mockedLaunchTranscoderAddAVTransitionsUseCase);

    Video video = new Video(path);
    String tempPath = video.getTempPath();

    recordPresenter.videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());

    assertNotEquals("Update tempPath ", tempPath, video.getTempPath());
  }

}
