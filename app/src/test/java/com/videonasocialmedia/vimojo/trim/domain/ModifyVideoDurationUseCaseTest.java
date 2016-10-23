package com.videonasocialmedia.vimojo.trim.domain;

import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Image;
import com.videonasocialmedia.vimojo.export.utils.TranscoderHelper;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;

import org.hamcrest.CoreMatchers;
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

    injectedUseCase.trimVideo(video, videonaFormat, 0, 10, mediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
            eq(video.getMediaPath()), eq(video.getTempPath()), eq(videonaFormat), eq(mediaTranscoderListener),
            Matchers.any(Image.class), eq(0), eq(10));
  }

  @Test
  public void testTrimVideoCallsGenerateOutputVideoWithOverlayImageAndTrimmingIfVideoHasText()
          throws IOException {
    Video video = getVideoWithText();
    assert video.hasText();

    injectedUseCase.trimVideo(video, videonaFormat, 0, 10, mediaTranscoderListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithOverlayImageAndTrimming(video,
            videonaFormat, mediaTranscoderListener);
  }

  @Test
  public void testTrimVideoCallsTranscodeAndTrimVideoIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path");
    assert ! video.hasText();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.trimVideo(video, videonaFormat, 0, 10, mediaTranscoderListener);

    verify(mockedMediaTranscoder).transcodeAndTrimVideo(eq(video.getMediaPath()),
            eq(video.getTempPath()), eq(videonaFormat), eq(mediaTranscoderListener), eq(0), eq(10));
  }

  @Test
  public void testTrimVideoCallsGenerateOutputVideoWithTrimmingIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path");
    // TODO(jliarte): 19/10/16 should check if video is trimmed?
    assert ! video.hasText();

    injectedUseCase.trimVideo(video, videonaFormat, 0, 10, mediaTranscoderListener);

    verify(mockedTranscoderHelper).generateOutputVideoWithTrimming(video, videonaFormat,
            mediaTranscoderListener);
  }

  @Test
  public void trimVideoCallsVideoRepositoryUpdate() {
    Video video = new Video("media/path");

    injectedUseCase.trimVideo(video, videonaFormat, 2, 10, mediaTranscoderListener);

    verify(mockedVideoRepository).update(video);
    assertThat(video.getStartTime(), is(2));
    assertThat(video.getStopTime(), is(10));
    assertThat(video.isTrimmedVideo(), is(true));
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path");
    video.setClipText("text");
    video.setClipTextPosition(VideoEditTextActivity.TextPosition.CENTER.name());
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