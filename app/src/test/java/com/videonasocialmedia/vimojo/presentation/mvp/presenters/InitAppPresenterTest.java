package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenterTest {

  @Mock CreateDefaultProjectUseCase mockedUseCase;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Context mockedContext;
  @Mock InitAppView mockedInitAppView;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock CameraSettingsDataSource mockedCameraSettingsRepository;
  @Mock RunSyncAdapterHelper mockedRunSyncAdapterHelper;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock SaveComposition mockedSaveComposition;
  private Project currentProject;


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

  private InitAppPresenter getInitAppPresenter() {
    return new InitAppPresenter(mockedContext, mockedInitAppView, mockedSharedPreferences,
        mockedUseCase, mockedCameraSettingsRepository, mockedRunSyncAdapterHelper,
        mockedProjectInstanceCache, mockedSaveComposition);
  }

  public void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
  }
}
