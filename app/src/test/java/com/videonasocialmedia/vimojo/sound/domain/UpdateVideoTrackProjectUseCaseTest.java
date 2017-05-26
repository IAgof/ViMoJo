package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 11/04/17.
 */

public class UpdateVideoTrackProjectUseCaseTest {

  @InjectMocks UpdateVideoTrackProjectUseCase injectedUseCase;

  @Mock
  TrackRepository mockedTrackRepository;

  UpdateVideoTrackProjectUseCase updateVideoTrackProjectUseCase;

  @Before
  public void injectMocks() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void setVideoTrackVolumeUpdateProjectMediaTrackVolume(){
    getAProject().clear();
    Project project = getAProject();
    assertThat("Default volume is 0.5f", project.getMediaTrack().getVolume(), is(0.5f));

    float volume = 0.8f;
    updateVideoTrackProjectUseCase = new UpdateVideoTrackProjectUseCase(mockedTrackRepository);
    updateVideoTrackProjectUseCase.setVideoTrackVolume(project.getMediaTrack(), volume);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Update volume", currentProject.getMediaTrack().getVolume(), is(volume));
  }

  @Test
  public void setVideoTrackSoloUpdateProjectMediaTrackSolo(){
    getAProject().clear();
    Project project = getAProject();
    assertThat("Default solo is false", project.getMediaTrack().isSolo(), is(false));

    boolean solo = true;
    updateVideoTrackProjectUseCase = new UpdateVideoTrackProjectUseCase(mockedTrackRepository);
    updateVideoTrackProjectUseCase.setVideoTrackSolo(project.getMediaTrack(), solo);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Update volume", currentProject.getMediaTrack().isSolo(), is(solo));
  }

  @Test
  public void setVideoTrackMuteUpdateProjectMediaTrackMute(){
    getAProject().clear();
    Project project = getAProject();
    assertThat("Default mute is false", project.getMediaTrack().isMute(), is(false));

    boolean mute = true;
    updateVideoTrackProjectUseCase = new UpdateVideoTrackProjectUseCase(mockedTrackRepository);
    updateVideoTrackProjectUseCase.setVideoTrackMute(project.getMediaTrack(), mute);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Update volume", currentProject.getMediaTrack().isMute(), is(mute));
  }

  @Test
  public void setVideoTrackVolumeUpdateTrackRepository(){
    Project project = getAProject();
    MediaTrack mediaTrack = project.getMediaTrack();

    injectedUseCase.setVideoTrackVolume(mediaTrack, 0.8f);

    verify(mockedTrackRepository).update(mediaTrack);
  }

  @Test
  public void setVideoTrackSoloUpdateTrackRepository(){
    Project project = getAProject();
    MediaTrack mediaTrack = project.getMediaTrack();

    injectedUseCase.setVideoTrackSolo(mediaTrack, true);

    verify(mockedTrackRepository).update(mediaTrack);
  }

  @Test
  public void setVideoTrackMuteUpdateTrackRepository(){
    Project project = getAProject();
    MediaTrack mediaTrack = project.getMediaTrack();

    injectedUseCase.setVideoTrackMute(mediaTrack, true);

    verify(mockedTrackRepository).update(mediaTrack);
  }

  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("project title", "root/path", profile);
  }
}
