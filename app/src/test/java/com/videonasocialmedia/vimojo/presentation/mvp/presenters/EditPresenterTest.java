package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorView;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ToolbarNavigator;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 31/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class EditPresenterTest {
  @Mock private GetMusicFromProjectUseCase mockedGetMusicFromProjectUseCase;
  @Mock private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
  @Mock private EditorView mockedEditorView;
  @Mock private VideonaPlayer mockedVideonaPlayer;
  @Mock private MixpanelAPI mockedMixpanelApi;
  @Mock private UserEventTracker mockedUserEventTracker;
  @Mock private ToolbarNavigator.ProjectModifiedCallBack mockedProjectModifiedCallback;
  @Mock private RemoveVideoFromProjectUseCase mockedVideoRemover;
  @Mock private ReorderMediaItemUseCase mockedMediaItemReorderer;

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

  @Test
  public void constructorSetsCurrentProject() {
    Project videonaProject = getAProject();

    assertThat(injectedEditPresenter.currentProject, is(videonaProject));
  }

  @Test
  public void loadProjectCallsGetMediaListFromProjectUseCase() {
    injectedEditPresenter.loadProject();
    verify(getMediaListFromProjectUseCase).getMediaListFromProject(injectedEditPresenter);
  }

  @Test
  public void loadProjectCallsGetMusicFromProjectUseCaseIfProjectHasMusic() {
    Project videonaProject = getAProject();
    // TODO:(alvaro.martinez) 10/10/16 Check and improve hasMusic, setter not needed.
  }

  @Test
  public void trackClipsReorderedIsCalledOnMediaReordered() {
    Project videonaProject = getAProject();

    injectedEditPresenter.onMediaReordered(null, 2);

    verify(mockedUserEventTracker).trackClipsReordered(videonaProject);
  }

  @Test
  public void loadProjectCallsEditorViewSetMusicIfProjectHasVoiceOver()
          throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "voice/over/path";
    float musicVolume = 0.6f;
    Music voiceOver = new Music(musicPath, musicVolume);
    project.getVMComposition().getAudioTracks().get(0).insertItem(voiceOver);
    GetMusicFromProjectUseCase getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
    EditPresenter presenter = new EditPresenter(mockedEditorView, mockedProjectModifiedCallback,
            mockedUserEventTracker, mockedVideoRemover, mockedMediaItemReorderer,
            getMusicFromProjectUseCase);

    presenter.loadProject();

    verify(mockedEditorView).setMusic(voiceOver);
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", profile);
  }
}
