package com.videonasocialmedia.vimojo.main.modules;

import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 1/12/16.
 */
@Module
public class TrackerModule {

  public TrackerModule() {
  }

  @Provides @Singleton UserEventTracker provideUserEventTracker() {
    return UserEventTracker.getInstance();
  }
}
