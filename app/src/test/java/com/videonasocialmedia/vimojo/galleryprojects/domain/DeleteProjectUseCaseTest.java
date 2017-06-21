package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;


/**
 * Created by alvaro on 14/12/16.
 */
@RunWith(PowerMockRunner.class)
public class DeleteProjectUseCaseTest {

  @Mock ProjectRepository mockedProjectRepository;
  @Mock TrackRepository mockedTrackRepository;
  @InjectMocks
  DeleteProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void deleteProjectCallsRemoveProjectRepository(){
    Project currentProject = getAProject();
    injectedUseCase.delete(currentProject);
    verify(mockedProjectRepository).remove(currentProject);
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", profile);
  }
}
