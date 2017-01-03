package com.videonasocialmedia.vimojo.galleryprojects.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.junit.Assert.assertThat;


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

  @Test
  public void shouldSaveProfileVMCompositionDurationLastModificationIfProjectIsDuplicate() throws IllegalItemOnTrack {
    Project project = getAProject();
    Project duplicateProject = new Project(project);

    assertThat("copy project save duration ", duplicateProject.getDuration(),
        CoreMatchers.is(project.getDuration()));
    assertThat("copy project save last modification ", duplicateProject.getLastModification(),
        CoreMatchers.is(project.getLastModification()));
  }

  @Test
  public void shouldUpdateTitleUuidProjectPathIfProjectIsDuplicate() throws IllegalItemOnTrack {
    Project project = getAProject();
    Project duplicateProject = new Project(project);

    assertThat("copy project change profile ", duplicateProject.getProfile(),
        CoreMatchers.not(project.getProfile()));
    assertThat("copy project change VMComposition ", duplicateProject.getVMComposition(),
        CoreMatchers.not(project.getVMComposition()));
    assertThat("copy project change title ", duplicateProject.getTitle(),
        CoreMatchers.not(project.getTitle()));
    assertThat("copy project change uuid ", duplicateProject.getUuid(),
        CoreMatchers.not(project.getUuid()));
    assertThat("copy project change project path ", duplicateProject.getProjectPath(),
        CoreMatchers.not(project.getProjectPath()));
  }

  private Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

}
