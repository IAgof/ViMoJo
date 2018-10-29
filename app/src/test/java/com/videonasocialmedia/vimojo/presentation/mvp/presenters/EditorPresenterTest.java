package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.asset.domain.usecase.RemoveMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateCompositionWatermark;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 4/01/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class EditorPresenterTest {

  @Mock EditorActivityView mockedEditorActivityView;
  @Mock
  VideonaPlayer mockedVideonaPlayerView;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Activity mockedContext;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock CreateDefaultProjectUseCase mockedCreateDefaultProjectUseCase;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock RemoveVideoFromProjectUseCase mockedRemoveVideoFromProjectUseCase;
  @Mock RelaunchTranscoderTempBackgroundUseCase mockedRelaunchTranscoderTempBackgroundUseCase;
  @Mock NewClipImporter mockedNewClipImporter;
  @Mock BillingManager mockedBillingManager;
  @Mock SharedPreferences.Editor mockedPreferencesEditor;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock SaveComposition mockedSaveComposition;
  @Mock RemoveMedia mockedRemoveMedia;
  @Mock UpdateCompositionWatermark mockedUpdateCompositionWatermark;
  @Mock UpdateComposition mockedUpdateComposition;
  private Project currentProject;
  private boolean hasBeenProjectExported = false;
  private String videoExportedPath = "videoExportedPath";
  private String currentAppliedTheme = "dark";
  private boolean showWatermarkSwitch;
  private boolean vimojoStoreAvailable;
  private boolean vimojoPlatformAvailable;
  private boolean watermarkIsForced;
  private boolean hideTutorials;
  private boolean amIAVerticalApp;
  @Mock BackgroundExecutor mockedBackgroundExecutor;
  @Mock ListenableFuture mockedListenableFuture;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(Log.class);
    setAProject();
    when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  @Test
  public void constructorSetsUserTracker() {
    UserEventTracker userEventTracker = UserEventTracker.getInstance();
    EditorPresenter editorPresenter = new EditorPresenter(mockedContext, mockedEditorActivityView,
        mockedVideonaPlayerView, mockedSharedPreferences, userEventTracker,
        mockedCreateDefaultProjectUseCase, mockedRemoveVideoFromProjectUseCase,
        mockedRelaunchTranscoderTempBackgroundUseCase,
        mockedNewClipImporter, mockedBillingManager, mockedProjectInstanceCache,
        mockedSaveComposition, mockedRemoveMedia, mockedUpdateCompositionWatermark,
        mockedUpdateComposition, showWatermarkSwitch, vimojoStoreAvailable,
        vimojoPlatformAvailable, watermarkIsForced, hideTutorials, amIAVerticalApp,
        mockedBackgroundExecutor);

    assertThat(editorPresenter.userEventTracker, is(userEventTracker));
  }

  @Test
  public void switchPreferenceWatermarkCallsUseCaseAndUpdateProject() {
    EditorPresenter editorPresenter = getEditorPresenter();
    boolean watermarkActivated = true;
    when(mockedSharedPreferences.edit()).thenReturn(mockedPreferencesEditor);
    assert(!currentProject.hasWatermark());
    when(mockedBackgroundExecutor.submit(any(Runnable.class))).then(new Answer<Runnable>() {
      @Override
      public Runnable answer(InvocationOnMock invocation) throws Throwable {
        Runnable runnable = invocation.getArgument(0);
        runnable.run();
        return null;
      }
    });

    editorPresenter.switchPreference(watermarkActivated, ConfigPreferences.WATERMARK);

    verify(mockedUpdateCompositionWatermark).updateCompositionWatermark(currentProject,
        watermarkActivated);
    verify(mockedUpdateComposition).updateComposition(currentProject);
  }

  @Test
  public void initCallsInitPreviewFromProjectIfProjectHasNotBeenExported() {
    EditorPresenter spyEditorPresenter = Mockito.spy(getEditorPresenter());
    boolean hasBeenProjectExported = false;
    when(mockedBackgroundExecutor.submit(any(Runnable.class))).thenReturn(mockedListenableFuture);

    Futures.addCallback(spyEditorPresenter
                    .updatePresenter(hasBeenProjectExported, videoExportedPath, currentAppliedTheme),
            new FutureCallback<Object>() {
              @Override
              public void onSuccess(@Nullable Object result) {
                verify(spyEditorPresenter).initPreviewFromProject();
              }

              @Override
              public void onFailure(Throwable t) {
                assert false;
              }
            });

  }

  @Test
  public void initCallsInitPreviewFromVideoExportedIfProjectHasBeenExported() {
    EditorPresenter spyEditorPresenter = Mockito.spy(getEditorPresenter());
    boolean hasBeenProjectExported = true;
    when(mockedBackgroundExecutor.submit(any(Runnable.class))).thenReturn(mockedListenableFuture);

    Futures.addCallback(spyEditorPresenter
                    .updatePresenter(hasBeenProjectExported, videoExportedPath, currentAppliedTheme),
            new FutureCallback<Object>() {
              @Override
              public void onSuccess(@Nullable Object result) {
                verify(spyEditorPresenter).initPreviewFromVideoExported(videoExportedPath);
              }

              @Override
              public void onFailure(Throwable t) {
                assert false;
              }
            });
  }

  @Test
  public void obtainVideosCallsGetMediaListFromProjectUseCase() throws IllegalItemOnTrack {
    Video video = new Video("somePath", Video.DEFAULT_VOLUME);
    currentProject.getMediaTrack().insertItem(video);
    Assert.assertThat("Project has video", currentProject.getVMComposition().hasVideos(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();
    when(mockedBackgroundExecutor.submit(any(Runnable.class))).thenReturn(mockedListenableFuture);

    Futures.addCallback(editorPresenter.obtainVideoFromProject(), new FutureCallback<Object>() {
              @Override
              public void onSuccess(@Nullable Object result) {
                verify(mockedGetMediaListFromProjectUseCase)
                        .getMediaListFromProject(any(Project.class), any(OnVideosRetrieved.class));

              }

              @Override
              public void onFailure(Throwable t) {
                assert false;
              }
            });
  }

  private EditorPresenter getEditorPresenter() {
    EditorPresenter editorPresenter = new EditorPresenter(mockedContext, mockedEditorActivityView,
        mockedVideonaPlayerView, mockedSharedPreferences, mockedUserEventTracker,
        mockedCreateDefaultProjectUseCase, mockedRemoveVideoFromProjectUseCase,
        mockedRelaunchTranscoderTempBackgroundUseCase,
        mockedNewClipImporter, mockedBillingManager, mockedProjectInstanceCache,
        mockedSaveComposition, mockedRemoveMedia, mockedUpdateCompositionWatermark,
        mockedUpdateComposition, showWatermarkSwitch, vimojoStoreAvailable,
        vimojoPlatformAvailable, watermarkIsForced, hideTutorials, amIAVerticalApp,
        mockedBackgroundExecutor);
    editorPresenter.currentProject = currentProject;
    return editorPresenter;
  }

  public void setAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
