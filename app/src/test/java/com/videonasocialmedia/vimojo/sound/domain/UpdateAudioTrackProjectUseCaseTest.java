package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
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
 * Created by alvaro on 12/04/17.
 */

public class UpdateAudioTrackProjectUseCaseTest {
  @InjectMocks
  UpdateAudioTrackProjectUseCase injectedUseCase;

  @Mock
  TrackRepository mockedTrackRepository;

  UpdateAudioTrackProjectUseCase updateAudioTrackProjectUseCase;

  @Before
  public void injectMocks() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void setAudioTrackVolumeUpdateProjectAudioTrackVolume(){
    getAProject().clear();
    Project project = getAProject();
    assertThat("Default volume is 0.5f", project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getVolume(), is(0.5f));

    float volume = 0.8f;
    updateAudioTrackProjectUseCase = new UpdateAudioTrackProjectUseCase(mockedTrackRepository);
    updateAudioTrackProjectUseCase.setAudioTrackVolume(project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC), volume);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Update volume", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getVolume(), is(volume));
  }

  @Test
  public void setAudioTrackSoloUpdateProjectAudioTrackSolo(){
    getAProject().clear();
    Project project = getAProject();
    assertThat("Default solo is false", project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).isSolo(), is(false));

    boolean solo = true;
    updateAudioTrackProjectUseCase = new UpdateAudioTrackProjectUseCase(mockedTrackRepository);
    updateAudioTrackProjectUseCase.setAudioTrackSolo(project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC), solo);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Update volume", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).isSolo(), is(solo));
  }

  @Test
  public void setAudioTrackMuteUpdateProjectAudioTrackMute(){
    getAProject().clear();
    Project project = getAProject();
    assertThat("Default mute is false", project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).isMute(), is(false));

    boolean mute = true;
    updateAudioTrackProjectUseCase = new UpdateAudioTrackProjectUseCase(mockedTrackRepository);
    updateAudioTrackProjectUseCase.setAudioTrackMute(project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC), mute);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Update volume", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).isMute(), is(mute));
  }

  @Test
  public void setAudioTrackVolumeUpdateTrackRepository(){
    Project project = getAProject();
    AudioTrack audioTrack = project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC);

    injectedUseCase.setAudioTrackVolume(audioTrack, 0.8f);

    verify(mockedTrackRepository).update(audioTrack);
  }

  @Test
  public void setAudioTrackSoloUpdateTrackRepository(){
    Project project = getAProject();
    AudioTrack audioTrack = project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC);

    injectedUseCase.setAudioTrackSolo(audioTrack, true);

    verify(mockedTrackRepository).update(audioTrack);
  }

  @Test
  public void setAudioTrackMuteUpdateTrackRepository(){
    Project project = getAProject();
    AudioTrack audioTrack = project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC);

    injectedUseCase.setAudioTrackMute(audioTrack, true);

    verify(mockedTrackRepository).update(audioTrack);
  }

  @Test
  public void addNewTrackUpdateTrackPositionToFirstIfThereIsNotMoreTrackAdded(){
    Project project = getAProject();
    assert !project.hasVoiceOver();
    assert !project.hasMusic();

    updateAudioTrackProjectUseCase = new UpdateAudioTrackProjectUseCase(mockedTrackRepository);
    updateAudioTrackProjectUseCase.addedNewTrack(Constants.INDEX_AUDIO_TRACK_MUSIC);

    int positionFirst = 1;
    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Music track position is 1 ", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getPosition(), is(positionFirst));

  }

  @Test
  public void addNewTrackUpdateTrackPositionToTwoIfThereIsOneAudioTrackAdded() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Music voiceOver = new Music("media/path", 0.5f, 66);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).insertItem(voiceOver);
    assert project.hasVoiceOver();
    assert !project.hasMusic();

    updateAudioTrackProjectUseCase = new UpdateAudioTrackProjectUseCase(mockedTrackRepository);
    updateAudioTrackProjectUseCase.addedNewTrack(Constants.INDEX_AUDIO_TRACK_MUSIC);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Music track position is 2 ", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getPosition(), is(2));

  }

  @Test
  public void removeTrackUpdateTrackPositionIfThereIsOtherAudioTrackAdded() throws IllegalItemOnTrack {

    Project project = getAProject();
    Music voiceOver = new Music("media/path", 0.5f, 66);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).insertItem(voiceOver);
    Music music = new Music("media/path", 0.5f, 66);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);
    assert project.hasVoiceOver();
    assert project.hasMusic();

    updateAudioTrackProjectUseCase = new UpdateAudioTrackProjectUseCase(mockedTrackRepository);
    updateAudioTrackProjectUseCase.removeTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);

    Project currentProject = Project.getInstance(null,null,null);

    assertThat("Music track position is 1 ", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getPosition(), is(1));
  }

  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("project title", "root/path", profile);
  }
}
