package com.videonasocialmedia.vimojo.main.modules;


import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
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
  @Provides @Singleton
  ProjectRepository provideDefaultProjectRepository() {
    return new ProjectRealmRepository();
  }

  @Provides @Singleton
  VideoRepository provideDefaultVideoRepository() {
    return new VideoRealmRepository();
  }

  @Provides @Singleton
  TrackRepository provideDefaultTrackRepository(){
    return new TrackRealmRepository();
  }

  @Provides @Singleton
  MusicRepository provideDefaultMusicRepository(){
    return new MusicRealmRepository();
  }

  @Provides @Singleton
  VideoToAdaptRepository provideDefaultVideoToAdaptRepository() {
    return new VideoToAdaptRealmRepository();
  }
}
