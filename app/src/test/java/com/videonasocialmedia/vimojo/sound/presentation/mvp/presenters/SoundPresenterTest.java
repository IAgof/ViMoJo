package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.os.Build;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 8/03/17.
 */
public class SoundPresenterTest {

  @Mock SoundView mockedSoundView;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock GetMusicFromProjectUseCase mockedGetMusicFromProjectUseCase;
  @Mock GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionFromProjectUseCase;
  @Mock ListenableFuture<Void> mockedTranscodingTask;

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
  public void getMediaListCallsGetMediaListFromProjectUseCase(){
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
    project.getVMComposition().getAudioTracks().get(0).insertItem(music);

    Project currentProject = Project.getInstance(null, null, null);
    assertThat("Current project has music", currentProject.hasMusic(), is(true));

    injectedSoundPresenter.init();

    verify(mockedGetMusicFromProjectUseCase).getMusicFromProject(injectedSoundPresenter);
  }

  @Test
  public void ifProjectHasVideosCallsBindVideoList() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Video video = new Video("video/path");
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItem(video);

    assertThat("Project has video", project.getVMComposition().hasVideos(), is(true));

    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase =
        new GetMediaListFromProjectUseCase();
    SoundPresenter soundPresenter = new SoundPresenter(mockedSoundView,
        getMediaListFromProjectUseCase, mockedGetMusicFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase);
    soundPresenter.init();

    verify(mockedSoundView).bindVideoList(videoList);

  }

  @Test
  public void ifProjectHasNotVideosCallsResetPreview(){
    Project project = getAProject();
    assertThat("Project has not video", project.getVMComposition().hasVideos(), is(false));

    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase =
        new GetMediaListFromProjectUseCase();
    SoundPresenter soundPresenter = new SoundPresenter(mockedSoundView,
        getMediaListFromProjectUseCase, mockedGetMusicFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase);
    soundPresenter.init();

    verify(mockedSoundView).resetPreview();
  }

  @Test
  public void ifProjectHasMusicCallsBindMusicList() throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    project.getVMComposition().getAudioTracks().get(0).insertItem(music);
    GetMusicFromProjectUseCase getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();

    Project currentProject = Project.getInstance(null, null, null);
    assertThat("Current project has music", currentProject.hasMusic(), is(true));

    SoundPresenter soundPresenter = new SoundPresenter(mockedSoundView,
        mockedGetMediaListFromProjectUseCase, getMusicFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase);
    soundPresenter.init();

    verify(mockedSoundView).bindMusicList(musicList);
  }


  @Test
  public void ifProjectHasVoiceOverCallsBindVoiceOverList() throws IllegalItemOnTrack {

    Project project = getAProject();
    String musicPath = "voice/over/path";
    float musicVolume = 0.6f;
    Music voiceOver = new Music(musicPath, musicVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    project.getVMComposition().getAudioTracks().get(0).insertItem(voiceOver);
    GetMusicFromProjectUseCase getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();

    Project currentProject = Project.getInstance(null, null, null);
    assertThat("Current project has music", currentProject.hasMusic(), is(true));
    assertThat("Music is voiceOver", currentProject.getMusic().getMusicTitle(),
        is(Constants.MUSIC_AUDIO_VOICEOVER_TITLE));

    SoundPresenter soundPresenter = new SoundPresenter(mockedSoundView,
        mockedGetMediaListFromProjectUseCase, getMusicFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase);
    soundPresenter.init();
    verify(mockedSoundView).bindVoiceOverList(voiceOverList);

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
  public void ifProjectHasSomeVideoWithErrorsCallsShowWarningTempFile() throws IllegalItemOnTrack {

    Video video1 = new Video("video/path");
    Video video2 = new Video("video/path");
    video2.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());

    assertThat("video1 has not error", video1.getVideoError() == null, is(true));
    assertThat("video2 has error", video2.getVideoError() == null, is(false));

    video1.setTranscodingTask(mockedTranscodingTask);
    video2.setTranscodingTask(mockedTranscodingTask);

    when(mockedTranscodingTask.isCancelled()).thenReturn(true);

    List<Video> videoList = new ArrayList<>();
    videoList.add(video1);
    videoList.add(video2);

    SoundPresenter soundPresenter = new SoundPresenter(mockedSoundView,
        mockedGetMediaListFromProjectUseCase, mockedGetMusicFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase);
    soundPresenter.checkWarningMessageVideosRetrieved(videoList);

    verify(mockedSoundView).showWarningTempFile();

  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", profile);
  }



}
