package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 31/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class EditPresenterTest {

  @Mock private EditActivityView mockedEditorView;
  @Mock private Context mockedContext;
  @Mock private UserEventTracker mockedUserEventTracker;
  @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock private RemoveVideoFromProjectUseCase mockedVideoRemover;
  @Mock ReorderMediaItemUseCase mockedMediaItemReorderer;
  @Mock ListenableFuture<Video> mockedTranscodingTask;
  @Mock private VideoTranscodingErrorNotifier mockedVideoTranscodingErrorNotifier;
  @Mock ProjectInstanceCache mockedProjectInstantCache;
  private Project currentProject;


  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
    setAProject();
  }

  @Test
  public void constructorSetsUserTracker() {
    EditPresenter editPresenter = getEditPresenter();

    assertThat(editPresenter.userEventTracker, is(mockedUserEventTracker));
  }

  // TODO(jliarte): 27/04/17 FIXME fix this test
//    @Test
//  public void trackClipsReorderedIsCalledOnMediaReordered() {
//    Project videonaProject = getAProject();
//    injectedEditPresenter.onMediaReordered(null, 2);
//    verify(mockedUserEventTracker).trackClipsReordered(videonaProject);
//  }

  @Test
  public void ifProjectHasSomeVideoWithErrorsCallsShowWarningTempFile() throws IllegalItemOnTrack {
    Video video1 = new Video("video/path", Video.DEFAULT_VOLUME);
    Video video2 = new Video("video/path", Video.DEFAULT_VOLUME);
    video2.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
    assertThat("video1 has not error", video1.getVideoError() == null, is(true));
    assertThat("video2 has error", video2.getVideoError() == null, is(false));
    video1.setTranscodingTask(mockedTranscodingTask);
    video2.setTranscodingTask(mockedTranscodingTask);
    when(mockedTranscodingTask.isCancelled()).thenReturn(true);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video1);
    videoList.add(video2);
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    mediaTrack.insertItem(video1);
    EditPresenter presenter = getEditPresenter();

    presenter.videoListErrorCheckerDelegate
            .checkWarningMessageVideosRetrieved(videoList, mockedVideoTranscodingErrorNotifier);

    ArgumentCaptor<ArrayList> failedVideosCaptor = ArgumentCaptor.forClass(ArrayList.class);
    verify(mockedVideoTranscodingErrorNotifier).showWarningTempFile(failedVideosCaptor.capture());
    assertThat(failedVideosCaptor.getValue().size(), is(2));
    Video failedVideo = (Video) failedVideosCaptor.getValue().get(0);
    Video failedVideo2 = (Video) failedVideosCaptor.getValue().get(1);
    assertThat(failedVideo, is(video1));
    assertThat(failedVideo2, is(video2));
  }

  @Test
  public void ifRemoveVideoFromProjectDeleteLastVideoInProjectCallsNavigateToRecordOrGallery() throws IllegalItemOnTrack {
    EditPresenter editPresenter = getEditPresenter();

    editPresenter.onRemoveMediaItemFromTrackSuccess();

    assertThat(currentProject.getVMComposition().hasVideos(), is(false));
    verify(mockedEditorView).goToRecordOrGallery();
  }

  @Test
  public void ifRemoveVideoFromProjectSuccessAndThereAreVideosInProjectUpdateTimeLine() throws IllegalItemOnTrack {
    Video video1 = new Video("video/path", 1f);
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    mediaTrack.insertItem(video1);
    EditPresenter editPresenter = getEditPresenter();

    editPresenter.onRemoveMediaItemFromTrackSuccess();

    assertThat(currentProject.getVMComposition().hasVideos(), is(true));
    verify(mockedEditorView).updatePlayerAndTimeLineVideoListChanged();
  }

  @Test
  public void moveItemCallsUpdatePlayerVideoListChanged() throws IllegalItemOnTrack {
    Video video1 = new Video("video/path", 1f);
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    mediaTrack.insertItem(video1);
    mediaTrack.insertItem(video1);
    int fromPosition = 1;
    int toPosition = 0;
    Answer<Void> answer = new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        OnReorderMediaListener listener = invocation.getArgument(3);
        listener.onSuccessMediaReordered();
        return null;
      }
    };
    doAnswer(answer).when(mockedMediaItemReorderer).moveMediaItem(eq(currentProject), eq(fromPosition),
        eq(toPosition), Matchers.any(OnReorderMediaListener.class));
    assertThat(currentProject.getMediaTrack().getItems().size(), is(2));
    EditPresenter editPresenter = getEditPresenter();

    editPresenter.finishedMoveItem(fromPosition, toPosition);

    verify(mockedEditorView).updatePlayerVideoListChanged();
  }

  @NonNull
  public EditPresenter getEditPresenter() {
    EditPresenter editPresenter = new EditPresenter(mockedEditorView, mockedContext,
        mockedVideoTranscodingErrorNotifier, mockedUserEventTracker,
        mockedGetMediaListFromProjectUseCase, mockedVideoRemover, mockedMediaItemReorderer,
        mockedProjectInstantCache);
    editPresenter.currentProject = currentProject;
    return editPresenter;
  }

  public void setAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
  }

}
