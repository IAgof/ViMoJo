package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jliarte on 2/11/16.
 */
@Singleton
@Component(modules = {ApplicationModule.class, DataRepositoriesModule.class})
public interface SystemComponent {
  void inject(VimojoActivity activity);
}
