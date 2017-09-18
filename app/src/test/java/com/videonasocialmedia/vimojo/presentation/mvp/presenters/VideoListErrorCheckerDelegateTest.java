package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
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

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  @Test
  public void ifProjectHasSomeVideoWithErrorsCallsShowWarningTempFile() throws IllegalItemOnTrack {

    Project project = getAProject();
    Video video = new Video("somePath", Video.DEFAULT_VOLUME);
    project.getMediaTrack().insertItem(video);
    assertThat("Project has video", project.getVMComposition().hasVideos(), is(true));

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

    VideoListErrorCheckerDelegate videoListErrorCheckerDelegate =
            new VideoListErrorCheckerDelegate();
    videoListErrorCheckerDelegate.checkWarningMessageVideosRetrieved(videoList,
        mockedVideoTranscodingErrorNotifier);

    verify(mockedVideoTranscodingErrorNotifier).showWarningTempFile();
    verify(mockedVideoTranscodingErrorNotifier).setWarningMessageTempFile(anyString());
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", "private/path", profile);
  }
}

