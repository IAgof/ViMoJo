package com.videonasocialmedia.vimojo.trim.domain;

import android.support.annotation.NonNull;
import android.util.Log;

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
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase.AUTOTRIM_MS_RANGE;
import static com.videonasocialmedia.vimojo.utils.Constants.MIN_TRIM_OFFSET;
import static com.videonasocialmedia.vimojo.utils.Constants.MS_CORRECTION_FACTOR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 18/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class ModifyVideoDurationUseCaseTest {
  @Mock TextToDrawable mockedDrawableGenerator;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock
  VideoDataSource mockedVideoRepository;
  @Mock ListenableFuture mockedFuture;
  @InjectMocks ModifyVideoDurationUseCase injectedUseCase;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock private VideoToAdaptDataSource mockedVideoToAdaptRepository;
  private Project currentProject;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  // TODO(jliarte): 22/08/17 cant make this pass when invoked all class tests
  @Ignore
  @Test
  public void testTrimVideoCallsUpdateIntermediateFileIfVideoHasText()
          throws IOException {
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
  public void trimVideoCallsVideoRepositoryUpdate() throws IOException {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    injectedUseCase.transcoderHelper = mockedTranscoderHelper;
    // TODO(jliarte): 19/09/17 dunno why automatically injected video repo is not mockedVideoRepository
//    injectedUseCase.videoDataSource = mockedVideoRepository;
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
  public void trimVideoUpdatesVideoParams() throws IOException {
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

  @Test
  public void handleTaskErrorModifiesVideoTrimmingTimes() throws IOException {
    PowerMockito.mockStatic(Log.class);
    String message = "Error message";
    Video video = spy(new Video("media/path", Video.DEFAULT_VOLUME));
    video.setStartTime(50);
    video.setStopTime(2540);
    doReturn(5000).when(video).getFileDuration();
    ModifyVideoDurationUseCase useCaseSpy = spy(injectedUseCase);
    ListenableFuture<Video> mockedListenable = mock(ListenableFuture.class);
    doReturn(mockedListenable).when(useCaseSpy).runTrimTranscodingTask(video, currentProject);

    useCaseSpy.handleTaskError(video, message, currentProject);

    assertThat(video.getStartTime(), is(not(50)));
    assertThat(video.getStopTime(), is(not(2540)));

    assertThat(Math.abs(video.getStartTime() - 50), lessThanOrEqualTo(AUTOTRIM_MS_RANGE));
    assertThat(Math.abs(video.getStopTime() - 2540), lessThanOrEqualTo(AUTOTRIM_MS_RANGE));
    assertThat(video.getStopTime() - video.getStartTime(),
            greaterThanOrEqualTo((int) (MIN_TRIM_OFFSET * MS_CORRECTION_FACTOR)));
    // (jliarte): 23/10/17 uncomment the line above to see the result values
//    assertThat(video.getStartTime(), is(video.getStopTime()));
  }

  @NonNull
  private Video getVideoWithText() {
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    video.setClipText("text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    return video;
  }

  private void getAProject() {
    currentProject = new Project(null, null, null, new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.GOOD, VideoFrameRate.FrameRate.FPS30));
  }

}