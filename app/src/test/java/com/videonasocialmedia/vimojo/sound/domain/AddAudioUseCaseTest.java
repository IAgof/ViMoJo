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
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackDataSource;

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

public class AddAudioUseCaseTest {
  
  @Mock OnAddMediaFinishedListener mockedOnAddMediaFinishedListener;
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
  public void addMusicUpdateTrackPositionIfProjectHasNotVoiceOver() throws IllegalItemOnTrack {
    assertThat("Project has not voice over ", currentProject.hasVoiceOver(), is(false));
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    assertThat("Initial position in track is 0", musicTrack.getPosition(), is(0));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated music track position to 1, ",
        currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).getPosition(), is(1));
  }

  @Test
  public void addMusicUpdateTrackPositionIfProjectHasVoiceOver() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    AudioTrack voiceOverTrack = currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    assertThat("Project has voiceOver", currentProject.hasVoiceOver(), is(true));
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    assertThat("Initial position in track is 0", musicTrack.getPosition(), is(0));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated music track position to 2, ", currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getPosition(), is(2));
  }

  @Test
  public void addVoiceOverUpdateTrackPositionIfProjectHasNotMusic() throws IllegalItemOnTrack {
    assertThat("Project has not music ", currentProject.hasMusic(), is(false));
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    AudioTrack voiceOverTrack = currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    assertThat("Initial position in track is 0", voiceOverTrack.getPosition(), is(0));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated voice over track position to 2, ",
        currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getPosition(), is(1));
  }

  @Test
  public void addVoiceOverUpdateTrackPositionIfProjectHasMusic() throws IllegalItemOnTrack {
    float defaultVolume = 0.5f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(music);
    assertThat("Project has music", currentProject.hasMusic(), is(true));
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    AudioTrack voiceOverTrack = currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    assertThat("Initial position in track is 0", voiceOverTrack.getPosition(), is(0));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated voice over track position to 2, ",
        currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getPosition(), is(2));
  }

  @Test
  public void addAudioUpdateVolumeToTrack() {
    float defaultVolume = 0.7f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    assertThat("Default volume VoiceOver track is 0.5f",
        currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getVolume(), is(0.5f));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated volume track to 0.7f ",
        currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getVolume(), is(0.7f));
  }

  @Test
  public void addAudioInsertMusicToTrack() {
    float defaultVolume = 0.7f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    assertThat("Default items in track is zero",
        currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getItems().size(),
            is(0));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has updated audio track items ",
        currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).getItems().size(),
            is(1));
  }

  @Test
  public void addAudioCreateNewAudioTrackIfVoiceOverIsAdded() {
    float defaultVolume = 0.7f;
    int defaultDuration = 100;
    Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    assertThat("Default audioTrack list size is one",currentProject.getAudioTracks().size(), is(1));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has added one track to audioTrack list", currentProject.getAudioTracks().size(),
      is(2));
  }

  @Test
  public void addAudioDontCreateNewAudioTrackIfMusicIsAdded() {
    float defaultVolume = 0.7f;
    int defaultDuration = 100;
    Music music = new Music("somePath", defaultVolume, defaultDuration);
    assertThat("Default audioTrack list size is one",currentProject.getAudioTracks().size(), is(1));
    AddAudioUseCase addAudioUseCase = getAddAudioUseCase();

    addAudioUseCase.addMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
        mockedOnAddMediaFinishedListener);

    assertThat("UseCase has added one track to audioTrack list", currentProject.getAudioTracks().size(),
        is(1));
  }


  private AddAudioUseCase getAddAudioUseCase() {
    return new AddAudioUseCase();
  }
  
  private void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.GOOD,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "root/path", "private/path", profile);
  }
}
