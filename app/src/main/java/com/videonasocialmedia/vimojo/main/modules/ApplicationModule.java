package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;

import com.videonasocialmedia.vimojo.main.VimojoApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 27/10/16.
 */
@Module
public class ApplicationModule {
  VimojoApplication vimojoApplication;

  public ApplicationModule(VimojoApplication application) {
    vimojoApplication = application;
  }

  @Provides
  @Singleton
  Context provideVimojoApplication() {
    return vimojoApplication;
  }
}
