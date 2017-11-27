package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import javax.inject.Inject;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  private SharedPreferences sharedPreferences;

  @Inject
  public InitAppPresenter(SharedPreferences sharedPreferences, CreateDefaultProjectUseCase
      createDefaultProjectUseCase) {
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.sharedPreferences = sharedPreferences;
  }

  public void startLoadingProject(String rootPath, String privatePath) {
    boolean isWatermarkFeatured = isWatermarkActivated();
    createDefaultProjectUseCase.loadOrCreateProject(rootPath, privatePath, isWatermarkFeatured);
  }

  private boolean isWatermarkActivated() {
    if(BuildConfig.FEATURE_FORCE_WATERMARK) {
      return true;
    }
    return sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, false);
  }
}
