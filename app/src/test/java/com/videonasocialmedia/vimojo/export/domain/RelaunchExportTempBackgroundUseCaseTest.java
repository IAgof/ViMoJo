package com.videonasocialmedia.vimojo.export.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideoTranscoderFormat;
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
public class RelaunchExportTempBackgroundUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock Drawable mockDrawableFadeTransition;
  boolean isVideoFadeTransitionActivated;

  @InjectMocks RelaunchExportTempBackgroundUseCase injectedRelaunchExportTempBackgroundUseCase;
  private final MediaTranscoderListener mediaTranscoderListener = getMediaTranscoderListener();
  private final VideoTranscoderFormat videoTranscoderFormat = new VideoTranscoderFormat();

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testBugRelaunchExportThrowsNPE_WhenVideoHasntText() throws Exception {
    Video video = new Video("media/path");
    assertThat(video.getClipText(), is(nullValue()));

    new RelaunchExportTempBackgroundUseCase().relaunchExport(mockDrawableFadeTransition, video,
        mediaTranscoderListener, videoTranscoderFormat);
  }

  @Test
  public void testRelaunchExportCallsTranscodeTrimAndOverlayImageToVideoIfVideoHasText()
          throws IOException {
    Video video = getVideoWithText();
    assert video.hasText();
    video.setStopTime(10);
    // TODO(jliarte): 19/10/16 replace injected mocked transcoderhelper with a real one.
    injectedRelaunchExportTempBackgroundUseCase.transcoderHelper =
            new TranscoderHelper(mockedDrawableGenerator, mockedMediaTranscoder);

    injectedRelaunchExportTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition, video,
        mediaTranscoderListener, videoTranscoderFormat);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
        eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()),
            eq(video.getTempPath()), eq(videoTranscoderFormat), eq(mediaTranscoderListener),
            Matchers.any(Image.class), eq(video.getStartTime()), eq(video.getStopTime()));
  }

  @Test
  public void testRelaunchExportCallsgenerateOutputVideoWithOverlayImageAndTrimmingIfVideoHasText()
          throws Exception {
    Video video = getVideoWithText();
    assert video.hasText();

    injectedRelaunchExportTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition,
        video, mediaTranscoderListener, videoTranscoderFormat);

    verify(mockedTranscoderHelper).generateOutputVideoWithOverlayImageAndTrimming(
        mockDrawableFadeTransition, isVideoFadeTransitionActivated, video, videoTranscoderFormat,
        mediaTranscoderListener);
  }

  @Test
  public void testRelaunchExportCallsTranscodeAndTrimVideoIfVideoHasntText() throws IOException {
    Video video = new Video("media/path");
    assert ! video.hasText();
    injectedRelaunchExportTempBackgroundUseCase.transcoderHelper =
            new TranscoderHelper(mockedDrawableGenerator, mockedMediaTranscoder);

    injectedRelaunchExportTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition, video,
        mediaTranscoderListener, videoTranscoderFormat);

    verify(mockedMediaTranscoder).transcodeAndTrimVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()), eq(video.getTempPath()),
        eq(videoTranscoderFormat), eq(mediaTranscoderListener), eq(video.getStartTime()),
        eq(video.getStopTime()));
  }

  @Test
  public void testRelaunchExportCallsGenerateOutputVideoWithTrimmingIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path");
    assert ! video.hasText();

    injectedRelaunchExportTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition, video,
        mediaTranscoderListener, videoTranscoderFormat);

    verify(mockedTranscoderHelper).generateOutputVideoWithTrimming(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated, video, videoTranscoderFormat, mediaTranscoderListener);
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path");
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    video.setTextToVideoAdded(true);
    return video;
  }

  @NonNull
  private MediaTranscoderListener getMediaTranscoderListener() {
    return new MediaTranscoderListener() {
      @Override
      public void onTranscodeProgress(double v) {

      }

      @Override
      public void onTranscodeCompleted() {

      }

      @Override
      public void onTranscodeCanceled() {

      }

      @Override
      public void onTranscodeFailed(Exception e) {

      }
    };
  }
}