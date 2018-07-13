package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 1/06/17.
 */

public class RemoveAudioUseCaseTest {

  @Mock OnRemoveMediaFinishedListener mockedOnRemoveMediaFinishedListener;
  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock
  TrackDataSource mockedTrackRepository;
  @Mock
  MusicDataSource mockedMusicRepository;
  private Project currentProject;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  @Test
  public void removeMusicUpdatePositionVoiceOverTrack() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    musicTrack.setPosition(1);
    assertThat("Project has music ", currentProject.hasMusic(), is(true));
    assertThat("MusicTrack position is 1 ", musicTrack.getPosition(), is(1));
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack voiceOverTrack = currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    voiceOverTrack.setPosition(2);
    assertThat("Project has voice over ", currentProject.hasVoiceOver(), is(true));
    assertThat("VoiceOverTrack position is 2 ", voiceOverTrack.getPosition(), is(2));
    RemoveAudioUseCase removeAudioUseCase = getRemoveAudioUseCase();

    removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnRemoveMediaFinishedListener);

    assertThat("UseCase has updated voice over track position to 1 ",
        voiceOverTrack.getPosition(), is(1));
  }

  @Test
  public void removeMusicResetPositionMusicTrack() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    musicTrack.setPosition(1);
    assertThat("Project has music ", currentProject.hasMusic(), is(true));
    assertThat("MusicTrack position is 1 ", musicTrack.getPosition(), is(1));
    RemoveAudioUseCase removeAudioUseCase = getRemoveAudioUseCase();

    removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnRemoveMediaFinishedListener);

    assertThat("UseCase has reStart music track position to 0",
        musicTrack.getPosition(), is(0));
  }

  @Test
  public void removeMusicDeleteItemOnTrack() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    musicTrack.setPosition(1);
    assertThat("Project has music ", currentProject.hasMusic(), is(true));
    assertThat("MusicTrack has one item ", musicTrack.getItems().size(), is(1));
    RemoveAudioUseCase removeAudioUseCase = getRemoveAudioUseCase();

    removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnRemoveMediaFinishedListener);

    assertThat("UseCase has delete item on music track",
        musicTrack.getItems().size(), is(0));
  }

  @Test
  public void removeMusicDeleteResetValuesOnTrack() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    float trackVolume = 0.8f;
    boolean trackMute = true;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    musicTrack.setPosition(1);
    musicTrack.setVolume(trackVolume);
    musicTrack.setMute(trackMute);
    assertThat("Project has music ", currentProject.hasMusic(), is(true));
    assertThat("MusicTrack has one item ", musicTrack.getItems().size(), is(1));
    assertThat("Music track volume is trackVolume", musicTrack.getVolume(), is(trackVolume));
    assertThat("Music track mute is trackMute", musicTrack.isMuted(), is(trackMute));
    RemoveAudioUseCase removeAudioUseCase = getRemoveAudioUseCase();

    removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnRemoveMediaFinishedListener);

    AudioTrack updatedMusicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    assertThat("UseCase has update track volume to default", updatedMusicTrack.getVolume(),
        is(Music.DEFAULT_VOLUME));
    assertThat("UseCase has update track mute to default", updatedMusicTrack.isMuted(), is(false));
  }

  @Test
  public void removeMusicUpdateRepositories() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    musicTrack.setPosition(1);
    assertThat("Project has music ", currentProject.hasMusic(), is(true));
    RemoveAudioUseCase removeAudioUseCase = getRemoveAudioUseCase();

    removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnRemoveMediaFinishedListener);

    verify(mockedProjectRepository).update(currentProject);
    verify(mockedTrackRepository).update(musicTrack);
    verify(mockedMusicRepository).remove(music);
  }

  @Test
  public void removeVoiceOverWithOneItemDeleteVoiceOverTrack() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    musicTrack.setPosition(1);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack voiceOverTrack = currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    voiceOverTrack.setPosition(2);
    assertThat("AudioTrack list size is 2", currentProject.getAudioTracks().size(), is(2));
    assertThat("VoiceOver track has only one item", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getItems().size(), is(1));
    RemoveAudioUseCase removeAudioUseCase = getRemoveAudioUseCase();

    removeAudioUseCase.removeMusic(currentProject, voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnRemoveMediaFinishedListener);

    assertThat("UseCase has removed voice over track", currentProject.getAudioTracks().size(), is(1));
  }

  @Test
  public void removeVoiceOverWithOneItemDontDeleteMusicTrack() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    musicTrack.setPosition(1);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack voiceOverTrack = currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    voiceOverTrack.setPosition(2);
    assertThat("AudioTrack list size is 2", currentProject.getAudioTracks().size(), is(2));
    assertThat("Music track has only one item", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getItems().size(), is(1));
    RemoveAudioUseCase removeAudioUseCase = getRemoveAudioUseCase();

    removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnRemoveMediaFinishedListener);

    assertThat("UseCase has removed voice over track", currentProject.getAudioTracks().size(), is(2));
  }

  private RemoveAudioUseCase getRemoveAudioUseCase() {
    return new RemoveAudioUseCase(mockedProjectRepository, mockedTrackRepository, mockedMusicRepository);
  }

  private void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "root/path", "private/path", profile);
  }
}
