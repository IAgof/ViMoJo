package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
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

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat,
            video.getClipText(), video.getClipTextPosition(), intermediatesTempAudioFadeDirectory);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
            eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated),
            eq(video.getMediaPath()), eq(video.getTempPath()), eq(videonaFormat), any(Image.class),
            eq(0), eq(10));
  }

  @Test
  public void testAddTextToVideoCallsUpdateIntermediateFileIfVideoIsTrimmed()
          throws IOException {
    Video video = getVideoTrimmedWithText();
    assert video.isTrimmedVideo();

    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat,
            video.getClipText(), video.getClipTextPosition(), intermediatesTempAudioFadeDirectory);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated),
            eq(isAudioFadeTransitionActivated), eq(video), eq(videonaFormat),
            eq(intermediatesTempAudioFadeDirectory));
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

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat,
            video.getClipText(),video.getClipTextPosition(), intermediatesTempAudioFadeDirectory);

    verify(mockedMediaTranscoder).transcodeAndOverlayImageToVideo(eq(mockDrawableFadeTransition),
        eq(isVideoFadeTransitionActivated), eq(video.getMediaPath()), eq(video.getTempPath()),
        eq(videonaFormat), any(Image.class));
  }

  @Test
  public void testAddTextToVideoCallsUpdateIntermediateFileIfVideoIsNotTrimmed()
          throws IOException {
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat,
        video.getClipText(), video.getClipTextPosition(), intermediatesTempAudioFadeDirectory
    );

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated),
            eq(isAudioFadeTransitionActivated), eq(video), eq(videonaFormat),
            eq(intermediatesTempAudioFadeDirectory));
  }

  @Test
  public void addTextToVideoCallsVideoRepositoryUpdate() {
    Video video = new Video("media/path", 1f);
    String textPosition = TextEffect.TextPosition.BOTTOM.name();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;

      injectedUseCase.addTextToVideo(mockDrawableFadeTransition, video, videonaFormat, "text",
          textPosition, intermediatesTempAudioFadeDirectory);

    verify(mockedVideoRepository).update(video);
    assertThat(video.getClipText(), is("text"));
    assertThat(video.getClipTextPosition(), is(textPosition));
  }

  @NonNull
  private Video getVideoUntrimmedWithText() {
    Video video = new Video("media/path", 1f);
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
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

}