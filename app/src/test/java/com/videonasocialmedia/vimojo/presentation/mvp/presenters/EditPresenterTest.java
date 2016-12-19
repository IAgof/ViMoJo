package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
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
import com.videonasocialmedia.vimojo.sound.model.VoiceOver;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
  @Mock private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
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
    Project videonaProject = Project.getInstance("title", "/path", Profile.getInstance(null, null, null));

    injectedEditPresenter.onMediaReordered(null, 2);

    verify(mockedUserEventTracker).trackClipsReordered(videonaProject);
  }

  @Test
  public void loadProjectCallsEditorViewSetVoiceOverIfProjectHasVoiceOver() {
    Project project = getAProject();
    project.setVoiceOver(new VoiceOver("voice/over/path", 0.6f));

    injectedEditPresenter.loadProject();

    verify(mockedEditorView).setVoiceOver("voice/over/path", 0.6f);
  }

  // Seems not needed since we already use @InjectMocks annotation
  @NonNull
  public EditPresenter getInjectedEditPresenter() {
    return new EditPresenter(mockedEditorView, mockedProjectModifiedCallback,
            mockedUserEventTracker, mockedVideoRemover, mockedMediaItemReorderer);
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", profile);
  }
}
