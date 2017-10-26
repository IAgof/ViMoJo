package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.internals.di.PerActivity;
import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.VideoFormatPreferencesModule;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ChooseCameraQualityListPreferences;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ChooseCameraResolutionListPreferences;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ChooseFrameRateListPreferences;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import dagger.Component;

/**
 * Created by jliarte on 14/12/16.
 */
@PerActivity
@Component(dependencies = {SystemComponent.class},
        modules = {VideoFormatPreferencesModule.class})
public interface VideoFormatPreferencesComponent {
  ProjectRepository getProjectRepository();
  void inject(ChooseFrameRateListPreferences preferences);
  void inject(ChooseCameraQualityListPreferences preferences);
  void inject(ChooseCameraResolutionListPreferences preferences);
}
