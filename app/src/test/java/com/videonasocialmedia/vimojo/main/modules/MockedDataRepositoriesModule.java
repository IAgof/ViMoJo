package com.videonasocialmedia.vimojo.main.modules;

import com.birbit.android.jobqueue.JobManager;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.composition.repository.datasource.CompositionApiDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.repository.datasource.BackgroundScheduler;
import com.videonasocialmedia.vimojo.repository.datasource.FakeBackgroundScheduler;

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

  @Override BackgroundScheduler provideBackgroundScheduler(JobManager jobManager) {
    return new FakeBackgroundScheduler();
  }

}
