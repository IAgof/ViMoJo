package com.videonasocialmedia.vimojo.main.modules;


import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.repository.camera.CameraRealmRepository;
import com.videonasocialmedia.vimojo.repository.camera.CameraRepository;
import com.videonasocialmedia.vimojo.repository.music.MusicRealmRepository;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRealmRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
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
    return new ProjectRealmRepository();
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
  CameraRepository provideDefaultCameraRepository() {
    return new CameraRealmRepository();
  }
}
