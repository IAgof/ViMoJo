package com.videonasocialmedia.vimojo.init.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.init.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.init.presentation.views.activity.InitRegisterLoginActivity;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenterTest {

  @Mock CreateDefaultProjectUseCase mockedCreateDefaultProject;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Context mockedContext;
  @Mock InitAppView mockedInitAppView;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock CameraSettingsDataSource mockedCameraSettingsRepository;
  @Mock RunSyncAdapterHelper mockedRunSyncAdapterHelper;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock SaveComposition mockedSaveComposition;
  @Mock UserAuth0Helper mockedUserAuth0Helper;
  private Project currentProject;
  @Mock UserEventTracker mockedUserEventTracker;
  private boolean watermarkIsForced;
  private boolean showAds;
  private boolean amIAVerticalApp;
  private String defaultResolutionSetting;
  private boolean isAppOutOfDate;
  private boolean vimojoPlatformAvailable;
  @Mock BackgroundExecutor mockedBackgroundExecutor;


  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  @Test
  public void initAppRunSyncAdapter() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();

    initAppPresenter.init();

    verify(mockedRunSyncAdapterHelper).runSyncAdapterPeriodically();
  }

  @Test
  public void userNavigateToRecordIfBuildConfigPlatformNotActivated() {
    InitAppPresenter spyInitAppPresenter = spy(getInitAppPresenter());
    spyInitAppPresenter.vimojoPlatformAvailable = false;

    spyInitAppPresenter.setNavigation();

    verify(mockedInitAppView).navigate(RecordCamera2Activity.class);
  }

  @Test
  public void userLoggedNavigateToRecordActivity() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();
    when(mockedUserAuth0Helper.isLogged()).thenReturn(true);

    initAppPresenter.checkLogin();

    verify(mockedInitAppView).navigate(RecordCamera2Activity.class);
  }

  @Test
  public void userNotLoggedNavigateToInitRegisterLoginActivity() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();
    when(mockedUserAuth0Helper.isLogged()).thenReturn(false);

    initAppPresenter.checkLogin();

    verify(mockedInitAppView).navigate(InitRegisterLoginActivity.class);
  }

  private InitAppPresenter getInitAppPresenter() {
    return new InitAppPresenter(mockedContext, mockedInitAppView, mockedSharedPreferences,
        mockedCreateDefaultProject, mockedCameraSettingsRepository, mockedRunSyncAdapterHelper,
        mockedProjectInstanceCache, mockedSaveComposition, watermarkIsForced, showAds,
        amIAVerticalApp, defaultResolutionSetting, isAppOutOfDate, vimojoPlatformAvailable,
        mockedUserAuth0Helper, mockedUserEventTracker, mockedBackgroundExecutor);
  }

  public void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
  }
}
