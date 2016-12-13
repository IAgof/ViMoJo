package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 13/12/16.
 */

public class AddLastVideoExportedToProjectUseCaseTest {

  @Mock
  ProjectRealmRepository mockedProjectRepository;
  @InjectMocks
  AddLastVideoExportedToProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testAddLastVideoExportedToProjectUpdateProjectRepository(){

    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.addLastVideoExportedToProject("somePath");
    verify(mockedProjectRepository).update(currentProject);

  }

}
