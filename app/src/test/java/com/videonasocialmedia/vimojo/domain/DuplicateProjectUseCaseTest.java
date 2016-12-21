package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.domain.project.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;


/**
 * Created by alvaro on 14/12/16.
 */
@RunWith(PowerMockRunner.class)
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
    verify(mockedProjectRepository).createProject(currentProject);
  }

  /*
  @Test
  public void duplicateProjectUpdateUuidAndProjectPath(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.duplicate(currentProject);
    Project newProject = mockedProjectRepository.getListProjects().get(0);
    assertNotEquals(currentProject.getUuid(), newProject.getUuid());
    assertNotEquals(currentProject.getProjectPath(), newProject.getProjectPath());
  }
  */

}
