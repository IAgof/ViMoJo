package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
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
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 4/01/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class EditorPresenterTest {

  @InjectMocks EditorPresenter injectedEditorPresenter;

  @Mock private MixpanelAPI mockedMixpanelAPI;
  @Mock EditorActivityView mockedEditorActivityView;
  @Mock VideonaPlayerView mockedVideonaPlayerView;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Context mockedContext;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock CreateDefaultProjectUseCase mockedCreateDefaultProjectUseCase;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock RemoveVideoFromProjectUseCase mockedRemoveVideoFromProjectUseCase;
  @Mock GetAudioFromProjectUseCase mockedGetAudioFromProjectUseCase;
  @Mock GetPreferencesTransitionFromProjectUseCase mocekdGetPreferencesTransitionFromProjectUseCase;
  @Mock RelaunchTranscoderTempBackgroundUseCase mockedRelaunchTranscoderTempBackgroundUseCase;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock NewClipImporter mockedNewClipImporter;
  @Mock BillingManager mockedBillingManager;

  @Mock SharedPreferences.Editor mockedPreferencesEditor;

  @Mock Project mockedProject;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(Log.class);
  }

  @After
  public void tearDown() {
    Project.getInstance(null, null, null, null).clear();
  }

  @Test
  public void constructorSetsUserTracker() {
    UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
    EditorPresenter editorPresenter = new EditorPresenter(mockedEditorActivityView,
            mockedVideonaPlayerView, mockedSharedPreferences, mockedContext, userEventTracker,
            mockedCreateDefaultProjectUseCase, mockedGetMediaListFromProjectUseCase,
            mockedRemoveVideoFromProjectUseCase, mockedGetAudioFromProjectUseCase,
            mocekdGetPreferencesTransitionFromProjectUseCase,
            mockedRelaunchTranscoderTempBackgroundUseCase, mockedProjectRepository,
            mockedNewClipImporter, mockedBillingManager);

    assertThat(editorPresenter.userEventTracker, is(userEventTracker));
  }

  @Test
  public void switchPreferenceWatermarkUpdateProjectAndRepository() {
    EditorPresenter editorPresenter = getEditorPresenter();
    Project project = getAProject();
    boolean watermarkActivated = true;
    when(mockedSharedPreferences.edit()).thenReturn(mockedPreferencesEditor);
    assert(!project.hasWatermark());

    editorPresenter.switchPreference(watermarkActivated, ConfigPreferences.WATERMARK);

    verify(mockedProjectRepository).setWatermarkActivated(project, watermarkActivated);
  }

  @Test
  public void getMediaListCallsGetMediaListFromProjectUseCase() throws IllegalItemOnTrack {
    Project project = getAProject();
    Video video = new Video("somePath", Video.DEFAULT_VOLUME);
    project.getMediaTrack().insertItem(video);
    Assert.assertThat("Project has video", project.getVMComposition().hasVideos(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedGetMediaListFromProjectUseCase)
        .getMediaListFromProject(any(OnVideosRetrieved.class));
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
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedGetAudioFromProjectUseCase).getMusicFromProject(any(GetMusicFromProjectCallback.class));
  }

  @Test
  public void ifProjectHasVideosCallsBindVideoList() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItem(video);
    Assert.assertThat("Project has video", project.getVMComposition().hasVideos(), Matchers.is(true));
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((OnVideosRetrieved)invocation.getArguments()[0]).onVideosRetrieved(videoList);
        return null;
      }
    }).when(mockedGetMediaListFromProjectUseCase).getMediaListFromProject(any(OnVideosRetrieved.class));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).bindVideoList(any());
    verify(mockedNewClipImporter).relaunchUnfinishedAdaptTasks(project);
    verify(mockedEditorActivityView).hideProgressDialog();
  }

  @Test
  public void ifProjectHasNotVideosCallsBindVideoList() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Assert.assertThat("Project has not video", project.getVMComposition().hasVideos(), Matchers.is(false));
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((OnVideosRetrieved)invocation.getArguments()[0]).onNoVideosRetrieved();
        return null;
      }
    }).when(mockedGetMediaListFromProjectUseCase).getMediaListFromProject(any(OnVideosRetrieved.class));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedEditorActivityView).hideProgressDialog();
    verify(mockedEditorActivityView).goToRecordOrGalleryScreen();
  }

  @Test
  public void ifProjectHasVideosAndVideoIsMuteCallsSetVideoMute() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItem(video);
    mediaTrack.setMute(true);
    Assert.assertThat("Project has video", project.getVMComposition().hasVideos(), Matchers.is(true));
    Assert.assertThat("Project has video on mute", project.getVMComposition()
        .getMediaTrack().isMuted(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).setVideoMute();
  }

  @Test
  public void ifProjectHasVideosAndVideoIsNotMutedCallsSetVideoVolume() throws IllegalItemOnTrack {
    getAProject().clear();
    Project project = getAProject();
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = project.getMediaTrack();
    mediaTrack.insertItem(video);
    float volumeVideo = 0.7f;
    mediaTrack.setVolume(volumeVideo);
    Assert.assertThat("Project has video", project.getVMComposition().hasVideos(), Matchers.is(true));
    Assert.assertThat("Project has video and volume", project.getVMComposition()
        .getMediaTrack().getVolume(), Matchers.is(volumeVideo));
    Assert.assertThat("Project has video and it is not on mute", project.getVMComposition()
        .getMediaTrack().isMuted(), Matchers.is(false));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).setVideoVolume(volumeVideo);
  }

  @Test
  public void ifProjectHasMusicCallsBindMusic() throws IllegalItemOnTrack {
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
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((GetMusicFromProjectCallback)invocation.getArguments()[0]).onMusicRetrieved(music);
        return null;
      }
    }).when(mockedGetAudioFromProjectUseCase).getMusicFromProject(any(GetMusicFromProjectCallback.class));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).bindMusic(any());
  }

  @Test
  public void ifProjectHasMusicAndIsMutedCallsSetMusicVolume() throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    project.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC)
        .insertItem(music);
    Track musicTrack = project.getAudioTracks().get(0);
    musicTrack.setMute(true);
    Project currentProject = Project.getInstance(null, null, null, null);
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    assertThat("Music track is muted", currentProject.getAudioTracks().get(0).isMuted(),
        is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).setMusicVolume(0.f);
  }

  @Test
  public void ifProjectHasMusicAndIsNotMutedCallsSetMusicVolume() throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    project.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC)
        .insertItem(music);
    Track musicTrack = project.getAudioTracks().get(0);
    float musicTrackVolume = 0.7f;
    musicTrack.setVolume(musicTrackVolume);
    Project currentProject = Project.getInstance(null, null, null, null);
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    assertThat("Music track is not muted", currentProject.getAudioTracks().get(0).isMuted(),
        is(false));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).setMusicVolume(musicTrackVolume);
  }

  @Test
  public void ifProjectHasVoiceOverCallsBindVoiceOver() throws IllegalItemOnTrack {
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
    Assert.assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), Matchers.is(true));
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((GetMusicFromProjectCallback)invocation.getArguments()[0]).onMusicRetrieved(voiceOver);
        return null;
      }
    }).when(mockedGetAudioFromProjectUseCase).getVoiceOverFromProject(any(GetMusicFromProjectCallback.class));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).bindVoiceOver(any());
  }

  @Test
  public void ifProjectHasVoiceOverAndIsNotMutedCallsSetVoiceOverVolume() throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "voice/over/path";
    float voiceOverVolume = 0.6f;
    Music voiceOver = new Music(musicPath, voiceOverVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    project.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    project.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    Track voiceOverTrack = project.getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER);
    float voiceOverTrackVolume = 0.7f;
    voiceOverTrack.setVolume(voiceOverTrackVolume);
    Project currentProject = Project.getInstance(null, null, null, null);
    Assert.assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), Matchers.is(true));
    Assert.assertThat("VoiceOver is not muted", currentProject.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER).isMuted(), Matchers.is(false));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).setVoiceOverVolume(voiceOverTrackVolume);
  }
  @Test
  public void ifProjectHasVoiceOverAndIsMutedCallsSetVoiceOverVolume() throws IllegalItemOnTrack {
    Project project = getAProject();
    String musicPath = "voice/over/path";
    float voiceOverVolume = 0.6f;
    Music voiceOver = new Music(musicPath, voiceOverVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    project.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    project.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    Track voiceOverTrack = project.getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.setMute(true);
    Project currentProject = Project.getInstance(null, null, null, null);
    Assert.assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), Matchers.is(true));
    Assert.assertThat("VoiceOver is muted", currentProject.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER).isMuted(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.init();

    verify(mockedVideonaPlayerView).setVoiceOverVolume(0.0f);
  }

  private EditorPresenter getEditorPresenter() {
    return new EditorPresenter(mockedEditorActivityView,
        mockedVideonaPlayerView, mockedSharedPreferences, mockedContext, mockedUserEventTracker,
        mockedCreateDefaultProjectUseCase, mockedGetMediaListFromProjectUseCase,
        mockedRemoveVideoFromProjectUseCase, mockedGetAudioFromProjectUseCase,
        mocekdGetPreferencesTransitionFromProjectUseCase,
        mockedRelaunchTranscoderTempBackgroundUseCase, mockedProjectRepository,
        mockedNewClipImporter, mockedBillingManager);
  }

  public Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    return Project.getInstance(projectInfo, "/path", "private/path", compositionProfile);
  }
}
