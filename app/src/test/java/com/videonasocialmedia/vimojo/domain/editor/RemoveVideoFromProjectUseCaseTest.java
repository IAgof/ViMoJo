package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EventBus.class)
public class RemoveVideoFromProjectUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @Mock VideoRepository mockedVideoRepository;
  @InjectMocks RemoveVideoFromProjectUseCase injectedUseCase;
  @Mock MediaTrack mockedMediaTrack;
  private EventBus mockedEventBus;
  private Project currentProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Before
  public void setupTestEventBus() {
    PowerMockito.mockStatic(EventBus.class);
    EventBus mockedEventBus = PowerMockito.mock(EventBus.class);
    PowerMockito.when(EventBus.getDefault()).thenReturn(mockedEventBus);
    this.mockedEventBus = mockedEventBus;
  }

  @Test
  public void testRemoveMediaItemsFromProjectCallsUpdateProject() {
    currentProject.setMediaTrack(mockedMediaTrack);
    Video video = new Video("media/path", 1f);
    ArrayList<Media> videos = new ArrayList<Media>();
    videos.add(video);
    OnRemoveMediaFinishedListener listener = getOnRemoveMediaFinishedListener();

    injectedUseCase.removeMediaItemsFromProject(currentProject, videos, listener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @NonNull
  private OnRemoveMediaFinishedListener getOnRemoveMediaFinishedListener() {
    return new OnRemoveMediaFinishedListener() {
      @Override
      public void onRemoveMediaItemFromTrackError() {

      }

      @Override
      public void onRemoveMediaItemFromTrackSuccess() {

      }
    };
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}