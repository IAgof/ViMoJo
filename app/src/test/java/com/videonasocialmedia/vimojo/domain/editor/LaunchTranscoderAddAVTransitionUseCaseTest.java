package com.videonasocialmedia.vimojo.domain.editor;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.ApplyAudioFadeInFadeOutToVideo;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoDataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 22/03/17.
 */

public class LaunchTranscoderAddAVTransitionUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock ApplyAudioFadeInFadeOutToVideo mockedApplyAudioFadeInFadeOutToVideo;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock Drawable mockedDrawableFadeTransition;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;

  private final VideonaFormat videonaFormat = new VideonaFormat();

  @Mock
  VideoDataSource mockVideoRepository;
  @Mock Video mockedVideo;
  @Mock Project mockedProject;
  @Mock private ApplyAVTransitionsUseCase.AVTransitionsApplierListener mockedAVTransitionsApplierListener;
  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock
  VideoDataSource mockedVideoRepository;

  private Project currentProject;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  @Test
  public void ifProjectHasVideoTransitionActivatedCallsGenerateOutputVideoWithAVTransitionsAndUpdateRepository()
          throws IllegalItemOnTrack {
    currentProject.getVMComposition().setVideoFadeTransitionActivated(true);
    Video video = new Video("media/path", 1f);
    Track track = currentProject.getMediaTrack();
    track.insertItem(video);
    assertThat("Project has videos ", currentProject.getVMComposition().hasVideos(), is(true));
    ApplyAVTransitionsUseCase injectedApplyAVTransitionsUseCase = getInjectedApplyAVTransitionsUseCase();
    injectedApplyAVTransitionsUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedApplyAVTransitionsUseCase.applyAVTransitions(
            mockedDrawableFadeTransition, video, videonaFormat,
        currentProject.getProjectPathIntermediateFileAudioFade(), mockedAVTransitionsApplierListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithAVTransitionsAsync(
            eq(mockedDrawableFadeTransition),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            eq(videonaFormat), eq(currentProject.getProjectPathIntermediateFileAudioFade()),
            any(TranscoderHelperListener.class));
    verify(mockedTranscoderHelper, never()).generateOutputVideoWithAudioTransitionAsync(video,
        currentProject.getProjectPathIntermediateFileAudioFade(), mockedTranscoderHelperListener);
    verify(mockedVideoRepository).update(video);
  }

  // Ignore test, verify not working, mock not used. Unify audio transcoder tasks in mediaTranscoder
  // and came back to this test.
  @Ignore
  @Test
  public void ifProjectHasAudioTransitionActivatedCallsGenerateOutputVideoWithAudioTransitions()
          throws IllegalItemOnTrack {
    currentProject.getVMComposition().setAudioFadeTransitionActivated(true);
    currentProject.getVMComposition().setVideoFadeTransitionActivated(false);
    Video video = new Video("media/path", 1f);
    Track track = currentProject.getMediaTrack();
    track.insertItem(video);
    assertThat("Project has videos ", currentProject.getVMComposition().hasVideos(), is(true));
    assertThat("Video transition is not activated ",
        currentProject.getVMComposition().isVideoFadeTransitionActivated(), is(false));
    assertThat("Audio transition is activated ",
        currentProject.getVMComposition().isAudioFadeTransitionActivated(), is(true));
    ApplyAVTransitionsUseCase injectedApplyAVTransitionsUseCase = getInjectedApplyAVTransitionsUseCase();
    injectedApplyAVTransitionsUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedApplyAVTransitionsUseCase.applyAVTransitions(
            mockedDrawableFadeTransition, video, videonaFormat,
        currentProject.getProjectPathIntermediateFileAudioFade(), mockedAVTransitionsApplierListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithAudioTransitionAsync(video,
        currentProject.getProjectPathIntermediateFileAudioFade(), mockedTranscoderHelperListener);
  }

  @Test
  public void launchExportTempFileUpdateIsTranscodingTempFileFinished() {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assert video.isTranscodingTempFileFinished();
    ApplyAVTransitionsUseCase injectedApplyAVTransitionsUseCase = getInjectedApplyAVTransitionsUseCase();

    injectedApplyAVTransitionsUseCase.applyAVTransitions(
            mockedDrawableFadeTransition, video, videonaFormat,
            mockedProject.getProjectPathIntermediateFileAudioFade(),
            mockedAVTransitionsApplierListener);

    assertThat(video.isTranscodingTempFileFinished(), is(false));
  }

  private ApplyAVTransitionsUseCase getInjectedApplyAVTransitionsUseCase() {
    return new ApplyAVTransitionsUseCase(currentProject, mockedVideoRepository);
  }

  public void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
