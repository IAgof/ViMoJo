package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClearProjectUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks ClearProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void clearProjectDeletesCurrentProject() {
    Project currentProject = Project.getInstance(null, null, null);

    injectedUseCase.clearProject(currentProject);

    Mockito.verify(mockedProjectRepository).remove(currentProject);
  }

  @Test
  public void clearProjectSetsProjectInstanceNull() {
    Project currentProject = Project.getInstance(null, null, null);

    injectedUseCase.clearProject(currentProject);

    assertThat(Project.INSTANCE, is(nullValue()));
  }
}