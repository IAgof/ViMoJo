package com.videonasocialmedia.vimojo.main.modules;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.repository.project.ProjectDataSource;
import com.videonasocialmedia.vimojo.repository.video.VideoDataSource;

import static org.mockito.Mockito.mock;

/**
 * Created by jliarte on 2/11/16.
 */
public class MockedDataRepositoriesModule extends DataRepositoriesModule {
  @Override
  ProjectDataSource provideDefaultProjectRepository() {
    return mock(ProjectDataSource.class);
  }

  @Override
  VideoDataSource provideDefaultVideoRepository() {
    return mock(VideoDataSource.class);
  }

  @Override
  CameraSettingsDataSource provideDefaultCameraRepository() {
    return mock(CameraSettingsDataSource.class);
  }
}
