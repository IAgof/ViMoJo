package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;

import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import static org.mockito.Mockito.mock;

/**
 * Created by jliarte on 2/11/16.
 */
public class MockedDataRepositoriesModule extends DataRepositoriesModule {
  @Override
  ProjectRepository provideDefaultProjectRepository() {
    return mock(ProjectRepository.class);
  }
}
