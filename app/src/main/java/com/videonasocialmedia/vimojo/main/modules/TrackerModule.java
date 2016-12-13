package com.videonasocialmedia.vimojo.main.modules;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
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
  private final VimojoApplication application;

  public TrackerModule(VimojoApplication application) {
    this.application = application;
  }

  @Provides @Singleton UserEventTracker provideUserEventTracker() {
    return UserEventTracker.getInstance(MixpanelAPI
            .getInstance(application, BuildConfig.MIXPANEL_TOKEN));
  }
}
