package com.videonasocialmedia.vimojo.main;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.TrackerModule;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
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
  TrackRepository getTrackRepository();
  MusicRepository getMusicRepository();
  UserEventTracker getUserEventTracker();
  SharedPreferences getSharedPreferences();
  ProfileRepository getSharedPreferencesProfileRepository();
  void inject(VimojoActivity activity);
}
