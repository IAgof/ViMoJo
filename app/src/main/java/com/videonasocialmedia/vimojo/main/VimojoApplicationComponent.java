package com.videonasocialmedia.vimojo.main;

/**
 * Created by jliarte on 20/04/18.
 */

import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.FeatureToggleModule;
import com.videonasocialmedia.vimojo.main.modules.TrackerModule;
import com.videonasocialmedia.vimojo.main.modules.VimojoApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component for injecting {@link VimojoActivity}
 */
@Singleton @Component(modules = { VimojoApplicationModule.class, DataRepositoriesModule.class,
        FeatureToggleModule.class, TrackerModule.class})
public interface VimojoApplicationComponent {
  void inject(VimojoApplication vimojoApplication);
}
