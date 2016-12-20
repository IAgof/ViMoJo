package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 19/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddVoiceOverToProjectUseCaseTest {
  @Mock private ProjectRepository mockedProjectRepository;
  @InjectMocks AddVoiceOverToProjectUseCase injectedUseCase;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void setVoiceOverSetsVoiceOverToProject() {
    Project project = getAProject();
    assert ! project.hasVoiceOver();

    injectedUseCase.setVoiceOver(project, "voice/over/path", 0.7f);

    assertThat(project.hasVoiceOver(), is(true));
    assertThat(project.getVoiceOverPath(), is("voice/over/path"));
    assertThat(project.getVoiceOverVolume(), is(0.7f));
  }

  @Test
  public void setVoiceOverCallsUpdateProject() {
    Project project = getAProject();

    injectedUseCase.setVoiceOver(project, "voice/over/path", 0.7f);

    verify(mockedProjectRepository).update(project);
  }

  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
            VideoFrameRate.FrameRate.FPS25);
    return new Project("project title", "root/path", profile);
  }
}