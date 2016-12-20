package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.domain.project.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 14/12/16.
 */

public class UpdateCurrentProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateCurrentProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void updateCurrentProjectUpdateLastModificationDate(){
    Project currentProject = Project.getInstance(null, null, null);
    currentProject.setLastModification("FakeDate");
    String oldLastModification = currentProject.getLastModification();

    injectedUseCase.updateLastModifactionProject(currentProject);

    Project actualProject = Project.getInstance(null, null, null);
    String newLastModification = actualProject.getLastModification();

    assertEquals(currentProject,actualProject);
    assertNotEquals(oldLastModification, newLastModification);
  }

  @Test
  public void updateCurrentProjectCallsUpdateProjectRepository(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.updateLastModifactionProject(currentProject);
    verify(mockedProjectRepository).update(currentProject);
  }


}