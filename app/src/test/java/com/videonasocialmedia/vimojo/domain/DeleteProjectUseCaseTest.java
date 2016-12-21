package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.domain.project.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
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
public class DeleteProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  DeleteProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void deleteProjectCallsRemoveProjectRepository(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.delete(currentProject);
    verify(mockedProjectRepository).remove(currentProject);
  }
}
