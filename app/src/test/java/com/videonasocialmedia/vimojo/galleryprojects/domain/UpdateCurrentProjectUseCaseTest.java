package com.videonasocialmedia.vimojo.galleryprojects.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.hamcrest.CoreMatchers;
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
    Project currentProject = Project.getInstance(null, null, null, null);
    currentProject.updateDateOfModification("FakeDate");
    String oldLastModification = currentProject.getLastModification();

    injectedUseCase.updateLastModificationAndProjectInstance(currentProject);

    Project actualProject = Project.getInstance(null, null, null, null);
    String newLastModification = actualProject.getLastModification();

    assertEquals(currentProject,actualProject);
    assertNotEquals(oldLastModification, newLastModification);
  }

  @Test
  public void updateCurrentProjectCallsUpdateProjectRepository(){
    Project currentProject = Project.getInstance(null, null, null, null);
    injectedUseCase.updateLastModificationAndProjectInstance(currentProject);
    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateProjectInstanceIfProjectIsUpdate(){
    Project project = getAProject();
    injectedUseCase.updateLastModificationAndProjectInstance(project);
    Project currentProject = Project.getInstance(null,null,null,null);
    assertThat("currentProject is different", currentProject, CoreMatchers.is(project));
  }

  private Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", "private/path", compositionProfile);
  }

}
