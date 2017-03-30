package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACKS_MUSIC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
public class SoundVolumePresenterTest {
  @Mock private SoundVolumeView mockedSoundVolumeView;
  @Mock private RemoveMusicFromProjectUseCase mockedRemoveMusicFromProjectUseCase;
  @InjectMocks SoundVolumePresenter injectedPresenter;
  @Mock private ProjectRepository mockedProjectRepository;
  @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock private GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionsFromProject;


  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  @Test
  public void removeMusicFromProjectCallsRemoveMusicFromProject() throws IllegalItemOnTrack {
    Project currentProject = getCurrentProject();
    Music music = new Music("media/path", 0);
    currentProject.getAudioTracks().get(INDEX_AUDIO_TRACKS_MUSIC).insertItemAt(0, music);

    injectedPresenter.removeMusicFromProject();

    Mockito.verify(mockedRemoveMusicFromProjectUseCase).removeMusicFromProject(music,
        INDEX_AUDIO_TRACKS_MUSIC);
  }

  @Ignore // Ignore until know what to do if composition have music and voice over
  @Test
  public void setVoiceOverRemovesPreviousMusicAndSetsVoiceOverAsMusicInComposition()
          throws IllegalItemOnTrack {
    Project currentProject = getCurrentProject();
    Music music = new Music("music/path", 0);
    assert music.getVolume() == Music.DEFAULT_MUSIC_VOLUME;
    currentProject.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACKS_MUSIC).insertItemAt(0, music);
    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase =
            new RemoveMusicFromProjectUseCase(mockedProjectRepository);
    AddMusicToProjectUseCase addMusicToProjectUseCase =
            new AddMusicToProjectUseCase(mockedProjectRepository);
    AddVoiceOverToProjectUseCase addVoiceOverToProjectUseCase = new AddVoiceOverToProjectUseCase(
            mockedProjectRepository, addMusicToProjectUseCase, removeMusicFromProjectUseCase);
    SoundVolumePresenter presenter = new SoundVolumePresenter(mockedSoundVolumeView,
            removeMusicFromProjectUseCase, addVoiceOverToProjectUseCase,
        mockedGetMediaListFromProjectUseCase, mockedGetPreferencesTransitionsFromProject);

    presenter.setVoiceOver("voice/over/path", 0.6f);

    assertThat(currentProject.getVMComposition().getMusic().getMediaPath(), not("music/path"));
    assertThat(currentProject.getVMComposition().getMusic().getMediaPath(), is("voice/over/path"));
    assertThat(currentProject.getVMComposition().getMusic().getVolume(),
            not(Music.DEFAULT_MUSIC_VOLUME));
    assertThat(currentProject.getVMComposition().getMusic().getVolume(), is(0.6f));
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null);
  }
}