package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateAudioTransitionPreferenceToProjectUseCaseTest {
  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateAudioTransitionPreferenceToProjectUseCase injectedUseCase;
  private Project currentProject;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  @Test
  public void updateVideoTransitionPreferenceCallsUpdateRepository(){
    injectedUseCase.setAudioFadeTransitionActivated(currentProject, false);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateVideoTransitionPreferenceProjectAfterUseCase(){
    boolean audioTransitionActivated = true;
    assertThat("project videoTransitionPreference false by default ",
        currentProject.getVMComposition().isAudioFadeTransitionActivated(), is(false));

    injectedUseCase.setAudioFadeTransitionActivated(currentProject, audioTransitionActivated);

    assertThat("project videoTransitionPreference is value injected",
        currentProject.getVMComposition().isAudioFadeTransitionActivated(), is(audioTransitionActivated));
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path","private/path", compositionProfile);
  }
}
