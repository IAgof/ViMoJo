package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 1/06/17.
 */

public class ModifyTrackUseCaseTest {

  @InjectMocks ModifyTrackUseCase injectedUseCase;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock TrackRepository mockedTrackRepository;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProject() {
    Project.INSTANCE.clear();
  }

  @Test
  public void setTrackVolumeUpdateProjectRepository(){
    Project project = getAProject();
    AudioTrack track = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    float volumeTrack = 0.85f;

    injectedUseCase.setTrackVolume(track, volumeTrack);

    verify(mockedProjectRepository).update(project);
  }

  @Test
  public void setTrackVolumeUpdateTrackVolume(){
    Project project = getAProject();
    AudioTrack track = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    float volumeTrack = 0.85f;

    injectedUseCase.setTrackVolume(track, volumeTrack);

    assertThat("setTrackVolume update volume track", track.getVolume(), is(volumeTrack));
  }

  @Test
  public void setTrackVolumeUpdateVolumeInTrackItems() throws IllegalItemOnTrack {
    Project project = getAProject();
    MediaTrack mediaTrack = project.getMediaTrack();
    Video video1 = new Video("media/path", 1f);
    Video video2 = new Video("media/path", 0.5f);
    mediaTrack.insertItem(video1);
    mediaTrack.insertItem(video2);
    float mediaTrackVolume = 0.7f;

    injectedUseCase.setTrackVolume(mediaTrack, mediaTrackVolume);

    assertThat("setTrackVolume update volume video 1", project.getMediaTrack().getItems().get(0)
        .getVolume(), is(mediaTrackVolume));

    assertThat("setTrackVolume update volume video 2", project.getMediaTrack().getItems().get(1)
        .getVolume(), is(mediaTrackVolume));

  }

  @Test
  public void setTrackMuteUpdateTrack(){
    Project project = getAProject();
    AudioTrack track = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    assertThat("Default mute track is false", track.isMuted(), is(false));

    injectedUseCase.setTrackMute(track, true);

    assertThat("UseCase update track mute", track.isMuted(), is(true));
  }

  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("project title", "root/path", "private/path", profile);
  }
}
