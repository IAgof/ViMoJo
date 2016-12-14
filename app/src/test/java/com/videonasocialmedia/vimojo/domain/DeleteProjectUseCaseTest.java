package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.project.RealmProject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.internal.log.RealmLog;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 14/12/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Realm.class, RealmLog.class, RealmQuery.class})
public class DeleteProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock
  DuplicateProjectUseCase mockedDuplicateProjectUseCase;
  @InjectMocks
  DeleteProjectUseCase injectedUseCase;
  DuplicateProjectUseCase duplicateProjectUseCase;
  ProjectRepository projectRepository;
  private Realm mockedRealm;

  public DeleteProjectUseCaseTest(){
    duplicateProjectUseCase = new DuplicateProjectUseCase();
    projectRepository = new ProjectRealmRepository();
  }

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setup() {

    mockStatic(RealmLog.class);
    mockStatic(Realm.class);

    Realm mockRealm = PowerMockito.mock(Realm.class);

    when(Realm.getDefaultInstance()).thenReturn(mockRealm);

    this.mockedRealm = mockRealm;
  }

  @Test
  public void deleteProjectDecrementListProjects(){

    Project currentProject = Project.getInstance(null, null, null);
    duplicateProjectUseCase.duplicate(currentProject);
    duplicateProjectUseCase.duplicate(currentProject);

    RealmQuery<RealmProject> mockedRealmQuery = PowerMockito.mock(RealmQuery.class);
    when(mockedRealm.where(RealmProject.class)).thenReturn(mockedRealmQuery);

    int numProjects = mockedProjectRepository.getListProjects().size();

    injectedUseCase.delete(currentProject);

    assertEquals(numProjects-1, mockedProjectRepository.getListProjects().size());

  }

  @Test
  public void deleteProjectCallsRemoveProjectRepository(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.delete(currentProject);
    verify(mockedProjectRepository).remove(currentProject);
  }
}
