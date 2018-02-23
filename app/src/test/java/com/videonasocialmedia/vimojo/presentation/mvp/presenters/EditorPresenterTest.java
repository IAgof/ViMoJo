package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 4/01/17.
 */

public class EditorPresenterTest {

  @Mock private MixpanelAPI mockedMixpanelAPI;
  @Mock EditorActivityView mockedEditorActivityView;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Context mockedContext;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock CreateDefaultProjectUseCase mockedCreateDefaultProjectUseCase;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock RelaunchTranscoderTempBackgroundUseCase mockedRelaunchTranscoderTempBackgroundUseCase;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock NewClipImporter mockedNewClipImporter;
  @Mock BillingManager mockedBillingManager;

  @Mock SharedPreferences.Editor mockedPreferencesEditor;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    Project.getInstance(null, null, null, null).clear();
  }

  @Test
  public void constructorSetsUserTracker() {
    UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
    EditorPresenter editorPresenter = new EditorPresenter(mockedEditorActivityView,
            mockedSharedPreferences, mockedContext, userEventTracker,
            mockedCreateDefaultProjectUseCase, mockedGetMediaListFromProjectUseCase,
            mockedRelaunchTranscoderTempBackgroundUseCase, mockedProjectRepository,
            mockedNewClipImporter, mockedBillingManager);

    assertThat(editorPresenter.userEventTracker, is(userEventTracker));
  }

  @Test
  public void switchPreferenceWatermarkUpdateProjectAndRepository() {
    EditorPresenter editorPresenter = getEditorPresenter();
    Project project = getAProject();
    boolean watermarkActivated = true;
    when(mockedSharedPreferences.edit()).thenReturn(mockedPreferencesEditor);
    assert(!project.hasWatermark());

    editorPresenter.switchPreference(watermarkActivated, ConfigPreferences.WATERMARK);

    verify(mockedProjectRepository).setWatermarkActivated(project, watermarkActivated);
  }

  private EditorPresenter getEditorPresenter() {
    return new EditorPresenter(mockedEditorActivityView, mockedSharedPreferences,
            mockedContext, mockedUserEventTracker, mockedCreateDefaultProjectUseCase,
            mockedGetMediaListFromProjectUseCase, mockedRelaunchTranscoderTempBackgroundUseCase,
            mockedProjectRepository, mockedNewClipImporter, mockedBillingManager);
  }

  public Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    return Project.getInstance(projectInfo, "/path", "private/path", compositionProfile);
  }
}
