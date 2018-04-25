package com.videonasocialmedia.vimojo.main;

/**
 * Created by jliarte on 20/04/18.
 */

import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.VimojoApplicationModule;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component for injecting {@link VimojoActivity}
 */
@Singleton @Component(modules = { VimojoApplicationModule.class, DataRepositoriesModule.class})
public interface VimojoApplicationComponent {
  ProjectRepository getProjectRepository();

  void inject(VimojoApplication vimojoApplication);
}
