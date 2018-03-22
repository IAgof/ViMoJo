package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenterTest {

  @Mock CreateDefaultProjectUseCase mockedUseCase;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Context mockedContext;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock CameraSettingsRepository mockedCameraSettingsRepository;
  @Mock RunSyncAdapterHelper mockedRunSyncAdapterHelper;
  private Project currentProject;


  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void startLoadingProjectCallsLoadOrCreateProject() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();

    initAppPresenter.startLoadingProject("root/path", "private/path");

    // TODO:(alvaro.martinez) 28/11/17 Learn how to mock BuildConfig values and check values in verify method, not anyString, anyString, anyBoolean
    verify(mockedUseCase).loadOrCreateProject(any(Project.class), anyString(),anyString(), anyBoolean());
  }

  @Test
  public void initAppRunSyncAdapter() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();

    initAppPresenter.init();

    verify(mockedRunSyncAdapterHelper).runSyncAdapterPeriodically();
  }

  private InitAppPresenter getInitAppPresenter() {
    return new InitAppPresenter(mockedContext, mockedSharedPreferences, mockedUseCase,
        mockedProjectRepository, mockedCameraSettingsRepository, mockedRunSyncAdapterHelper);
  }

  public void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
  }
}