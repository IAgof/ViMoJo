package com.videonasocialmedia.vimojo.main.modules;


import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.cut.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRealmRepository;
import com.videonasocialmedia.vimojo.repository.music.datasource.MusicRealmRepository;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.datasource.TrackRealmRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.repository.video.datasource.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 26/10/16.
 */
@Module
public class DataRepositoriesModule {
  @Singleton @Provides
  ProjectRepository provideDefaultProjectRepository() {
    return new ProjectRealmDataSource();
  }

  @Singleton @Provides
  VideoRepository provideDefaultVideoRepository() {
    return new VideoRealmRepository();
  }

  @Singleton @Provides
  TrackRepository provideDefaultTrackRepository(){
    return new TrackRealmRepository();
  }

  @Singleton @Provides
  MusicRepository provideDefaultMusicRepository(){
    return new MusicRealmRepository();
  }

  @Singleton @Provides
  VideoToAdaptRepository provideDefaultVideoToAdaptRepository() {
    return new VideoToAdaptRealmRepository();
  }

  @Singleton @Provides
  CameraSettingsRepository provideDefaultCameraRepository() {
    return new CameraSettingsRealmRepository();
  }

}
