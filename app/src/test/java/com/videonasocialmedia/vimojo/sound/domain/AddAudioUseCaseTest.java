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
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
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

/**
 * Created by alvaro on 1/06/17.
 */

public class AddAudioUseCaseTest {

  @InjectMocks AddAudioUseCase injectedUseCase;

  @Mock OnAddMediaFinishedListener mockedOnAddMediaFinishedListener;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock TrackRepository mockedTrackRepository;
  @Mock MusicRepository mockedMusicRepository;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProject() {
    Project.INSTANCE.clear();
  }

  @Test
  public void addMusicUpdateTrackPositionIfProjectHasNotVoiceOver() throws IllegalItemOnTrack {
    Project project = getAProject();
    assertThat("Project has not voice over ", project.hasVoiceOver(), is(false));
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    assertThat("Initial position in track is 0", musicTrack.getPosition(), is(0));

    injectedUseCase.addMusic(music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated music track position to 1, ",
        project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).getPosition(), is(1));
  }

  @Test
  public void addMusicUpdateTrackPositionIfProjectHasVoiceOver() throws IllegalItemOnTrack {
    Project project = getAProject();
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack voiceOverTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    assertThat("Project has voiceOver", project.hasVoiceOver(), is(true));
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    assertThat("Initial position in track is 0", musicTrack.getPosition(), is(0));

    injectedUseCase.addMusic(music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated music track position to 2, ",
        project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).getPosition(), is(2));
  }

  @Test
  public void addVoiceOverUpdateTrackPositionIfProjectHasNotMusic() throws IllegalItemOnTrack {
    Project project = getAProject();
    assertThat("Project has not music ", project.hasMusic(), is(false));
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack voiceOverTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    assertThat("Initial position in track is 0", voiceOverTrack.getPosition(), is(0));

    injectedUseCase.addMusic(voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated voice over track position to 2, ",
        project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getPosition(), is(1));
  }

  @Test
  public void addVoiceOverUpdateTrackPositionIfProjectHasMusic() throws IllegalItemOnTrack {
    Project project = getAProject();
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    assertThat("Project has music", project.hasMusic(), is(true));
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack voiceOverTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(music);
    assertThat("Initial position in track is 0", voiceOverTrack.getPosition(), is(0));

    injectedUseCase.addMusic(voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated voice over track position to 2, ",
        project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getPosition(), is(2));
  }

  @Test
  public void addAudioUpdateVolumeToTrack(){
    Project project = getAProject();
    float defaultVolume = 0.7f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    assertThat("Default volume VoiceOver track is 0.5f",
        project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getVolume(), is(0.5f));

    injectedUseCase.addMusic(voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated volume track to 0.7f ",
        project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getVolume(), is(0.7f));
  }

  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("project title", "root/path", profile);
  }
}
