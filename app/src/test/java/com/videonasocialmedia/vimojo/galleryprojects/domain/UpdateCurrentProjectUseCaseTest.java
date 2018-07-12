package com.videonasocialmedia.vimojo.galleryprojects.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

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
  private Project currentProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void updateCurrentProjectUpdateLastModificationDate(){
    currentProject.updateDateOfModification("FakeDate");
    String oldLastModification = currentProject.getLastModification();

    injectedUseCase.updateLastModificationAndProjectInstance(currentProject);

    String newLastModification = currentProject.getLastModification();
    assertNotEquals(oldLastModification, newLastModification);
  }

  @Test
  public void updateCurrentProjectCallsUpdateProjectRepository(){
    injectedUseCase.updateLastModificationAndProjectInstance(currentProject);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateProjectInstanceIfProjectIsUpdate(){
    injectedUseCase.updateLastModificationAndProjectInstance(currentProject);

    assertThat("currentProject is different", currentProject, CoreMatchers.is(currentProject));
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }

}
