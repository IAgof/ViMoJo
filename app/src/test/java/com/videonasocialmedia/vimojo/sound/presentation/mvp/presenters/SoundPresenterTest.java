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
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoListErrorCheckerDelegate;
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

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 */
public class SoundPresenterTest {
  @Mock SoundView mockedSoundView;
  @Mock ModifyTrackUseCase mockedModifyTrackUseCase;
  @Mock VideoListErrorCheckerDelegate mockedVideoListErrorCheckerDelegate;

  @InjectMocks
  SoundPresenter injectedSoundPresenter;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  @Test
  public void ifProjectHasVideosCallsBindTrack() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItem(video);
    assertThat("Project has video", project.getVMComposition().hasVideos(), is(true));
    SoundPresenter soundPresenter = getSoundPresenter();

    soundPresenter.init();

    verify(mockedSoundView).bindTrack(project.getMediaTrack());
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
    Project currentProject = Project.getInstance(null, null, null, null);
    assertThat("Current project has music", currentProject.hasMusic(), is(true));
    SoundPresenter soundPresenter = getSoundPresenter();

    soundPresenter.init();

    verify(mockedSoundView).bindTrack(project.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC));
  }

  @Test
  public void ifProjectHasVoiceOverCallsBindVoiceOverTrack() throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "voice/over/path";
    float musicVolume = 0.6f;
    Music voiceOver = new Music(musicPath, musicVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    project.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    project.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    Project currentProject = Project.getInstance(null, null, null, null);
    assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), is(true));
    SoundPresenter soundPresenter = getSoundPresenter();

    soundPresenter.init();

    verify(mockedSoundView).bindTrack(project.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER));
  }

  @Test
  public void ifProjectHasNotEnableVoiceOverCallsHideVoiceOverCardView() {
    // TODO:(alvaro.martinez) 27/03/17 How to mock Build.Config values
    boolean FEATURE_TOGGLE_VOICE_OVER = false;
    injectedSoundPresenter.checkVoiceOverFeatureToggle(FEATURE_TOGGLE_VOICE_OVER);
    verify(mockedSoundView).hideVoiceOverCardView();
  }

  @Test
  public void ifProjectHasEnableVoiceOverCallsAddVoiceOverToFabButton() {
    // TODO:(alvaro.martinez) 27/03/17 How to mock Build.Config values
    boolean FEATURE_TOGGLE_VOICE_OVER = true;
    injectedSoundPresenter.checkVoiceOverFeatureToggle(FEATURE_TOGGLE_VOICE_OVER);
    verify(mockedSoundView).addVoiceOverOptionToFab();
  }

  @Test
  public void initPresenterInProjectWithVideoMusicAndVoiceOverShowTracks()
      throws IllegalItemOnTrack {
    Project project = getAProject();
    project.getMediaTrack().insertItem(new Video("somePath", Video.DEFAULT_VOLUME));
    project.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    Track musicTrack = project.getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC);
    musicTrack.insertItem(new Music("somePath", 1f, 50));
    musicTrack.setPosition(1);
    Track voiceOverTrack = project.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(new Music("somePath", 1f, 50));
    voiceOverTrack.setPosition(2);

    injectedSoundPresenter.init();

    verify(mockedSoundView).showTrackVideo();
    verify(mockedSoundView).showTrackAudioFirst();
    verify(mockedSoundView).showTrackAudioSecond();
  }

  @NonNull
  private SoundPresenter getSoundPresenter() {
    return new SoundPresenter(mockedSoundView, mockedModifyTrackUseCase,
        mockedVideoListErrorCheckerDelegate);
  }

  public Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    return Project.getInstance(projectInfo, "/path","private/path", profile);
  }

}