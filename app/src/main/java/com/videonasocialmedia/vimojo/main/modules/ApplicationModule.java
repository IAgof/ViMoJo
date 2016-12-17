package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

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

  @Provides @Singleton
  SharedPreferences provideSharedPreferences() {
    return vimojoApplication.getSharedPreferences(
            ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
  }
}
