package com.videonasocialmedia.vimojo.main.modules;


import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRealmDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.CompositionApiDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmDataSource;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.repository.music.datasource.MusicRealmDataSource;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackRealmDataSource;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoRealmDataSource;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 26/10/16.
 */
@Module
public class DataRepositoriesModule {
  // TODO(jliarte): 7/09/18 move somewhere else?
  private static final int MIN_CONSUMER_COUNT = 1;
  private static final int MAX_CONSUMER_COUNT = 5;
  private static final int LOAD_FACTOR = 1;

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
  TrackDataSource provideDefaultTrackRepository(VideoRealmDataSource videoDataSource,
                                                MusicRealmDataSource musicDataSource) {
    return new TrackRealmDataSource(videoDataSource, musicDataSource);
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

  @Singleton @Provides
  JobManager provideAPIJobManager(Context context) {
    Configuration config = new Configuration.Builder(context).minConsumerCount(MIN_CONSUMER_COUNT)
            .maxConsumerCount(MAX_CONSUMER_COUNT)
            .loadFactor(LOAD_FACTOR)
            .build();
    return new JobManager(config);
  }

}
