package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.UpdateVideoFrameRateToProjectUseCase;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoFrameRateToProjectUseCaseTest {

  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateVideoFrameRateToProjectUseCase injectedUseCase;
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
  public void updateFrameRateCallsProjectRepositoryUpdate() {
    injectedUseCase.updateFrameRate(VideoFrameRate.FrameRate.FPS30);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void updateFrameRateSetsProjectProfileFrameRate() {

    assertThat("frameRate", 25, is(currentProject.getProfile().getVideoFrameRate().getFrameRate()));

    injectedUseCase.updateFrameRate(VideoFrameRate.FrameRate.FPS30);

    verify(mockedProjectRepository).update(currentProject);

    VideoFrameRate updatedVideoFrameRate = currentProject.getProfile().getVideoFrameRate();
    assertThat("updatedFrameRate", 30, CoreMatchers.is(updatedVideoFrameRate.getFrameRate()));

  }

  private Project getAProject() {
    String title = "project title";
    String rootPath = "project/root/path";
    String privatePath = "private/path";
    Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance(title, rootPath, privatePath, profile);
  }
}
