package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.domain.UpdateVideoQualityToProjectUseCase;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoQualityToProjectUseCaseTest {

  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateVideoQualityToProjectUseCase injectedUseCase;
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
  public void updateQualityCallsProjectRepositoryUpdate() {
    injectedUseCase.updateQuality(VideoQuality.Quality.LOW);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void updateQualitySetProjectProfileQuality(){

    VideoQuality videoQuality = currentProject.getProfile().getVideoQuality();
    assertThat("bitRate", 50 * 1000 * 1000, is(videoQuality.getVideoBitRate()));

    injectedUseCase.updateQuality(VideoQuality.Quality.LOW);

    VideoQuality updatedVideoQuality = currentProject.getProfile().getVideoQuality();
    assertThat("updated bitRate", 16 * 1000 * 1000, is(updatedVideoQuality.getVideoBitRate()));

  }

  private Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

}
