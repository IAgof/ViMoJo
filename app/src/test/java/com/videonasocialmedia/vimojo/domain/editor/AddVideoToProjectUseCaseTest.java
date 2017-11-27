package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnLaunchAVTransitionTempFileListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddVideoToProjectUseCaseTest {
  @Mock ProjectRealmRepository mockedProjectRepository;
  @Mock OnLaunchAVTransitionTempFileListener mockedLaunchAVTransitionTempFileListener;
  @Mock OnAddMediaFinishedListener mockedOnAddMediaFinishedListener;
  @Mock ApplyAVTransitionsUseCase mockedApplyAVTransitionsUseCase;
  @InjectMocks AddVideoToProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    Project.getInstance(null, null, null, null).clear();
  }

  @Test
  public void testAddVideoToProjectAtPositionCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null, null);
    Video video = new Video("media/path", 1f);

    injectedUseCase.addVideoToProjectAtPosition(video, 0, mockedOnAddMediaFinishedListener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testAddVideoListToTrackCallsUpdateProject() {
    Project currentProject = getAProject();
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();
        getOnLaunchAVTransitionTempFileListener();

    injectedUseCase.addVideoListToTrack(videoList, listener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void ifAudioTransitionActivatedAddVideoToProjectCallsApplyAVTransitions() {
    Project project = getAProject();
    project.getVMComposition().setAudioFadeTransitionActivated(true);
    assertThat("Audio transition is activated ",
            project.getVMComposition().isAudioFadeTransitionActivated(), is(true));
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(videoList, listener);

    verify(mockedApplyAVTransitionsUseCase).applyAVTransitions(
            eq(project.getVMComposition().getDrawableFadeTransitionVideo()), eq(video),
            // FIXME: 1/09/17 videonaFormat class is a new object each time is retrieved
            any(VideonaFormat.class),
            eq(project.getProjectPathIntermediateFileAudioFade()),
            any(ApplyAVTransitionsUseCase.AVTransitionsApplierListener.class));
  }

  @Test
  public void ifVideoTransitionActivatedAddVideoToProjectCallsApplyAVTransitions() {
    Project project = getAProject();
    project.getVMComposition().setVideoFadeTransitionActivated(true);
    assertThat("Video transition is activated ",
            project.getVMComposition().isVideoFadeTransitionActivated(), is(true));
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(videoList, listener);

    verify(mockedApplyAVTransitionsUseCase).applyAVTransitions(
            eq(project.getVMComposition().getDrawableFadeTransitionVideo()), eq(video),
            // FIXME: 1/09/17 videonaFormat class is a new object each time is retrieved
            any(VideonaFormat.class),
            eq(project.getProjectPathIntermediateFileAudioFade()),
            any(ApplyAVTransitionsUseCase.AVTransitionsApplierListener.class));
  }

  @Test
  public void ifAVTransitionNotActivatedAddVideoToProjectNotCallVideoToLaunchAVTransitionTempFile() {
    getAProject().clear();
    Project project = getAProject();
    assertThat("Audio transition is not activated ",
            project.getVMComposition().isAudioFadeTransitionActivated(), is(false));
    assertThat("Video transition is not activated ",
            project.getVMComposition().isVideoFadeTransitionActivated(), is(false));
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(videoList, listener);

    verify(mockedLaunchAVTransitionTempFileListener, never())
            .videoToLaunchAVTransitionTempFile(video,
                    project.getProjectPathIntermediateFileAudioFade());
  }

  @NonNull
  private OnAddMediaFinishedListener getOnAddMediaFinishedListener() {
    return new OnAddMediaFinishedListener() {
      @Override
      public void onAddMediaItemToTrackError() {

      }

      @Override
      public void onAddMediaItemToTrackSuccess(Media media) {

      }
    };
  }

  @NonNull
  private OnLaunchAVTransitionTempFileListener getOnLaunchAVTransitionTempFileListener() {
    return new OnLaunchAVTransitionTempFileListener() {
      @Override
      public void videoToLaunchAVTransitionTempFile(Video video,
                                                    String intermediatesTempAudioFadeDirectory) {

      }
    };
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
