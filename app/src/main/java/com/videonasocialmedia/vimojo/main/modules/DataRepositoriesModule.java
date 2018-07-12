package com.videonasocialmedia.vimojo.main.modules;


import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRealmDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.CompositionApiDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmDataSource;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.repository.music.datasource.MusicRealmDataSource;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackDataSource;
import com.videonasocialmedia.vimojo.repository.track.datasource.TrackRealmDataSource;
import com.videonasocialmedia.vimojo.repository.video.datasource.VideoRealmDataSource;
import com.videonasocialmedia.vimojo.repository.video.VideoDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 26/10/16.
 */
@Module
public class DataRepositoriesModule {
  @Singleton @Provides
  ProjectRepository provideDefaultProjectRepository(
          ProjectRealmDataSource projectRealmRepository,
          CompositionApiDataSource compositionApiDataSource) {
    return new ProjectRepository(projectRealmRepository, compositionApiDataSource);
  }

  @Singleton @Provides
  VideoDataSource provideDefaultVideoRepository() {
    return new VideoRealmDataSource();
  }

  @Singleton @Provides
  TrackDataSource provideDefaultTrackRepository(){
    return new TrackRealmDataSource();
  }

  @Singleton @Provides
  MusicDataSource provideDefaultMusicRepository(){
    return new MusicRealmDataSource();
  }

  @Singleton @Provides
  VideoToAdaptDataSource provideDefaultVideoToAdaptRepository() {
    return new VideoToAdaptRealmDataSource();
  }

  @Singleton @Provides
  CameraSettingsDataSource provideDefaultCameraRepository() {
    return new CameraSettingsRealmDataSource();
  }

}
