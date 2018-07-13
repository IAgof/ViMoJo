package com.videonasocialmedia.vimojo.split.domain;

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase.AUTOSPLIT_MS_RANGE;
import static com.videonasocialmedia.vimojo.utils.Constants.MIN_TRIM_OFFSET;
import static com.videonasocialmedia.vimojo.utils.Constants.MS_CORRECTION_FACTOR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class SplitVideoUseCaseTest {
  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock private OnSplitVideoListener mockedSpliListener;
  @Mock private VideoDataSource videoRepository;
  @InjectMocks SplitVideoUseCase injectedUseCase;
  private Project currentProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void splitVideoCallsAddVideoToProjectAtPosition() {
    Video video = new Video("media/path", 1f);

    injectedUseCase.splitVideo(currentProject, video, 0, 10, mockedSpliListener);

    ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
    verify(mockedAddVideoToProjectUseCase).addVideoToProjectAtPosition(eq(currentProject),
        videoCaptor.capture(), eq(1), any(OnAddMediaFinishedListener.class));
    assertThat(videoCaptor.getValue().getMediaPath(), is(video.getMediaPath()));
  }

  @Test
  public void handleTaskErrorModifiesVideoTrimmingTimes() throws IOException {
    PowerMockito.mockStatic(Log.class);
    String message = "Error message";
    Video video = spy(new Video("media/path", Video.DEFAULT_VOLUME));
    int splitTime = 1250;
    video.setStopTime(splitTime);
    doReturn(5000).when(video).getFileDuration();
    SplitVideoUseCase useCaseSpy = spy(injectedUseCase);
    Video endVideo = new Video(video);
    endVideo.setStartTime(splitTime);
    endVideo.setStopTime(5000);
    useCaseSpy.endVideo = endVideo;
    doNothing().when(useCaseSpy).runTrimTasks(any(Video.class), any(Video.class));

    useCaseSpy.handleTaskError(video, message, currentProject);

    assertThat(video.getStartTime(), is(not(50)));
    assertThat(video.getStopTime(), is(not(2540)));

    assertThat(Math.abs(video.getStopTime() - splitTime), lessThanOrEqualTo(AUTOSPLIT_MS_RANGE));
    assertThat(Math.abs(endVideo.getStartTime() - splitTime), lessThanOrEqualTo(AUTOSPLIT_MS_RANGE));
    assertThat(video.getStopTime() - video.getStartTime(),
            greaterThanOrEqualTo((int) (MIN_TRIM_OFFSET * MS_CORRECTION_FACTOR)));
    assertThat(endVideo.getStopTime() - endVideo.getStartTime(),
            greaterThanOrEqualTo((int) (MIN_TRIM_OFFSET * MS_CORRECTION_FACTOR)));
    assertThat(video.getStopTime(), is(endVideo.getStartTime()));
    // (jliarte): 23/11/01 uncomment the line above to see the result values
//    assertThat(video.getStopTime(), is(not(endVideo.getStartTime())));
  }

  private void getAProject() {
    currentProject = new Project(null, null, null, new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.GOOD, VideoFrameRate.FrameRate.FPS30));
  }
}