package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 31/05/17.
 */

public class VideoListErrorCheckerDelegateTest {

  @Mock
  VideoListErrorCheckerDelegate mockedVideoListErrorCheckerDelegate;
  @Mock
  ListenableFuture<Video> mockedTranscodingTask;
  @Mock
  VideoTranscodingErrorNotifier mockedVideoTranscodingErrorNotifier;
  private Project currentProject;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void ifProjectHasSomeVideoWithErrorsCallsShowWarningTempFile() throws IllegalItemOnTrack {
    Video video = new Video("somePath", Video.DEFAULT_VOLUME);
    currentProject.getMediaTrack().insertItem(video);
    assertThat("Project has video", currentProject.getVMComposition().hasVideos(), is(true));
    Video video1 = new Video("video/path", Video.DEFAULT_VOLUME);
    Video video2 = new Video("video/path", Video.DEFAULT_VOLUME);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video1);
    videoList.add(video2);
    video2.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
    assertThat("video1 has not error", video1.getVideoError(), is(nullValue()));
    assertThat("video2 has error", video2.getVideoError(), notNullValue());
    assertThat("video2 has error and not empty", video2.getVideoError().isEmpty(), is(false));
    video1.setTranscodingTask(mockedTranscodingTask);
    video2.setTranscodingTask(mockedTranscodingTask);
    when(mockedTranscodingTask.isCancelled()).thenReturn(true);
    ArrayList<Video> failedVideos = new ArrayList<>();

    VideoListErrorCheckerDelegate videoListErrorCheckerDelegate =
            new VideoListErrorCheckerDelegate();
    videoListErrorCheckerDelegate.checkWarningMessageVideosRetrieved(videoList,
        mockedVideoTranscodingErrorNotifier);

    ArgumentCaptor<ArrayList> failedVideosCaptor = ArgumentCaptor.forClass(ArrayList.class);
    verify(mockedVideoTranscodingErrorNotifier).showWarningTempFile(failedVideosCaptor.capture());
    verify(mockedVideoTranscodingErrorNotifier).setWarningMessageTempFile(anyString());
    assertThat(failedVideosCaptor.getValue().size(), is(2));
    Video failedVideo1 = (Video) failedVideosCaptor.getValue().get(0);
    Video failedVideo2 = (Video) failedVideosCaptor.getValue().get(1);
    assertThat(failedVideo1, is(video1));
    assertThat(failedVideo2, is(video2));
  }

  public void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.H_720P, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
  }
}

