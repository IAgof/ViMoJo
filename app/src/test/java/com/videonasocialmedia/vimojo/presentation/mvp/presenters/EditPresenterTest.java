package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

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
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
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

  @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock private EditActivityView mockedEditorView;
  @Mock private VideonaPlayer mockedVideonaPlayer;
  @Mock private MixpanelAPI mockedMixpanelApi;
  @Mock private UserEventTracker mockedUserEventTracker;
  @Mock private RemoveVideoFromProjectUseCase mockedVideoRemover;
  @Mock private ReorderMediaItemUseCase mockedMediaItemReorderer;
  @Mock private GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionsFromProject;

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
  public void loadProjectCallsGetMediaListFromProjectUseCase() {
    injectedEditPresenter.obtainVideos();
    verify(mockedGetMediaListFromProjectUseCase).getMediaListFromProject(injectedEditPresenter);
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
    Music voiceOver = new Music(musicPath, musicVolume, 0);
    project.getVMComposition().getAudioTracks().get(0).insertItem(voiceOver);
    GetMusicFromProjectUseCase getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
    EditPresenter presenter = new EditPresenter(mockedEditorView, mockedUserEventTracker,
        mockedVideoRemover, mockedMediaItemReorderer, getMusicFromProjectUseCase,
        mockedGetMediaListFromProjectUseCase,mockedGetPreferencesTransitionsFromProject);

    presenter.init();

    verify(mockedEditorView).setMusic(voiceOver);
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", profile);
  }
}
