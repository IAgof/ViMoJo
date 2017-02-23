package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderOldListener;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.transcoder.video.overlay.Image;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;

import org.junit.Before;
import org.junit.Ignore;
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
  String intermediatesTempAudioFadeDirectory;
  boolean isVideoFadeTransitionActivated;
  boolean isAudioFadeTransitionActivated;
  @InjectMocks ModifyVideoTextAndPositionUseCase injectedUseCase;
  private final VideonaFormat videonaFormat = new VideonaFormat();
  private MediaTranscoderListener mockedMediaTranscoderListener;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Ignore
  @Test
  public void testAddTextToVideoCallsTranscodeTrimAndOverlayImageToVideoIfVideoIsTrimmed()
          throws IOException {
    Video video = getVideoTrimmedWithText();
    // TODO(jliarte): 19/10/16 should not use a boolean here
    assert video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, video.getClipText(),
            video.getClipTextPosition(), intermediatesTempAudioFadeDirectory, mockedMediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()), eq(video.getTempPath()),
        eq(videonaFormat), Matchers.any(Image.class), eq(0), eq(10));
  }

  @Test
  public void testAddTextToVideoCallsGenerateOutputVideoWithOverlayImageAndTrimmingIfVideoIsTrimmed()
          throws IOException {
    Video video = getVideoTrimmedWithText();
    assert video.isTrimmedVideo();

    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, video.getClipText(),
            video.getClipTextPosition(), intermediatesTempAudioFadeDirectory, mockedMediaTranscoderListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithOverlayImageAndTrimming(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated,isAudioFadeTransitionActivated, video, videonaFormat,
        intermediatesTempAudioFadeDirectory,mockedMediaTranscoderListener);
  }

  @Ignore
  @Test
  public void testAddTextToVideoCallsTranscodeAndOverlayImageToVideoIfVideoIsNotTrimmed()
          throws IOException {
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, video.getClipText(),
            video.getClipTextPosition(), intermediatesTempAudioFadeDirectory, mockedMediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeAndOverlayImageToVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()), eq(video.getTempPath()),
        eq(videonaFormat),Matchers.any(Image.class));
  }

  @Test
  public void testAddTextToVideoCallsGenerateOutputVideoWithOverlayImageIfVideoIsNotTrimmed()
          throws IOException {
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();

    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat,
        video.getClipText(), video.getClipTextPosition(), intermediatesTempAudioFadeDirectory,
        mockedMediaTranscoderListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithOverlayImage(mockDrawableFadeTransition,
        isVideoFadeTransitionActivated, isAudioFadeTransitionActivated,
        video, videonaFormat, intermediatesTempAudioFadeDirectory, mockedMediaTranscoderListener);
  }

  @Test
  public void addTextToVideoCallsVideoRepositoryUpdate() {
    Video video = new Video("media/path");
    String textPosition = TextEffect.TextPosition.BOTTOM.name();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

      injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, "text",
          textPosition, intermediatesTempAudioFadeDirectory, mockedMediaTranscoderListener);

    verify(mockedVideoRepository).update(video);
    assertThat(video.getClipText(), is("text"));
    assertThat(video.getClipTextPosition(), is(textPosition));
  }

  @NonNull
  private Video getVideoUntrimmedWithText() {
    Video video = new Video("media/path");
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
  private MediaTranscoderOldListener getMediaTranscoderListener() {
    return new MediaTranscoderOldListener() {
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