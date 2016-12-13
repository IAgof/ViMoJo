package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.TrackerModule;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jliarte on 2/11/16.
 */
@Singleton
@Component(modules = {ApplicationModule.class, DataRepositoriesModule.class,
        TrackerModule.class})
public interface SystemComponent {
  ProjectRepository getProjectRepository();
  VideoRepository getVideoRepository();
  UserEventTracker getUserEventTracker();

  void inject(VimojoActivity activity);
}
