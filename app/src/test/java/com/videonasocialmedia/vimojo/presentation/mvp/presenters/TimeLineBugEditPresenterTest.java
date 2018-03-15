package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 27/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeLineBugEditPresenterTest {
  @Mock ReorderMediaItemUseCase reorderMediaItemUseCase;
  @InjectMocks EditPresenter editPresenter;
  private Project currentProject;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path",
        compositionProfile);
  }

  @After
  public void clearProject() {
    currentProject.clear();
  }

  @Test
  public void moveItemGetsMediaToMoveFromProjectInsteadOfViewModel() throws IllegalItemOnTrack {
    Video video0 = new Video("video/0", Video.DEFAULT_VOLUME);
    Video video1 = new Video("video/1", Video.DEFAULT_VOLUME);
    Video video2 = new Video("video/2", Video.DEFAULT_VOLUME);
    Video video3 = new Video("video/3", Video.DEFAULT_VOLUME);
    Video video4 = new Video("video/4", Video.DEFAULT_VOLUME);
    Video video5 = new Video("video/5", Video.DEFAULT_VOLUME);
    currentProject.getMediaTrack().insertItem(video0);
    currentProject.getMediaTrack().insertItem(video1);
    currentProject.getMediaTrack().insertItem(video2);
    ArrayList<Video> videoList = new ArrayList<>();
    videoList.add(video3);
    videoList.add(video4);
    videoList.add(video5);
    editPresenter.videoList = videoList;

    editPresenter.finishedMoveItem(0, 1);

    verify(reorderMediaItemUseCase).moveMediaItem(eq(0), eq(1),
            Mockito.any(OnReorderMediaListener.class));
  }
}