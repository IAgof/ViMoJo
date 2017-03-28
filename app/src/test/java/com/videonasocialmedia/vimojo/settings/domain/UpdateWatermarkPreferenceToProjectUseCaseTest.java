package com.videonasocialmedia.vimojo.settings.domain;

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
 * Created by alvaro on 28/02/17.
 */

public class UpdateWatermarkPreferenceToProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateWatermarkPreferenceToProjectUseCase injectedUseCase;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void updateWatermarkPreferenceCallsUpdateRepository(){
    Project currentProject = Project.getInstance(null,null, null);
    injectedUseCase.setWatermarkActivated(true);
    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateWatermarkPreferenceAfterUseCase(){
    Project currentProject = getAProject();
    assertThat("Add watermark is false by default", currentProject.hasWatermark(),
        CoreMatchers.is(false));
    boolean activateWatermark = true;

    injectedUseCase.setWatermarkActivated(activateWatermark);

    currentProject = Project.getInstance(null, null, null);

    assertThat("UseCase update Watermark ", currentProject.hasWatermark(),
        CoreMatchers.is(activateWatermark));

  }

  private Project getAProject() {
    return new Project("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

}
