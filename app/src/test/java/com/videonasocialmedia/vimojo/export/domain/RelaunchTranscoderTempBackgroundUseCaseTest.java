package com.videonasocialmedia.vimojo.export.domain;

import android.graphics.drawable.Drawable;
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
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.doReturn;
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
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock VideoRepository mockedVideoRepository;
  String intermediatesTempAudioFadeDirectory;
  boolean isVideoFadeTransitionActivated;
  boolean isAudioFadeTransitionActivated;

  @InjectMocks
  RelaunchTranscoderTempBackgroundUseCase injectedRelaunchTranscoderTempBackgroundUseCase;

  private final VideonaFormat videonaFormat = new VideonaFormat();

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Ignore
  @Test
  public void testBugRelaunchExportThrowsNPE_WhenVideoHasntText() throws Exception {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assertThat(video.getClipText(), is(nullValue()));

    new RelaunchTranscoderTempBackgroundUseCase(mockedVideoRepository)
            .relaunchExport(mockDrawableFadeTransition, video, videonaFormat,
                    intermediatesTempAudioFadeDirectory);
  }

  @Test
  public void testRelaunchExportCallsUpdateIntermediateFileIfVideoHasText()
          throws Exception {
    Project currentProject = getAProject();
    Video video = getVideoWithText();
    assert video.hasText();
    injectedRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            mockDrawableFadeTransition,
            currentProject.getVMComposition().isVideoFadeTransitionActivated(),
            currentProject.getVMComposition().isAudioFadeTransitionActivated(), video,
            videonaFormat, intermediatesTempAudioFadeDirectory);

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition,
        video, videonaFormat, intermediatesTempAudioFadeDirectory);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated),
            eq(isAudioFadeTransitionActivated), eq(video), eq(videonaFormat),
            eq(intermediatesTempAudioFadeDirectory));
  }

  @Test
  public void testRelaunchExportCallsUpdateIntermediateFileIfVideoHasntText()
          throws IOException {
    Project currentProject = getAProject();
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assert ! video.hasText();
    injectedRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            mockDrawableFadeTransition,
            currentProject.getVMComposition().isVideoFadeTransitionActivated(),
            currentProject.getVMComposition().isAudioFadeTransitionActivated(), video,
            videonaFormat, intermediatesTempAudioFadeDirectory);

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition,
            video, videonaFormat, intermediatesTempAudioFadeDirectory);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(mockDrawableFadeTransition), eq(isVideoFadeTransitionActivated),
            eq(isAudioFadeTransitionActivated), eq(video), eq(videonaFormat),
            eq(intermediatesTempAudioFadeDirectory));
  }

  @Test
  public void relaunchExportCallsVideoRepositoryUpdate() {
    Project currentProject = getAProject();
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    injectedRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            mockDrawableFadeTransition,
            currentProject.getVMComposition().isVideoFadeTransitionActivated(),
            currentProject.getVMComposition().isAudioFadeTransitionActivated(), video,
            videonaFormat, intermediatesTempAudioFadeDirectory);

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition,
            video, videonaFormat, intermediatesTempAudioFadeDirectory);

    verify(mockedVideoRepository).update(video);
  }

  @Test
  public void relaunchExportUpdateIsTranscodingTempFileFinished() {
    Project currentProject = getAProject();
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assert video.isTranscodingTempFileFinished();
    injectedRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            mockDrawableFadeTransition,
            currentProject.getVMComposition().isVideoFadeTransitionActivated(),
            currentProject.getVMComposition().isAudioFadeTransitionActivated(), video,
            videonaFormat, intermediatesTempAudioFadeDirectory);

    injectedRelaunchTranscoderTempBackgroundUseCase.relaunchExport(mockDrawableFadeTransition,
            video, videonaFormat, intermediatesTempAudioFadeDirectory);

    assertThat(video.isTranscodingTempFileFinished(), is(false));
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    return video;
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}