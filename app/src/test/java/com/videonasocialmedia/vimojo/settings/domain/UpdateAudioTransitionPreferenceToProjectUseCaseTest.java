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
 * Created by alvaro on 10/01/17.
 */

public class UpdateAudioTransitionPreferenceToProjectUseCaseTest {
  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateAudioTransitionPreferenceToProjectUseCase injectedUseCase;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void updateVideoTransitionPreferenceCallsUpdateRepository(){
    Project currentProject = Project.getInstance(null, null, null, null);
    injectedUseCase.setAudioFadeTransitionActivated(false);
    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateVideoTransitionPreferenceProjectAfterUseCase(){
    Project project = getAProject();
    boolean audioTransitionActivated = true;
    assertThat("project videoTransitionPreference false by default ",
        project.isAudioFadeTransitionActivated(), CoreMatchers.is(false));

    injectedUseCase.setAudioFadeTransitionActivated(audioTransitionActivated);

    project = Project.getInstance(null,null,null,null);

    assertThat("project videoTransitionPreference is value injected",
        project.isAudioFadeTransitionActivated(), CoreMatchers.is(audioTransitionActivated));
  }

  private Project getAProject() {
    return new Project("title", "/path","private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
