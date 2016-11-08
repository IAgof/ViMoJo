package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadCurrentProjectUseCaseTest {
  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  LoadCurrentProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void clearProjectInstance() {
    Project instance = Project.INSTANCE;
    if (instance != null) {
      instance.clear();
    }
  }

  @Test
  public void loadCurrentProjectInjectsProjectRepositoryCurrentProjectIntoProjectInstanceIfNull() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    Project currentProject = new Project("title", "root/path", profile);
    assert Project.INSTANCE == null;
    doReturn(currentProject).when(mockedProjectRepository).getCurrentProject();

    Project retrievedProject = injectedUseCase.loadCurrentProject();

    verify(mockedProjectRepository).getCurrentProject();
    assertThat(retrievedProject, is(currentProject));
    assertThat(Project.INSTANCE, is(currentProject));
  }

  @Test
  public void loadCurrentProjectDoesNotChangeNonNullProjectInstance() {
    Project currentProject = Project.getInstance(null, null, null);
    assert Project.INSTANCE != null;

    Project retrievedProject = injectedUseCase.loadCurrentProject();

    verify(mockedProjectRepository, never()).getCurrentProject();
    assertThat(retrievedProject, is(currentProject));
    assertThat(Project.INSTANCE, is(currentProject));
  }
}