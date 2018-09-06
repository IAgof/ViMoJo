package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepositoryFromCameraSettings;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 20/04/18.
 */

@Module
public class VimojoApplicationModule {
  private final VimojoApplication vimojoApplication;
  private int defaultCameraIdSelected;

  public VimojoApplicationModule(VimojoApplication application, int defaultCameraIdSelected) {
    vimojoApplication = application;
    this.defaultCameraIdSelected = defaultCameraIdSelected;
  }

  public VimojoApplicationModule(VimojoApplication application) {
    vimojoApplication = application;
  }

  @Provides @Singleton
  Context provideContext() {
    return vimojoApplication.getApplicationContext();
  }

  @Provides
  ProfileRepository provideProfileRepository(
          CameraSettingsDataSource cameraSettingsRepository,
          @Named("showCameraProAvailable") boolean showCameraPro,
          @Named("defaultResolutionSetting") String defaultResolutionSetting) {
    return new ProfileRepositoryFromCameraSettings(cameraSettingsRepository,
            defaultCameraIdSelected, showCameraPro, defaultResolutionSetting);
  }

  @Provides @Singleton
  SharedPreferences provideSharedPreferences() {
    return vimojoApplication.getSharedPreferences(
            ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
  }

}
