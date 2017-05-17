package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateAudioTrackProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;


/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
public class SoundVolumePresenterTest {
  @Mock private SoundVolumeView mockedSoundVolumeView;
  @Mock AddVoiceOverToProjectUseCase mockedAddVoiceOverToProjectUseCase;
  @Mock private RemoveMusicFromProjectUseCase mockedRemoveMusicFromProjectUseCase;
  @InjectMocks SoundVolumePresenter injectedPresenter;
  @Mock private MusicRepository mockedMusicRepository;
  @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock private GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionsFromProject;
  @Mock UpdateAudioTrackProjectUseCase mockedUpdateAudioTrackProjectUseCase;
  @Mock UpdateCurrentProjectUseCase mockedUpdateCurrentProjectUseCase;


  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  /**@Test
  public void removeMusicFromProjectCallsRemoveMusicFromProject() throws IllegalItemOnTrack {
    Project currentProject = getCurrentProject();
    Music music = new Music("media/path", 0);
    currentProject.getAudioTracks().get(INDEX_AUDIO_TRACK_MUSIC).insertItemAt(0, music);

    injectedPresenter.removeMusicFromProject();

    Mockito.verify(mockedRemoveMusicFromProjectUseCase).removeMusicFromProject(music,
        INDEX_AUDIO_TRACK_MUSIC);
  }**/

  @Test
  public void setVoiceOverCallsGoToSoundActivity(){

    SoundVolumePresenter soundVolumePresenter = new SoundVolumePresenter(mockedSoundVolumeView,
        mockedAddVoiceOverToProjectUseCase,mockedGetMediaListFromProjectUseCase,
        mockedGetPreferencesTransitionsFromProject,mockedUpdateAudioTrackProjectUseCase,
        mockedUpdateCurrentProjectUseCase);

    soundVolumePresenter.setVoiceOver("media/path", 0.55f);

    verify(mockedSoundVolumeView).goToSoundActivity();
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null);
  }
}