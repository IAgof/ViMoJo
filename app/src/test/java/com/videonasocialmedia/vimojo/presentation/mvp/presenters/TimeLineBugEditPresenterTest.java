package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 27/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeLineBugEditPresenterTest {
  @Mock private EditActivityView mockedEditorView;
  @Mock private Context mockedContext;
  @Mock private UserEventTracker mockedUserEventTracker;
  @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock private RemoveVideoFromProjectUseCase mockedVideoRemover;
  @Mock ReorderMediaItemUseCase mockedMediaItemReorderer;
  @Mock private VideoTranscodingErrorNotifier mockedVideoTranscodingErrorNotifier;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock CompositionApiClient mockedCompositionApiClient;
  private Project currentProject;


  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    setAProject();
  }

  private void setAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path",
        compositionProfile);
  }

  @NonNull
  public EditPresenter getEditPresenter() {
    EditPresenter editPresenter = new EditPresenter(
            mockedEditorView, mockedContext, mockedVideoTranscodingErrorNotifier,
            mockedUserEventTracker, mockedGetMediaListFromProjectUseCase, mockedVideoRemover,
            mockedMediaItemReorderer, mockedProjectInstanceCache, mockedCompositionApiClient);
    editPresenter.currentProject = currentProject;
    return editPresenter;
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
    int fromPosition = 0;
    int toPosition = 1;
    EditPresenter editPresenter = getEditPresenter();

    editPresenter.moveClip(fromPosition, toPosition);

    verify(mockedMediaItemReorderer).moveMediaItem(eq(currentProject), eq(fromPosition),
        eq(toPosition), Matchers.any(OnReorderMediaListener.class));
  }
}