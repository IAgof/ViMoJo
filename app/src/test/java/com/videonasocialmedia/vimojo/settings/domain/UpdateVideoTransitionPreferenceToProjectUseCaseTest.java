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

public class UpdateVideoTransitionPreferenceToProjectUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  UpdateVideoTransitionPreferenceToProjectUseCase injectedUseCase;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void updateVideoTransitionPreferenceCallsUpdateRepository(){
    Project currentProject = Project.getInstance(null, null, null);
    injectedUseCase.setVideoFadeTransitionActivated(false);
    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateVideoTransitionPreferenceProjectAfterUseCase(){
    Project project = getAProject();
    boolean videoTransitionActivated = true;
    assertThat("project videoTransitionPreference false by default ",
        project.isVideoFadeTransitionActivated(), CoreMatchers.is(false));

    injectedUseCase.setVideoFadeTransitionActivated(videoTransitionActivated);

    project = Project.getInstance(null,null,null);

    assertThat("project videoTransitionPreference is value injected",
        project.isVideoFadeTransitionActivated(), CoreMatchers.is(videoTransitionActivated));
  }

  private Project getAProject() {
    return new Project("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}