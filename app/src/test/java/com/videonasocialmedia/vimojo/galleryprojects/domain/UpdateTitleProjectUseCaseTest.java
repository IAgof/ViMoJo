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

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 22/12/16.
 */

public class UpdateTitleProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateTitleProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void setTitleProjectCallsUpdateProjectRepository(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.setTitle(currentProject, "some title");
    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateTitleProjectAfterUseCase(){
    Project project = getAProject();
    String projectTitle = project.getTitle();
    assertThat("project title are equal ", project.getTitle(), CoreMatchers.is("title"));
    injectedUseCase.setTitle(project, "newTitle");
    assertThat("project title is different from original ",
        project.getTitle(), CoreMatchers.not(projectTitle));
    assertThat("project title is equal to new title",
        project.getTitle(), CoreMatchers.is("newTitle"));
  }

  private Project getAProject() {
    return new Project("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

}
