package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateWatermarkPreferenceToProjectUseCase;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 28/02/17.
 */

public class UpdateWatermarkPreferenceToProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateWatermarkPreferenceToProjectUseCase injectedUseCase;
  private Project currentProject;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  @Test
  public void updateWatermarkPreferenceCallsUpdateRepository(){
    injectedUseCase.setWatermarkActivated(currentProject, true);
    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateWatermarkPreferenceAfterUseCase(){
    assertThat("Add watermark is false by default", currentProject.hasWatermark(),
        CoreMatchers.is(false));
    boolean activateWatermark = true;

    injectedUseCase.setWatermarkActivated(currentProject, activateWatermark);

    assertThat("UseCase update Watermark ", currentProject.hasWatermark(),
        CoreMatchers.is(activateWatermark));
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }

}
