package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
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
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoListErrorCheckerDelegate;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 */
public class SoundPresenterTest {

  @Mock
  SoundView mockedSoundView;
  @Mock
  GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock
  GetAudioFromProjectUseCase mockedGetAudioFromProjectUseCase;
  @Mock
  GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionFromProjectUseCase;
  @Mock
  ModifyTrackUseCase mockedModifyTrackUseCase;
  @Mock
  VideoListErrorCheckerDelegate mockedVideoListErrorCheckerDelegate;
  @Mock
  ListenableFuture<Void> mockedTranscodingTask;
  @Mock VideoTranscodingErrorNotifier mockedVideoTranscodingErrorNotifier;

  @InjectMocks SoundPresenter injectedSoundPresenter;


  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  @Test
  public void getMediaListCallsGetMediaListFromProjectUseCase() throws IllegalItemOnTrack {
    Project project = getAProject();
    Video video = new Video("somePath", Video.DEFAULT_VOLUME);
    project.getMediaTrack().insertItem(video);
    assertThat("Project has video", project.getVMComposition().hasVideos(), is(true));

    injectedSoundPresenter.init();

    verify(mockedGetMediaListFromProjectUseCase)
        .getMediaListFromProject(injectedSoundPresenter);
  }

  @Test
  public void ifProjectHasMusicGetMediaListCallsGetMusicListFromProject()
      throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    project.getVMComposition().getAudioTracks().get(com.videonasocialmedia.videonamediaframework
        .model.Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);

    Project currentProject = Project.getInstance(null, null, null, null);
    assertThat("Current project has music", currentProject.hasMusic(), is(true));

    injectedSoundPresenter.init();

    verify(mockedGetAudioFromProjectUseCase).getMusicFromProject(injectedSoundPresenter);
  }

  @Test
  public void ifProjectHasVideosCallsBindVideoListAndTrack() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItem(video);
    assertThat("Project has video", project.getVMComposition().hasVideos(), is(true));
    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase =
        new GetMediaListFromProjectUseCase();
    SoundPresenter soundPresenter = getSoundPresenter(getMediaListFromProjectUseCase);

    soundPresenter.init();

    verify(mockedSoundView).bindVideoList(videoList);
    verify(mockedSoundView).bindTrack(project.getMediaTrack());
  }

  @NonNull
  private SoundPresenter getSoundPresenter(GetMediaListFromProjectUseCase getMediaListFromProjectUseCase) {
    return new SoundPresenter(mockedSoundView,
        getMediaListFromProjectUseCase, mockedGetAudioFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase, mockedModifyTrackUseCase,
        mockedVideoListErrorCheckerDelegate);
  }

  @Test
  public void ifProjectHasMusicCallsBindMusicListAndTrack() throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    project.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC)
        .insertItem(music);
    GetAudioFromProjectUseCase getAudioFromProjectUseCase = new GetAudioFromProjectUseCase();

    Project currentProject = Project.getInstance(null, null, null, null);
    assertThat("Current project has music", currentProject.hasMusic(), is(true));

    SoundPresenter soundPresenter = getSoundPresenter(getAudioFromProjectUseCase);
    soundPresenter.init();

    verify(mockedSoundView).bindMusicList(musicList);
    verify(mockedSoundView).bindTrack(project.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC));
  }

  @NonNull
  private SoundPresenter getSoundPresenter(GetAudioFromProjectUseCase getAudioFromProjectUseCase) {
    return new SoundPresenter(mockedSoundView,
        mockedGetMediaListFromProjectUseCase, getAudioFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase, mockedModifyTrackUseCase,
        mockedVideoListErrorCheckerDelegate);
  }


  @Test
  public void ifProjectHasVoiceOverCallsBindVoiceOverListAndTrack() throws IllegalItemOnTrack {

    Project project = getAProject();
    String musicPath = "voice/over/path";
    float musicVolume = 0.6f;
    Music voiceOver = new Music(musicPath, musicVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    project.getAudioTracks().add(new AudioTrack(com.videonasocialmedia.videonamediaframework.model
        .Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    project.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    GetAudioFromProjectUseCase getAudioFromProjectUseCase = new GetAudioFromProjectUseCase();
    Project currentProject = Project.getInstance(null, null, null, null);
    assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), is(true));
    SoundPresenter soundPresenter = getSoundPresenter(getAudioFromProjectUseCase);
    soundPresenter.init();

    verify(mockedSoundView).bindVoiceOverList(voiceOverList);
    verify(mockedSoundView).bindTrack(project.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
  }

  @Test
  public void ifProjectHasNotEnableVoiceOverCallsHideVoiceOverCardView(){
    // TODO:(alvaro.martinez) 27/03/17 How to mock Build.Config values
    boolean FEATURE_TOGGLE_VOICE_OVER = false;
    injectedSoundPresenter.checkVoiceOverFeatureToggle(FEATURE_TOGGLE_VOICE_OVER);
    verify(mockedSoundView).hideVoiceOverCardView();
  }

  @Test
  public void ifProjectHasEnableVoiceOverCallsAddVoiceOverToFabButton(){
    // TODO:(alvaro.martinez) 27/03/17 How to mock Build.Config values
    boolean FEATURE_TOGGLE_VOICE_OVER = true;
    injectedSoundPresenter.checkVoiceOverFeatureToggle(FEATURE_TOGGLE_VOICE_OVER);
    verify(mockedSoundView).addVoiceOverOptionToFab();
  }

  @Test
  public void initPresenterInProjectWithVideoMusicAndVoiceOverShowTracks() throws IllegalItemOnTrack {

    Project project = getAProject();
    project.getMediaTrack().insertItem(new Video("somePath", Video.DEFAULT_VOLUME));
    project.getAudioTracks().add(new AudioTrack(com.videonasocialmedia.videonamediaframework.model
        .Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    Track musicTrack = project.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(new Music("somePath", 1f, 50));
    musicTrack.setPosition(1);
    Track voiceOverTrack = project.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(new Music("somePath", 1f, 50));
    voiceOverTrack.setPosition(2);

    injectedSoundPresenter.init();

    verify(mockedSoundView).showTrackVideo();
    verify(mockedSoundView).showTrackAudioFirst();
    verify(mockedSoundView).showTrackAudioSecond();
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path","private/path", profile);
  }

}