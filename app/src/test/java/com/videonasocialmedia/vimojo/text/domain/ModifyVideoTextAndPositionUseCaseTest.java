package com.videonasocialmedia.vimojo.text.domain;

import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.transcoder.video.overlay.Image;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created by jliarte on 19/10/16.
 */
@RunWith(RobolectricTestRunner.class)
public class ModifyVideoTextAndPositionUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock VideoToAdaptDataSource mockedVideoToAdaptRepository;
  @Mock TranscoderHelper mockedTranscoderHelper;
  private Project currentProject;
  @Mock RelaunchTranscoderTempBackgroundUseCase mockedRelaunchTranscoderTempBackgroundUseCase;
  @Mock MediaRepository mockedMediaRepository;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Ignore
  @Test
  public void testAddTextToVideoCallsTranscodeTrimAndOverlayImageToVideoIfVideoIsTrimmed()
          throws IOException {
    Video video = getVideoTrimmedWithText();
    // TODO(jliarte): 19/10/16 should not use a boolean here
    assert video.isTrimmedVideo();
    ModifyVideoTextAndPositionUseCase spyModifyVideoTextAndPositionUseCase =
        spy(getModifyTextAndPositionUseCase());
    spyModifyVideoTextAndPositionUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    spyModifyVideoTextAndPositionUseCase.addTextToVideo(currentProject, video, video.getClipText(), video.getClipTextPosition());

    verify(mockedMediaTranscoder).transcodeTrimAndOverlayImageToVideo(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(video.getMediaPath()), eq(video.getTempPath()), any(VideonaFormat.class),
            any(Image.class), eq(0), eq(10));
  }

  @Test
  public void testAddTextToVideoCallsUpdateIntermediateFileIfVideoIsTrimmed()
          throws IOException {
    Video video = getVideoTrimmedWithText();
    assert video.isTrimmedVideo();
    ModifyVideoTextAndPositionUseCase spyModifyVideoTextAndPositionUseCase =
        spy(getModifyTextAndPositionUseCase());
    spyModifyVideoTextAndPositionUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    spyModifyVideoTextAndPositionUseCase.addTextToVideo(currentProject, video, video.getClipText(),
        video.getClipTextPosition());

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
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    ModifyVideoTextAndPositionUseCase spyModifyVideoTextAndPositionUseCase =
        spy(getModifyTextAndPositionUseCase());
    spyModifyVideoTextAndPositionUseCase.transcoderHelper = new TranscoderHelper(mockedDrawableGenerator,
            mockedMediaTranscoder);

    spyModifyVideoTextAndPositionUseCase.addTextToVideo(currentProject, video, video.getClipText(),
        video.getClipTextPosition());

    verify(mockedMediaTranscoder).transcodeAndOverlayImageToVideo(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(video.getMediaPath()), eq(video.getTempPath()),
            any(VideonaFormat.class), any(Image.class));
  }

  @Test
  public void testAddTextToVideoCallsUpdateIntermediateFileIfVideoIsNotTrimmed()
          throws IOException {
    Video video = getVideoUntrimmedWithText();
    assert video.hasText();
    assert ! video.isTrimmedVideo();
    ModifyVideoTextAndPositionUseCase spyModifyVideoTextAndPositionUseCase =
        spy(getModifyTextAndPositionUseCase());
    spyModifyVideoTextAndPositionUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    spyModifyVideoTextAndPositionUseCase.addTextToVideo(currentProject, video, video.getClipText(),
        video.getClipTextPosition());

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));
  }

  @Test
  public void addTextToVideoCallsVideoRepositoryUpdate() throws IOException {
    Video video = new Video("media/path", 1f);
    String textPosition = TextEffect.TextPosition.BOTTOM.name();
    ModifyVideoTextAndPositionUseCase spyModifyVideoTextAndPositionUseCase =
        spy(getModifyTextAndPositionUseCase());
    spyModifyVideoTextAndPositionUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    spyModifyVideoTextAndPositionUseCase.addTextToVideo(currentProject, video, "text", textPosition);

    verify(mockedMediaRepository, atLeastOnce()).update(video);
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

  private void getAProject() {
    currentProject = new Project(null, null, null, new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.GOOD, VideoFrameRate.FrameRate.FPS30));
  }

  private ModifyVideoTextAndPositionUseCase getModifyTextAndPositionUseCase() {
    return new ModifyVideoTextAndPositionUseCase(mockedRelaunchTranscoderTempBackgroundUseCase,
        mockedVideoToAdaptRepository, mockedMediaRepository);
  }
}
