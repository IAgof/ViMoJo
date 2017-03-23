package com.videonasocialmedia.vimojo.domain.editor;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.export.domain.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 22/03/17.
 */

public class LaunchTranscoderAddAVTransitionUseCaseTest {

  @Mock
  TextToDrawable mockedDrawableGenerator;
  @Mock
  MediaTranscoder mockedMediaTranscoder;
  @Mock
  TranscoderHelper mockedTranscoderHelper;
  @Mock
  Drawable mockDrawableFadeTransition;
  @Mock
  TranscoderHelperListener mockedTranscoderHelperListener;

  private final VideonaFormat videonaFormat = new VideonaFormat();

  @InjectMocks
  LaunchTranscoderAddAVTransitionsUseCase
      injectedLaunchTranscoderAddAVTransitionsUseCase;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void ifProjectHasVideoTransitionActivatedCallsGenerateOutputVideoWithAVTransitions(){
    Project project = getAProject();
    project.setVideoFadeTransitionActivated(true);
    Video video = new Video("media/path");

    injectedLaunchTranscoderAddAVTransitionsUseCase.launchExportTempFile(mockDrawableFadeTransition,
        video, videonaFormat, project.getProjectPathIntermediateFileAudioFade(),
        mockedTranscoderHelperListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithAVTransitions(mockDrawableFadeTransition,
        project.isVideoFadeTransitionActivated(), project.isAudioFadeTransitionActivated(), video,
        videonaFormat, project.getProjectPathIntermediateFileAudioFade(),
        mockedTranscoderHelperListener);

  }

  @Test
  public void ifProjectHasAudioTransitionActivatedCallsGenerateOutputVideoWithAudioTransitions(){
    Project project = getAProject();
    project.setAudioFadeTransitionActivated(true);
    Video video = new Video("media/path");

    assertThat("Video transition is not activated ", project.isVideoFadeTransitionActivated(),
        is(false));
    assertThat("Audio transition is activated ", project.isAudioFadeTransitionActivated(),
        is(true));

    injectedLaunchTranscoderAddAVTransitionsUseCase.launchExportTempFile(mockDrawableFadeTransition,
        video, videonaFormat, project.getProjectPathIntermediateFileAudioFade(),
        mockedTranscoderHelperListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithAudioTransition(video,
        project.getProjectPathIntermediateFileAudioFade(), mockedTranscoderHelperListener);

  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
