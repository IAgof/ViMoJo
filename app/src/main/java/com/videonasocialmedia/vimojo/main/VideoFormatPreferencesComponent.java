package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.VideoFormatPreferencesModule;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ChooseCameraQualityListPreferences;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ChooseCameraResolutionListPreferences;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ChooseFrameRateListPreferences;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jliarte on 14/12/16.
 */
@Singleton
@Component(modules = {VideoFormatPreferencesModule.class, DataRepositoriesModule.class})
public interface VideoFormatPreferencesComponent {
  void inject(ChooseFrameRateListPreferences preferences);
  void inject(ChooseCameraQualityListPreferences preferences);
  void inject(ChooseCameraResolutionListPreferences preferences);
}
