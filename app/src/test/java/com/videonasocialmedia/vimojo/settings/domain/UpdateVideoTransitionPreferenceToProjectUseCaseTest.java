package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
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
  private Project currentProject;

  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void updateVideoTransitionPreferenceCallsUpdateRepository(){
    injectedUseCase.setVideoFadeTransitionActivated(currentProject, false);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void shouldUpdateVideoTransitionPreferenceProjectAfterUseCase(){
    boolean videoTransitionActivated = true;
    assertThat("project videoTransitionPreference false by default ",
        currentProject.getVMComposition().isVideoFadeTransitionActivated(), is(false));

    injectedUseCase.setVideoFadeTransitionActivated(currentProject, videoTransitionActivated);

    assertThat("project videoTransitionPreference is value injected",
        currentProject.getVMComposition().isVideoFadeTransitionActivated(), is(videoTransitionActivated));
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    ProjectInfo projectInfo = new ProjectInfo("Project title",
        "Project description", new ArrayList<>());
    currentProject = new Project(projectInfo, "/path","private/path", compositionProfile);
  }
}
