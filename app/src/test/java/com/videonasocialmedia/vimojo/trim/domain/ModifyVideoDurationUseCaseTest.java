package com.videonasocialmedia.vimojo.trim.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.transcoder.video.overlay.Image;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 18/10/16.
 */
@RunWith(RobolectricTestRunner.class)
public class ModifyVideoDurationUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock VideoRepository mockedVideoRepository;
  @Mock Drawable mockDrawableFadeTransition;
  boolean isVideoFadeTransitionActivated;
  @InjectMocks ModifyVideoDurationUseCase injectedUseCase;
  private final VideonaFormat videonaFormat = new VideonaFormat();
  private final MediaTranscoderListener mediaTranscoderListener = getMediaTranscoderListener();

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testTrimVideoCallsTranscodeTrimAndOverlayImageToVideoIfVideoHasText()
          throws Exception {
    Video video = getVideoWithText();
    assert video.hasText();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        mediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
        eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()),
        eq(video.getTempPath()), eq(videonaFormat), eq(mediaTranscoderListener),
            Matchers.any(Image.class), eq(0), eq(10));
  }

  @Test
  public void testTrimVideoCallsGenerateOutputVideoWithOverlayImageAndTrimmingIfVideoHasText()
          throws IOException {
    Video video = getVideoWithText();
    assert video.hasText();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        mediaTranscoderListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithOverlayImageAndTrimming(
        mockDrawableFadeTransition, isVideoFadeTransitionActivated, video,videonaFormat,
        mediaTranscoderListener);
  }

  @Test
  public void testTrimVideoCallsTranscodeAndTrimVideoIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path", 1f);
    assert ! video.hasText();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        mediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeAndTrimVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()),eq(video.getTempPath()),
        eq(videonaFormat), eq(mediaTranscoderListener), eq(0), eq(10));
  }

  @Test
  public void testTrimVideoCallsGenerateOutputVideoWithTrimmingIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path", 1f);
    // TODO(jliarte): 19/10/16 should check if video is trimmed?
    assert ! video.hasText();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        mediaTranscoderListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithTrimming(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated, video, videonaFormat, mediaTranscoderListener);
  }

  @Test
  public void trimVideoCallsVideoRepositoryUpdate() {
    Video video = new Video("media/path", 1f);
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 2, 10,
        mediaTranscoderListener);

    verify(mockedVideoRepository).update(video);
    assertThat(video.getStartTime(), is(2));
    assertThat(video.getStopTime(), is(10));
    assertThat(video.isTrimmedVideo(), is(true));
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path", 1f);
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    // TODO(jliarte): 18/10/16 fix these methods
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