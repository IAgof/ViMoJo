package com.videonasocialmedia.vimojo.trim.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.transcoder.video.overlay.Image;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;

import org.junit.Before;
import org.junit.Ignore;
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
  @Mock ListenableFuture mockedFuture;
  String intermediatesTempAudioFadeDirectory;
  boolean isVideoFadeTransitionActivated;
  boolean isAudioFadeTransitionActivated;
  @InjectMocks ModifyVideoDurationUseCase injectedUseCase;
  private final VideonaFormat videonaFormat = new VideonaFormat();
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;


  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Ignore
  @Test
  public void testTrimVideoCallsTranscodeTrimAndOverlayImageToVideoIfVideoHasText()
          throws Exception {
    Video video = getVideoWithText();
    assert video.hasText();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
        eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()),
        eq(video.getTempPath()), eq(videonaFormat), Matchers.any(Image.class), eq(0), eq(10));
  }

  @Test
  public void testTrimVideoCallsGenerateOutputVideoWithOverlayImageAndTrimmingIfVideoHasText()
          throws IOException {
    Video video = getVideoWithText();
    assert video.hasText();

    injectedUseCase.transcoderHelper= mockedTranscoderHelper;

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithOverlayImageAndTrimming(
        mockDrawableFadeTransition, isVideoFadeTransitionActivated, isAudioFadeTransitionActivated,
        video, videonaFormat, intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);
  }

  @Ignore
  @Test
  public void testTrimVideoCallsTranscodeAndTrimVideoIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path");
    assert ! video.hasText();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);

    verify(mockedMediaTranscoder).transcodeAndTrimVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()),eq(video.getTempPath()),
        eq(videonaFormat), eq(0), eq(10));
  }

  @Test
  public void testTrimVideoCallsGenerateOutputVideoWithTrimmingIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path");
    // TODO(jliarte): 19/10/16 should check if video is trimmed?
    assert ! video.hasText();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 0, 10,
        intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithTrimming(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, video, videonaFormat,
        intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);
  }

  @Test
  public void trimVideoCallsVideoRepositoryUpdate() {
    Video video = new Video("media/path");
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

      injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 2, 10,
          intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);

    verify(mockedVideoRepository).update(video);
  }

  @Test
  public void trimVideoCallsUpdateVideoParams() {
    Video video = new Video("media/path");
    assert video.isTranscodingTempFileFinished();

    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.trimVideo(mockDrawableFadeTransition, video, videonaFormat, 2, 10,
        intermediatesTempAudioFadeDirectory, mockedTranscoderHelperListener);

    assertThat(video.getStartTime(), is(2));
    assertThat(video.getStopTime(), is(10));
    assertThat(video.isTrimmedVideo(), is(true));
    assertThat(video.isTranscodingTempFileFinished(), is(false));
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path");
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    return video;
  }

}