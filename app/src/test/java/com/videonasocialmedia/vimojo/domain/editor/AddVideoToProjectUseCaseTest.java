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
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnLaunchAVTransitionTempFileListener;
import com.videonasocialmedia.vimojo.repository.project.datasource.ProjectRealmRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
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
  private Project currentProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void testAddVideoToProjectAtPositionCallsUpdateProject() {
    Video video = new Video("media/path", 1f);

    injectedUseCase.addVideoToProjectAtPosition(currentProject, video, 0,
        mockedOnAddMediaFinishedListener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testAddVideoListToTrackCallsUpdateProject() {
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();
        getOnLaunchAVTransitionTempFileListener();

    injectedUseCase.addVideoListToTrack(currentProject, videoList, listener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void ifAudioTransitionActivatedAddVideoToProjectCallsApplyAVTransitions() {
    currentProject.getVMComposition().setAudioFadeTransitionActivated(true);
    assertThat("Audio transition is activated ",
        currentProject.getVMComposition().isAudioFadeTransitionActivated(), is(true));
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(currentProject, videoList, listener);

    verify(mockedApplyAVTransitionsUseCase).applyAVTransitions(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()), eq(video),
            // FIXME: 1/09/17 videonaFormat class is a new object each time is retrieved
            any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()),
            any(ApplyAVTransitionsUseCase.AVTransitionsApplierListener.class));
  }

  @Test
  public void ifVideoTransitionActivatedAddVideoToProjectCallsApplyAVTransitions() {
    currentProject.getVMComposition().setVideoFadeTransitionActivated(true);
    assertThat("Video transition is activated ",
        currentProject.getVMComposition().isVideoFadeTransitionActivated(), is(true));
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(currentProject, videoList, listener);

    verify(mockedApplyAVTransitionsUseCase).applyAVTransitions(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()), eq(video),
            // FIXME: 1/09/17 videonaFormat class is a new object each time is retrieved
            any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()),
            any(ApplyAVTransitionsUseCase.AVTransitionsApplierListener.class));
  }

  @Test
  public void ifAVTransitionNotActivatedAddVideoToProjectNotCallVideoToLaunchAVTransitionTempFile() {
    assertThat("Audio transition is not activated ",
        currentProject.getVMComposition().isAudioFadeTransitionActivated(), is(false));
    assertThat("Video transition is not activated ",
        currentProject.getVMComposition().isVideoFadeTransitionActivated(), is(false));
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(currentProject, videoList, listener);

    verify(mockedLaunchAVTransitionTempFileListener, never())
            .videoToLaunchAVTransitionTempFile(video,
                currentProject.getProjectPathIntermediateFileAudioFade());
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

  public void getAProject() {
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path",
        new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
