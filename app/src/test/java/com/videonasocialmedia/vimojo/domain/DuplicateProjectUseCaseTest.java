package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.domain.project.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotEquals;


/**
 * Created by alvaro on 14/12/16.
 */

public class DuplicateProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  DuplicateProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Ignore
  @Test
  public void duplicateProjectCallsCreateNewProjectRepository(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.duplicate(currentProject);
    Mockito.verify(mockedProjectRepository).createProject(currentProject);
  }

  @Test
  public void duplicateProjectUpdateUuidAndProjectPath(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.duplicate(currentProject);
    Project newProject = mockedProjectRepository.getListProjects().get(0);
    assertNotEquals(currentProject.getUuid(), newProject.getUuid());
    assertNotEquals(currentProject.getProjectPath(), newProject.getProjectPath());
  }

}
