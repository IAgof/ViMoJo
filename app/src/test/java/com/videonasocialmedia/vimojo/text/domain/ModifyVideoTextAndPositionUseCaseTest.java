package com.videonasocialmedia.vimojo.text.domain;

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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 19/10/16.
 */
@RunWith(RobolectricTestRunner.class)
public class ModifyVideoTextAndPositionUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock VideoRepository mockedVideoRepository;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock Drawable mockDrawableFadeTransition;
  boolean isVideoFadeTransitionActivated;
  @InjectMocks ModifyVideoTextAndPositionUseCase injectedUseCase;
  private final MediaTranscoderListener mediaTranscoderListener = getMediaTranscoderListener();
  private final VideonaFormat videonaFormat = new VideonaFormat();

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testAddTextToVideoCallsTranscodeTrimAndOverlayImageToVideoIfVideoIsTrimmed()
          throws IOException {
    Video video = getVideoTrimmedWithText();
    // TODO(jliarte): 19/10/16 should not use a boolean here
    assert video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, video.getClipText(),
            video.getClipTextPosition(), mediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()), eq(video.getTempPath()), eq(videonaFormat),
            eq(mediaTranscoderListener), Matchers.any(Image.class), eq(0), eq(10));
  }

  @Test
  public void testAddTextToVideoCallsGenerateOutputVideoWithOverlayImageAndTrimmingIfVideoIsTrimmed()
          throws IOException {
    Video video = getVideoTrimmedWithText();
    assert video.isTrimmedVideo();
    TranscoderHelper spy = Mockito.spy(new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder));
    injectedUseCase.transcoderHelper = spy;

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, video.getClipText(),
            video.getClipTextPosition(), mediaTranscoderListener);

    verify(spy).generateOutputVideoWithOverlayImageAndTrimming(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated, video, videonaFormat, mediaTranscoderListener);
  }

  @Test
  public void testAddTextToVideoCallsTranscodeAndOverlayImageToVideoIfVideoIsNotTrimmed()
          throws IOException {
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, video.getClipText(),
            video.getClipTextPosition(), mediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeAndOverlayImageToVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()), eq(video.getTempPath()),
        eq(videonaFormat), eq(mediaTranscoderListener),
            Matchers.any(Image.class));
  }

  @Test
  public void testAddTextToVideoCallsGenerateOutputVideoWithOverlayImageIfVideoIsNotTrimmed()
          throws IOException {
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    TranscoderHelper spy = Mockito.spy(new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder));
    injectedUseCase.transcoderHelper = spy;

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat,
        video.getClipText(), video.getClipTextPosition(), mediaTranscoderListener);

    verify(spy).generateOutputVideoWithOverlayImage(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated, video, videonaFormat, mediaTranscoderListener);
  }

  @Test
  public void addTextToVideoCallsVideoRepositoryUpdate() {
    Video video = new Video("media/path", 1f);
    String textPosition = TextEffect.TextPosition.BOTTOM.name();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, "text",
        textPosition, mediaTranscoderListener);

    verify(mockedVideoRepository).update(video);
    assertThat(video.getClipText(), is("text"));
    assertThat(video.getClipTextPosition(), is(textPosition));
  }

  @NonNull
  private Video getVideoUntrimmedWithText() {
    Video video = new Video("media/path", 1f);
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    video.setTextToVideoAdded(true);
    return video;
  }

  @NonNull
  private Video getVideoTrimmedWithText() {
    Video video = getVideoUntrimmedWithText();
    video.setStartTime(0);
    video.setStopTime(10);
    video.setTrimmedVideo(true);
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