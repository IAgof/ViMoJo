package com.videonasocialmedia.vimojo.text.domain;

import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.transcoder.video.overlay.Image;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 19/10/16.
 */
@RunWith(RobolectricTestRunner.class)
public class ModifyVideoTextAndPositionUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock VideoRepository mockedVideoRepository;
  @Mock VideoToAdaptRepository mockedVideoToAdaptRepository;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @InjectMocks ModifyVideoTextAndPositionUseCase injectedUseCase;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Ignore
  @Test
  public void testAddTextToVideoCallsTranscodeTrimAndOverlayImageToVideoIfVideoIsTrimmed()
          throws IOException {
    Project currentProject = getAProject();
    Video video = getVideoTrimmedWithText();
    // TODO(jliarte): 19/10/16 should not use a boolean here
    assert video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.addTextToVideo(video, video.getClipText(), video.getClipTextPosition(),
            currentProject);

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(video.getMediaPath()), eq(video.getTempPath()), any(VideonaFormat.class),
            any(Image.class), eq(0), eq(10));
  }

  @Test
  public void testAddTextToVideoCallsUpdateIntermediateFileIfVideoIsTrimmed()
          throws IOException {
    Project currentProject = getAProject();
    Video video = getVideoTrimmedWithText();
    assert video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    injectedUseCase.addTextToVideo(video, video.getClipText(), video.getClipTextPosition(),
            currentProject);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));
  }

  @Ignore
  @Test
  public void testAddTextToVideoCallsTranscodeAndOverlayImageToVideoIfVideoIsNotTrimmed()
          throws IOException {
    Project currentProject = getAProject();
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    injectedUseCase.addTextToVideo(video,
            video.getClipText(),video.getClipTextPosition(), currentProject);

    verify(mockedMediaTranscoder).transcodeAndOverlayImageToVideo(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(video.getMediaPath()), eq(video.getTempPath()),
            any(VideonaFormat.class), any(Image.class));
  }

  @Test
  public void testAddTextToVideoCallsUpdateIntermediateFileIfVideoIsNotTrimmed()
          throws IOException {
    Project currentProject = getAProject();
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    injectedUseCase.addTextToVideo(video, video.getClipText(), video.getClipTextPosition(),
            currentProject);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));
  }

  @Test
  public void addTextToVideoCallsVideoRepositoryUpdate() throws IOException {
    Project currentProject = getAProject();
    Video video = new Video("media/path", 1f);
    String textPosition = TextEffect.TextPosition.BOTTOM.name();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    injectedUseCase.addTextToVideo(video, "text", textPosition, currentProject);

    verify(mockedVideoRepository, atLeastOnce()).update(video);
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

  private Project getAProject() {
    clearProject();
    return Project.getInstance(null, null, null, new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.GOOD, VideoFrameRate.FrameRate.FPS30));
  }

  private void clearProject() {
    if (Project.INSTANCE != null) {
      Project.INSTANCE.clear();
    }
  }

}