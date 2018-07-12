package com.videonasocialmedia.vimojo.main.modules;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.cut.repository.datasource.CompositionApiDataSource;
import com.videonasocialmedia.vimojo.cut.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoDataSource;

import static org.mockito.Mockito.mock;

/**
 * Created by jliarte on 2/11/16.
 */
public class MockedDataRepositoriesModule extends DataRepositoriesModule {
  @Override
  ProjectRepository provideDefaultProjectRepository(
          ProjectRealmDataSource projectRealmRepository,
          CompositionApiDataSource compositionApiDataSource) {
    return mock(ProjectRepository.class);
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
