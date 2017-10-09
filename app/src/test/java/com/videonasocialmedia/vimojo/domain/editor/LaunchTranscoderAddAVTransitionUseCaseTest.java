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
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

  @Mock VideoRepository mockVideoRepository;
  @Mock Video mockedVideo;
  @Mock Project mockedProject;
  @Mock private ApplyAVTransitionsUseCase.AVTransitionsApplierListener mockedAVTransitionsApplierListener;

  @InjectMocks ApplyAVTransitionsUseCase injectedApplyAVTransitionsUseCase;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void ifProjectHasVideoTransitionActivatedCallsGenerateOutputVideoWithAVTransitions()
          throws IllegalItemOnTrack {
    Project project = getAProject();
    project.getVMComposition().setVideoFadeTransitionActivated(true);
    Video video = new Video("media/path", 1f);
    Track track = project.getMediaTrack();
    track.insertItem(video);
    assertThat("Project has videos ", project.getVMComposition().hasVideos(), is(true));
    injectedApplyAVTransitionsUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedApplyAVTransitionsUseCase.applyAVTransitions(
            mockedDrawableFadeTransition, video, videonaFormat,
            project.getProjectPathIntermediateFileAudioFade(), mockedAVTransitionsApplierListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithAVTransitionsAsync(
            eq(mockedDrawableFadeTransition),
            eq(project.getVMComposition().isVideoFadeTransitionActivated()),
            eq(project.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            eq(videonaFormat), eq(project.getProjectPathIntermediateFileAudioFade()),
            any(TranscoderHelperListener.class));
    verify(mockedTranscoderHelper, never()).generateOutputVideoWithAudioTransitionAsync(video,
        project.getProjectPathIntermediateFileAudioFade(), mockedTranscoderHelperListener);

  }

  // Ignore test, verify not working, mock not used. Unify audio transcoder tasks in mediaTranscoder
  // and came back to this test.
  @Ignore
  @Test
  public void ifProjectHasAudioTransitionActivatedCallsGenerateOutputVideoWithAudioTransitions()
          throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    project.getVMComposition().setAudioFadeTransitionActivated(true);
    project.getVMComposition().setVideoFadeTransitionActivated(false);
    Video video = new Video("media/path", 1f);
    Track track = project.getMediaTrack();
    track.insertItem(video);
    assertThat("Project has videos ", project.getVMComposition().hasVideos(), is(true));
    assertThat("Video transition is not activated ",
            project.getVMComposition().isVideoFadeTransitionActivated(), is(false));
    assertThat("Audio transition is activated ",
            project.getVMComposition().isAudioFadeTransitionActivated(), is(true));
    injectedApplyAVTransitionsUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedApplyAVTransitionsUseCase.applyAVTransitions(
            mockedDrawableFadeTransition, video, videonaFormat,
            project.getProjectPathIntermediateFileAudioFade(), mockedAVTransitionsApplierListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithAudioTransitionAsync(video,
        project.getProjectPathIntermediateFileAudioFade(), mockedTranscoderHelperListener);

  }

  @Test
  public void launchExportTempFileCallsVideoRepositoryUpdate() {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);

    injectedApplyAVTransitionsUseCase.transcoderHelper = mockedTranscoderHelper;
    injectedApplyAVTransitionsUseCase.applyAVTransitions(
            mockedDrawableFadeTransition, video, videonaFormat,
            mockedProject.getProjectPathIntermediateFileAudioFade(),
            mockedAVTransitionsApplierListener);

    verify(mockVideoRepository).update(video);
  }

  @Test
  public void launchExportTempFileUpdateIsTranscodingTempFileFinished() {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assert video.isTranscodingTempFileFinished();

    injectedApplyAVTransitionsUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedApplyAVTransitionsUseCase.applyAVTransitions(
            mockedDrawableFadeTransition, video, videonaFormat,
            mockedProject.getProjectPathIntermediateFileAudioFade(),
            mockedAVTransitionsApplierListener);

    assertThat(video.isTranscodingTempFileFinished(), is(false));
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
