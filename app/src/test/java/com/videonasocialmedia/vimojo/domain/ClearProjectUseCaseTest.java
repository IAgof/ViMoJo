package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.domain.project.ClearProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 23/10/16.
 */
// TODO(jliarte): 27/10/16 As constants use android SDK we need use robolectric here
@RunWith(RobolectricTestRunner.class)
@PrepareForTest(FileUtils.class)
public class ClearProjectUseCaseTest {

  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks
  ClearProjectUseCase injectedUseCase;

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

  @Ignore
  @Test
  public void clearProjectCallsUtilsCleanDirectory() {
    Project currentProject = Project.getInstance(null, null, null);
    String projectPath = currentProject.getProjectPath();
    PowerMockito.mockStatic(FileUtils.class);

    injectedUseCase.clearProject(currentProject);
    PowerMockito.verifyStatic();
    // FIXME(jliarte): 27/10/16 not working
    FileUtils.cleanDirectory(new File(projectPath));
  }
}