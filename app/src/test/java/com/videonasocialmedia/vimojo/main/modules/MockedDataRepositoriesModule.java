package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import static org.mockito.Mockito.mock;

/**
 * Created by jliarte on 2/11/16.
 */
public class MockedDataRepositoriesModule extends DataRepositoriesModule {
  @Override
  ProjectRepository provideDefaultProjectRepository() {
    return mock(ProjectRepository.class);
  }

  @Override
  VideoRepository provideDefaultVideoRepository() {
    return mock(VideoRepository.class);
  }

  @Override
  CameraSettingsRepository provideDefaultCameraRepository() {
    return mock(CameraSettingsRepository.class);
  }
}
