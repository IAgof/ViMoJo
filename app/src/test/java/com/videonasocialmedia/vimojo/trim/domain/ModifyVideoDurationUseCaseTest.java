package com.videonasocialmedia.vimojo.trim.domain;

import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 18/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ModifyVideoDurationUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock VideoRepository mockedVideoRepository;
  @Mock ListenableFuture mockedFuture;
  @InjectMocks ModifyVideoDurationUseCase injectedUseCase;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock private VideoToAdaptRepository mockedVideoToAdaptRepository;


  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  // TODO(jliarte): 22/08/17 cant make this pass when invoked all class tests
  @Ignore
  @Test
  public void testTrimVideoCallsUpdateIntermediateFileIfVideoHasText()
          throws IOException {
    Project currentProject = getAProject();
    Video video = getVideoWithText();
    assert video.hasText();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    injectedUseCase.videoToAdaptRepository = mockedVideoToAdaptRepository;

    injectedUseCase.trimVideo(video, 0, 10, currentProject);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            eq(currentProject.getVMComposition().getVideoFormat()),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));
  }

  // TODO(jliarte): 22/08/17 cant make this pass when invoked all class tests
  @Ignore
  @Test
  public void testTrimVideoCallsGenerateOutputVideoWithTrimmingIfVideoHasntText()
          throws IOException {
    Project currentProject = getAProject();
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    // TODO(jliarte): 19/10/16 should check if video is trimmed?
    assert ! video.hasText();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    injectedUseCase.videoToAdaptRepository = mockedVideoToAdaptRepository;

    injectedUseCase.trimVideo(video, 0, 10, currentProject);

    verify(mockedTranscoderHelper).generateOutputVideoWithTrimmingAsync(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            eq(currentProject.getVMComposition().getVideoFormat()),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));
  }

  @Test
  public void trimVideoCallsVideoRepositoryUpdate() {
    Project currentProject = getAProject();
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    // TODO(jliarte): 19/09/17 dunno why automatically injected video repo is not mockedVideoRepository
    injectedUseCase.videoRepository = mockedVideoRepository;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    injectedUseCase.trimVideo(video, 2, 10, currentProject);

    verify(mockedVideoRepository).update(video);
  }

  @Test
  public void trimVideoUpdatesVideoParams() {
    Project currentProject = getAProject();
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assert video.isTranscodingTempFileFinished();
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class), eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    injectedUseCase.trimVideo(video, 2, 10, currentProject);

    assertThat(video.getStartTime(), is(2));
    assertThat(video.getStopTime(), is(10));
    assertThat(video.isTrimmedVideo(), is(true));
    // (jliarte): 22/08/17 now this is set to false in a new thread, after waiting for adapt job to
    // finish
//    assertThat(video.isTranscodingTempFileFinished(), is(false));
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
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