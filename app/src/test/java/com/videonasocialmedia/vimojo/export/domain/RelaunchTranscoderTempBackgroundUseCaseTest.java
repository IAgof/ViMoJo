package com.videonasocialmedia.vimojo.export.domain;

import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
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
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created by jliarte on 17/10/16.
 */
@RunWith(PowerMockRunner.class)
// TODO(jliarte): 17/10/16 a use case should be able to be tested without needing robolectric help!
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(manifest= Config.NONE) // Warning running test relaunchExportCallsVideoRepositoryUpdate STANDARD_OUT WARNING: No manifest file found at ./AndroidManifest.xml.Falling back to the Android OS resources only. To remove this warning, annotate your test class with @Config(manifest=Config.NONE).
@PrepareForTest({TextToDrawable.class})
public class RelaunchTranscoderTempBackgroundUseCaseTest {
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock Project mockedProject;

  private Project currentProject;
  @Mock MediaRepository mockedMediaRepository;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }


  @Ignore
  @Test
  public void testBugRelaunchExportThrowsNPE_WhenVideoHasntText() throws Exception {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assertThat(video.getClipText(), is(nullValue()));

    new RelaunchTranscoderTempBackgroundUseCase(mockedProject, mockedMediaRepository)
            .relaunchExport(video, currentProject);
  }


  @Test
  public void testRelaunchExportCallsUpdateIntermediateFileIfVideoHasText()
          throws Exception {
    Video video = getVideoWithText();
    assert video.hasText();
    RelaunchTranscoderTempBackgroundUseCase spyRelaunchTranscoderTempBackgroundUseCase =
        spy(getRelaunchTranscoderTempBackgroundUseCase());
    spyRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()),
            eq(video), any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    spyRelaunchTranscoderTempBackgroundUseCase.relaunchExport(video, currentProject);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));
  }

  @Test
  public void testRelaunchExportCallsUpdateIntermediateFileIfVideoHasntText()
          throws IOException {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assert ! video.hasText();
    RelaunchTranscoderTempBackgroundUseCase spyRelaunchTranscoderTempBackgroundUseCase =
        spy(getRelaunchTranscoderTempBackgroundUseCase());
    spyRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()),
            eq(video), any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    spyRelaunchTranscoderTempBackgroundUseCase.relaunchExport(video, currentProject);

    verify(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()), eq(video),
            any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));
  }

  @Test
  public void relaunchExportCallsVideoRepositoryUpdate() throws IOException {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    RelaunchTranscoderTempBackgroundUseCase spyRelaunchTranscoderTempBackgroundUseCase =
        spy(getRelaunchTranscoderTempBackgroundUseCase());
    spyRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()),
            eq(video), any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    spyRelaunchTranscoderTempBackgroundUseCase.relaunchExport(video, currentProject);

    verify(mockedMediaRepository).update(video);
  }

  @Test
  public void relaunchExportUpdateIsTranscodingTempFileFinished() throws IOException {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    assert video.isTranscodingTempFileFinished();
    RelaunchTranscoderTempBackgroundUseCase spyRelaunchTranscoderTempBackgroundUseCase =
        spy(getRelaunchTranscoderTempBackgroundUseCase());
    spyRelaunchTranscoderTempBackgroundUseCase.transcoderHelper = mockedTranscoderHelper;
    ListenableFuture<Video> mockedTask = Mockito.mock(ListenableFuture.class);
    doReturn(mockedTask).when(mockedTranscoderHelper).updateIntermediateFile(
            eq(currentProject.getVMComposition().getDrawableFadeTransitionVideo()),
            eq(currentProject.getVMComposition().isVideoFadeTransitionActivated()),
            eq(currentProject.getVMComposition().isAudioFadeTransitionActivated()),
            eq(video), any(VideonaFormat.class),
            eq(currentProject.getProjectPathIntermediateFileAudioFade()));

    spyRelaunchTranscoderTempBackgroundUseCase.relaunchExport(video, currentProject);

    assertThat(video.isTranscodingTempFileFinished(), is(false));
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    return video;
  }

  private void getAProject() {
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path",
            new Profile(VideoResolution.Resolution.HD720,
                    VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

  private RelaunchTranscoderTempBackgroundUseCase getRelaunchTranscoderTempBackgroundUseCase() {
    return new RelaunchTranscoderTempBackgroundUseCase(mockedProject, mockedMediaRepository);
  }
}
