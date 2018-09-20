package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 */
public class SoundPresenterTest {
  @Mock SoundView mockedSoundView;
  @Mock ModifyTrackUseCase mockedModifyTrackUseCase;
  @Mock ProjectInstanceCache mockedProjectInstantCache;
  @Mock UpdateComposition mockedUpdateComposition;
  private Project currentProject;
  private boolean voiceOverAvailable;
  @Mock BackgroundExecutor mockedBackgroundExecutor;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    setAProject();
    when(mockedProjectInstantCache.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void ifProjectHasVideosCallsBindTrack() throws IllegalItemOnTrack {
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    mediaTrack.insertItem(video);
    assertThat("Project has video", currentProject.getVMComposition().hasVideos(), is(true));
    SoundPresenter soundPresenter = getSoundPresenter();

    soundPresenter.updatePresenter();

    verify(mockedSoundView).bindTrack(currentProject.getMediaTrack());
  }

  @Test
  public void ifProjectHasMusicCallsBindMusicListAndTrack() throws IllegalItemOnTrack {
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    currentProject.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC)
        .insertItem(music);
    assertThat("Current project has music", currentProject.hasMusic(), is(true));
    SoundPresenter soundPresenter = getSoundPresenter();

    soundPresenter.updatePresenter();

    verify(mockedSoundView).bindTrack(currentProject.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC));
  }

  @Test
  public void ifProjectHasVoiceOverCallsBindVoiceOverTrack() throws IllegalItemOnTrack {
    String musicPath = "voice/over/path";
    float musicVolume = 0.6f;
    Music voiceOver = new Music(musicPath, musicVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    currentProject.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    currentProject.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), is(true));
    SoundPresenter soundPresenter = getSoundPresenter();

    soundPresenter.updatePresenter();

    verify(mockedSoundView).bindTrack(currentProject.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER));
  }

  @Test
  public void ifProjectHasNotEnableVoiceOverCallsHideVoiceOverCardView() {
    SoundPresenter spySoundPresenter = Mockito.spy(getSoundPresenter());
    spySoundPresenter.voiceOverAvailable = false;

    spySoundPresenter.checkVoiceOverFeatureToggle();

    verify(mockedSoundView).hideVoiceOverTrack();
  }

  @Test
  public void ifProjectHasEnableVoiceOverCallsAddVoiceOverToFabButton() {
    SoundPresenter spySoundPresenter = Mockito.spy(getSoundPresenter());
    spySoundPresenter.voiceOverAvailable = true;

    spySoundPresenter.checkVoiceOverFeatureToggle();

    verify(mockedSoundView).addVoiceOverOptionToFab();
  }

  @Test
  public void initPresenterInProjectWithVideoMusicAndVoiceOverShowTracks()
      throws IllegalItemOnTrack {
    currentProject.getMediaTrack().insertItem(new Video("somePath", Video.DEFAULT_VOLUME));
    currentProject.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    Track musicTrack = currentProject.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(new Music("somePath", 1f, 50));
    musicTrack.setPosition(1);
    Track voiceOverTrack = currentProject.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(new Music("somePath", 1f, 50));
    voiceOverTrack.setPosition(2);
    SoundPresenter soundPresenter = getSoundPresenter();

    soundPresenter.updatePresenter();

    verify(mockedSoundView).showTrackVideo();
    verify(mockedSoundView).showTrackAudioFirst();
    verify(mockedSoundView).showTrackAudioSecond();
  }

  @NonNull
  private SoundPresenter getSoundPresenter() {
    SoundPresenter soundPresenter = new SoundPresenter(mockedSoundView, mockedModifyTrackUseCase,
        mockedProjectInstantCache, mockedUpdateComposition, voiceOverAvailable,
        mockedBackgroundExecutor);
    soundPresenter.currentProject = currentProject;
    return soundPresenter;
  }

  private void setAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path","private/path", profile);
  }

}