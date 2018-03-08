package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
  @Mock private MixpanelAPI mockedMixpanelApi;
  @Mock private UserEventTracker mockedUserEventTracker;
  @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock private RemoveVideoFromProjectUseCase mockedVideoRemover;
  @Mock private ReorderMediaItemUseCase mockedMediaItemReorderer;
  @Mock ListenableFuture<Video> mockedTranscodingTask;
  @Mock private VideoTranscodingErrorNotifier mockedVideoTranscodingErrorNotifier;

  @InjectMocks private EditPresenter injectedEditPresenter;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  @Test
  public void constructorSetsUserTracker() {
    assertThat(injectedEditPresenter.userEventTracker, is(mockedUserEventTracker));
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
    Project project = getAProject();
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
    MediaTrack mediaTrack = project.getMediaTrack();
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

    assertThat(getAProject().getVMComposition().hasVideos(), is(false));
    verify(mockedEditorView).goToRecordOrGallery();
  }

  @Test
  public void ifRemoveVideoFromProjectSuccessAndThereAreVideosInProjectUpdateTimeLine() throws IllegalItemOnTrack {
    Project project = getAProject();
    Video video1 = new Video("video/path", 1f);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItem(video1);
    EditPresenter editPresenter = getEditPresenter();

    editPresenter.onRemoveMediaItemFromTrackSuccess();

    assertThat(getAProject().getVMComposition().hasVideos(), is(true));
    verify(mockedEditorView).updateTimeLine();
  }

  @Test
  public void moveItemCallsObtainVideoOnSuccess() throws IllegalItemOnTrack {
    Project project = getAProject();
    Media media1 = new Video("video/path", 1f);
    Media media2 = new Video("video/path", 1f);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItemAt(0, media1);
    mediaTrack.insertItemAt(1,media2);
    int fromPosition = 1;
    int toPosition = 0;
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((OnReorderMediaListener)invocation.getArguments()[2]).onSuccessMediaReordered();
        return null;
      }
    }).when(mockedMediaItemReorderer).moveMediaItem(anyInt(),anyInt(), any(OnReorderMediaListener.class));
    EditPresenter editPresenter = getEditPresenter();

    editPresenter.moveItem(fromPosition, toPosition);

    verify(mockedEditorView).updatePlayerAndTimelineVideoListChanged();
  }

  @NonNull
  public EditPresenter getEditPresenter() {
    return new EditPresenter(mockedEditorView, mockedContext,
        mockedVideoTranscodingErrorNotifier, mockedUserEventTracker,
        mockedGetMediaListFromProjectUseCase,
        mockedVideoRemover, mockedMediaItemReorderer);
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    return Project.getInstance(projectInfo, "/path", "private/path", profile);
  }
}
