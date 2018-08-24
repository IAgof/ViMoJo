/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.init.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.init.presentation.views.activity.InitRegisterLoginActivity;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.init.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 22/10/16.
 */
@PrepareForTest(BuildConfig.class)
public class InitAppPresenterTest {

  @Mock CreateDefaultProjectUseCase mockedUseCase;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Context mockedContext;
  @Mock InitAppView mockedInitAppView;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock CameraSettingsRepository mockedCameraSettingsRepository;
  @Mock RunSyncAdapterHelper mockedRunSyncAdapterHelper;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock UserAuth0Helper mockedUserAuth0Helper;
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

  @Ignore
  @Test
  public void userNavigateToRecordIfBuildConfigPlatformNotActivated() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();
    // TODO: 24/8/18 Learn how to mock BuildConfig values, seems to be necessary Roboelectric
    PowerMockito.mockStatic(BuildConfig.class);
    Mockito.doReturn(false).when(BuildConfig.FEATURE_VIMOJO_PLATFORM);

    initAppPresenter.setNavigation();

    verify(mockedInitAppView).navigate(RecordCamera2Activity.class);
  }

  @Test
  public void userLoggedNavigateToRecordActivity() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();
    when(mockedUserAuth0Helper.isLogged()).thenReturn(true);

    initAppPresenter.setNavigation();

    verify(mockedInitAppView).navigate(RecordCamera2Activity.class);
  }

  @Test
  public void userNotLoggedNavigateToInitRegisterLoginActivity() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();
    when(mockedUserAuth0Helper.isLogged()).thenReturn(false);

    initAppPresenter.setNavigation();

    verify(mockedInitAppView).navigate(InitRegisterLoginActivity.class);
  }

  private InitAppPresenter getInitAppPresenter() {
    return new InitAppPresenter(mockedContext, mockedInitAppView, mockedSharedPreferences,
        mockedUseCase, mockedCameraSettingsRepository, mockedRunSyncAdapterHelper,
        mockedProjectRepository, mockedProjectInstanceCache, mockedUserAuth0Helper);
  }

  public void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.H_720P, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
  }
}