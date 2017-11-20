package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.UpdateVideoResolutionToProjectUseCase;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;


/**
 * Created by alvaro on 20/10/16.
 */


public class UpdateVideoResolutionToProjectUseCaseTest {

  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateVideoResolutionToProjectUseCase injectedUseCase;
  private Project currentProject;

  @Before
  public void setUp() throws Exception {
    // FIXME: tests are not independent as Project keeps state between tests
    Project singletonProject = Project.getInstance(null, null, null, null);
    singletonProject.clear();
    currentProject = getAProject();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void updateResolutionCallsProjectRepositoryUpdate() {
    injectedUseCase.updateResolution(VideoResolution.Resolution.HD1080);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void updateResolutionSetsProjectProfileResolution() {

    assertThat("height", 720, is(currentProject.getProfile().getVideoResolution().getHeight()));
    assertThat("width", 1280, is(currentProject.getProfile().getVideoResolution().getWidth()));

    injectedUseCase.updateResolution(VideoResolution.Resolution.HD1080);

    VideoResolution updatedVideoResolution = currentProject.getProfile().getVideoResolution();

    assertThat("height", 1080, is(updatedVideoResolution.getHeight()));
    assertThat("width", 1920, is(updatedVideoResolution.getWidth()));

  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
