package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
public class RemoveVideoFromProjectUseCaseTest {
  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock
  VideoDataSource mockedVideoRepository;
  @InjectMocks RemoveVideoFromProjectUseCase injectedUseCase;
  @Mock MediaTrack mockedMediaTrack;
  private Project currentProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
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

  @Test
  public void testRemoveMediaItemFromProjectCallsUpdateProject() throws IllegalItemOnTrack {
    Video video = new Video("media/path", 1f);
    int positionVideoToRemove = 0;
    currentProject.getMediaTrack().insertItem(video);
    OnRemoveMediaFinishedListener listener = getOnRemoveMediaFinishedListener();

    injectedUseCase.removeMediaItemFromProject(currentProject, positionVideoToRemove, listener);

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