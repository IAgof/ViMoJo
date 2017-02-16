package com.videonasocialmedia.vimojo.export.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.transcoder.video.overlay.Image;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 17/10/16.
 */
@RunWith(PowerMockRunner.class)
// TODO(jliarte): 17/10/16 a use case should be able to be tested without needing robolectric help!
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PrepareForTest({TextToDrawable.class})
public class RelaunchTranscoderTempBackgroundUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock Drawable mockDrawableFadeTransition;
  @Mock String mockedIntermediatesTempAudioFadeDirectory;
  boolean isVideoFadeTransitionActivated;
  boolean isAudioFadeTransitionActivated;

  @InjectMocks
  RelaunchTranscoderTempBackgroundUseCase injectedRelaunchTranscoderTempBackgroundUseCase;
  private final MediaTranscoderListener mediaTranscoderListener = new MediaTranscoderListener() {
    @Override
    public void onSuccessTranscoding(Video video) {

    }

    @Override
    public void onErrorTranscoding(Video video, String message) {

    }
  };
  private final VideonaFormat videonaFormat = new VideonaFormat();

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testBugRelaunchExportThrowsNPE_WhenVideoHasntText() throws Exception {
    Video video = new Video("media/path");
    assertThat(video.getClipText(), is(nullValue()));

    new RelaunchTranscoderTempBackgroundUseCase().relaunchExport(mockDrawableFadeTransition, video,
        videonaFormat, currentProject.getProjectPathIntermediateFileAudioFade());
  }

  @Test
  public void testRelaunchExportCallsTranscodeTrimAndOverlayImageToVideoIfVideoHasText()
          throws IOException {
    Video video = getVideoWithText();
    assert video.hasText();
    video.setStopTime(10);
    // TODO(jliarte): 19/10/16 replace injected mocked transcoderhelper with a real one.
    injectedRelaunchTranscoderTempBackgroundUseCase.transcoderHelper =
            new TranscoderHelper(mockedDrawableGenerator, mockedMediaTranscoder);

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition, video,
        videonaFormat, currentProject.getProjectPathIntermediateFileAudioFade());

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
        eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()),
            eq(video.getTempPath()), eq(videonaFormat),
            Matchers.any(Image.class), eq(video.getStartTime()), eq(video.getStopTime()));
  }

  @Test
  public void testRelaunchExportCallsgenerateOutputVideoWithOverlayImageAndTrimmingIfVideoHasText()
          throws Exception {
    Video video = getVideoWithText();
    assert video.hasText();

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition,
        video, videonaFormat, currentProject.getProjectPathIntermediateFileAudioFade());

    verify(mockedTranscoderHelper).generateOutputVideoWithOverlayImageAndTrimming(
        mockDrawableFadeTransition, isVideoFadeTransitionActivated, isAudioFadeTransitionActivated,
        video, videonaFormat,mockedIntermediatesTempAudioFadeDirectory, mediaTranscoderListener);
  }

  @Test
  public void testRelaunchExportCallsTranscodeAndTrimVideoIfVideoHasntText() throws IOException {
    Video video = new Video("media/path");
    assert ! video.hasText();
    injectedRelaunchTranscoderTempBackgroundUseCase.transcoderHelper =
            new TranscoderHelper(mockedDrawableGenerator, mockedMediaTranscoder);

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition, video,
        videonaFormat, currentProject.getProjectPathIntermediateFileAudioFade());

    verify(mockedMediaTranscoder).transcodeAndTrimVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()), eq(video.getTempPath()),
        eq(videonaFormat), eq(video.getStartTime()), eq(video.getStopTime()));
  }

  @Test
  public void testRelaunchExportCallsGenerateOutputVideoWithTrimmingIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path");
    assert ! video.hasText();

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition, video,
        videonaFormat, currentProject.getProjectPathIntermediateFileAudioFade());

    verify(mockedTranscoderHelper).generateOutputVideoWithTrimming(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, video, videonaFormat,
        mockedIntermediatesTempAudioFadeDirectory, mediaTranscoderListener);
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path");
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    video.setTextToVideoAdded(true);
    return video;
  }

}