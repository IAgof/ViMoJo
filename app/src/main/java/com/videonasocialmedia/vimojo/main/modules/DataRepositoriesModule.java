package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;

import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
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
  ProjectRepository provideDefaultProjectRepository(Context context) {
    return new ProjectRealmRepository(context);
  }

  @Provides @Singleton
  VideoRepository provideDefaultVideoRepository() {
    return new VideoRealmRepository();
  }
}
