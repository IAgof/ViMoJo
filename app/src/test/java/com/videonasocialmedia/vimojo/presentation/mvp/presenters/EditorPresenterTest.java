package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
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
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;

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

  @Mock private MixpanelAPI mockedMixpanelAPI;
  @Mock EditorActivityView mockedEditorActivityView;
  @Mock VideonaPlayerView mockedVideonaPlayerView;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock Activity mockedContext;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock CreateDefaultProjectUseCase mockedCreateDefaultProjectUseCase;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock RemoveVideoFromProjectUseCase mockedRemoveVideoFromProjectUseCase;
  @Mock GetAudioFromProjectUseCase mockedGetAudioFromProjectUseCase;
  @Mock GetPreferencesTransitionFromProjectUseCase mocekdGetPreferencesTransitionFromProjectUseCase;
  @Mock RelaunchTranscoderTempBackgroundUseCase mockedRelaunchTranscoderTempBackgroundUseCase;
  @Mock NewClipImporter mockedNewClipImporter;
  @Mock BillingManager mockedBillingManager;
  @Mock SharedPreferences.Editor mockedPreferencesEditor;
  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock
  SaveComposition mockedSaveComposition;
  private Project currentProject;
  private boolean hasBeenProjectExported = false;
  private String videoExportedPath = "videoExportedPath";
  private String currentAppliedTheme = "dark";

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(Log.class);
    setAProject();
    when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
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
            mockedNewClipImporter, mockedBillingManager, mockedProjectInstanceCache, mockedSaveComposition);

    assertThat(editorPresenter.userEventTracker, is(userEventTracker));
  }

  @Test
  public void switchPreferenceWatermarkUpdateProjectAndRepository() {
    EditorPresenter editorPresenter = getEditorPresenter();
    boolean watermarkActivated = true;
    when(mockedSharedPreferences.edit()).thenReturn(mockedPreferencesEditor);
    assert(!currentProject.hasWatermark());

    editorPresenter.switchPreference(watermarkActivated, ConfigPreferences.WATERMARK);

    verify(mockedProjectRepository).setWatermarkActivated(currentProject, watermarkActivated);
  }

  @Test
  public void initCallsInitPreviewFromProjectIfProjectHasNotBeenExported() {
    EditorPresenter spyEditorPresenter = Mockito.spy(getEditorPresenter());
    boolean hasBeenProjectExported = false;

    Futures.addCallback(spyEditorPresenter
                    .updatePresenter(hasBeenProjectExported, videoExportedPath, currentAppliedTheme),
            new FutureCallback<Object>() {
              @Override
              public void onSuccess(@Nullable Object result) {
                verify(spyEditorPresenter).initPreviewFromProject();
              }

              @Override
              public void onFailure(Throwable t) {
                assert false;
              }
            });

  }

  @Test
  public void initCallsInitPreviewFromVideoExportedIfProjectHasBeenExported() {
    EditorPresenter spyEditorPresenter = Mockito.spy(getEditorPresenter());
    boolean hasBeenProjectExported = true;

    Futures.addCallback(spyEditorPresenter
                    .updatePresenter(hasBeenProjectExported, videoExportedPath, currentAppliedTheme),
            new FutureCallback<Object>() {
              @Override
              public void onSuccess(@Nullable Object result) {
                verify(spyEditorPresenter).initPreviewFromVideoExported(videoExportedPath);
              }

              @Override
              public void onFailure(Throwable t) {
                assert false;
              }
            });
  }

  @Test
  public void obtainVideosCallsGetMediaListFromProjectUseCase() throws IllegalItemOnTrack {
    Video video = new Video("somePath", Video.DEFAULT_VOLUME);
    currentProject.getMediaTrack().insertItem(video);
    Assert.assertThat("Project has video", currentProject.getVMComposition().hasVideos(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    Futures.addCallback(editorPresenter.obtainVideoFromProject(), new FutureCallback<Object>() {
              @Override
              public void onSuccess(@Nullable Object result) {
                verify(mockedGetMediaListFromProjectUseCase)
                        .getMediaListFromProject(any(Project.class), any(OnVideosRetrieved.class));

              }

              @Override
              public void onFailure(Throwable t) {
                assert false;
              }
            });
  }

  @Test
  public void ifProjectHasMusicGetMediaListCallsGetMusicListFromProject()
      throws IllegalItemOnTrack {
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    currentProject.getVMComposition().getAudioTracks().get(com.videonasocialmedia.videonamediaframework
        .model.Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedGetAudioFromProjectUseCase).getMusicFromProject(any(Project.class),
        any(GetMusicFromProjectCallback.class));
  }

  @Test
  public void ifProjectHasVideosCallsBindVideoList() throws IllegalItemOnTrack {
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    mediaTrack.insertItem(video);
    Assert.assertThat("Project has video", currentProject.getVMComposition().hasVideos(),
        Matchers.is(true));
    doAnswer(invocation -> {
      ((OnVideosRetrieved)invocation.getArguments()[1]).onVideosRetrieved(videoList);
      return null;
    }).when(mockedGetMediaListFromProjectUseCase).getMediaListFromProject(any(Project.class),
        any(OnVideosRetrieved.class));
    EditorPresenter editorPresenter = getEditorPresenter();

    Futures.addCallback(editorPresenter.obtainVideoFromProject(), new FutureCallback<Object>() {
              @Override
              public void onSuccess(@Nullable Object result) {
                verify(mockedVideonaPlayerView).bindVideoList(any());
                verify(mockedNewClipImporter).relaunchUnfinishedAdaptTasks(currentProject);
              }

              @Override
              public void onFailure(Throwable t) {
                assert false;
              }
            });
  }

  @Test
  public void ifProjectHasVideosAndVideoIsMuteCallsSetVideoMute() throws IllegalItemOnTrack {
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    mediaTrack.insertItem(video);
    mediaTrack.setMute(true);
    Assert.assertThat("Project has video", currentProject.getVMComposition().hasVideos(), Matchers.is(true));
    Assert.assertThat("Project has video on mute", currentProject.getVMComposition()
        .getMediaTrack().isMuted(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).setVideoMute();
  }

  @Test
  public void ifProjectHasVideosAndVideoIsNotMutedCallsSetVideoVolume() throws IllegalItemOnTrack {
    Video video = new Video("video/path", 1f);
    List<Video> videoList = new ArrayList<>();
    videoList.add(video);
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    mediaTrack.insertItem(video);
    float volumeVideo = 0.7f;
    mediaTrack.setVolume(volumeVideo);
    Assert.assertThat("Project has video", currentProject.getVMComposition().hasVideos(), Matchers.is(true));
    Assert.assertThat("Project has video and volume", currentProject.getVMComposition()
        .getMediaTrack().getVolume(), Matchers.is(volumeVideo));
    Assert.assertThat("Project has video and it is not on mute", currentProject.getVMComposition()
        .getMediaTrack().isMuted(), Matchers.is(false));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).setVideoVolume(volumeVideo);
  }

  @Test
  public void ifProjectHasMusicCallsBindMusic() throws IllegalItemOnTrack {
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    currentProject.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC)
        .insertItem(music);
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((GetMusicFromProjectCallback)invocation.getArguments()[1]).onMusicRetrieved(music);
        return null;
      }
    }).when(mockedGetAudioFromProjectUseCase).getMusicFromProject(any(Project.class),
        any(GetMusicFromProjectCallback.class));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).bindMusic(any());
  }

  @Test
  public void ifProjectHasMusicAndIsMutedCallsSetMusicVolume() throws IllegalItemOnTrack {
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    currentProject.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC)
        .insertItem(music);
    Track musicTrack = currentProject.getAudioTracks().get(0);
    musicTrack.setMute(true);
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    assertThat("Music track is muted", currentProject.getAudioTracks().get(0).isMuted(),
        is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).setMusicVolume(0.f);
  }

  @Test
  public void ifProjectHasMusicAndIsNotMutedCallsSetMusicVolume() throws IllegalItemOnTrack {
    String musicPath = "music/path";
    float musicVolume = 0.6f;
    Music music = new Music(musicPath, musicVolume, 0);
    List<Music> musicList = new ArrayList<>();
    musicList.add(music);
    currentProject.getVMComposition().getAudioTracks()
        .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC)
        .insertItem(music);
    Track musicTrack = currentProject.getAudioTracks().get(0);
    float musicTrackVolume = 0.7f;
    musicTrack.setVolume(musicTrackVolume);
    Assert.assertThat("Current project has music", currentProject.hasMusic(), Matchers.is(true));
    assertThat("Music track is not muted", currentProject.getAudioTracks().get(0).isMuted(),
        is(false));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).setMusicVolume(musicTrackVolume);
  }

  @Test
  public void ifProjectHasVoiceOverCallsBindVoiceOver() throws IllegalItemOnTrack {
    String musicPath = "voice/over/path";
    float musicVolume = 0.6f;
    Music voiceOver = new Music(musicPath, musicVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    currentProject.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    currentProject.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    Assert.assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), Matchers.is(true));
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((GetMusicFromProjectCallback)invocation.getArguments()[1]).onMusicRetrieved(voiceOver);
        return null;
      }
    }).when(mockedGetAudioFromProjectUseCase).getVoiceOverFromProject(any(Project.class),
        any(GetMusicFromProjectCallback.class));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).bindVoiceOver(any());
  }

  @Test
  public void ifProjectHasVoiceOverAndIsNotMutedCallsSetVoiceOverVolume() throws IllegalItemOnTrack {
    String musicPath = "voice/over/path";
    float voiceOverVolume = 0.6f;
    Music voiceOver = new Music(musicPath, voiceOverVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    currentProject.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    currentProject.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    Track voiceOverTrack = currentProject.getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER);
    float voiceOverTrackVolume = 0.7f;
    voiceOverTrack.setVolume(voiceOverTrackVolume);
    Assert.assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), Matchers.is(true));
    Assert.assertThat("VoiceOver is not muted", currentProject.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER).isMuted(), Matchers.is(false));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).setVoiceOverVolume(voiceOverTrackVolume);
  }
  @Test
  public void ifProjectHasVoiceOverAndIsMutedCallsSetVoiceOverVolume() throws IllegalItemOnTrack {
    String musicPath = "voice/over/path";
    float voiceOverVolume = 0.6f;
    Music voiceOver = new Music(musicPath, voiceOverVolume, 0);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    List<Music> voiceOverList = new ArrayList<>();
    voiceOverList.add(voiceOver);
    currentProject.getAudioTracks().add(new AudioTrack(INDEX_AUDIO_TRACK_VOICE_OVER));
    currentProject.getVMComposition().getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER)
        .insertItem(voiceOver);
    Track voiceOverTrack = currentProject.getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.setMute(true);
    Assert.assertThat("Current project has voiceOver", currentProject.hasVoiceOver(), Matchers.is(true));
    Assert.assertThat("VoiceOver is muted", currentProject.getAudioTracks()
        .get(INDEX_AUDIO_TRACK_VOICE_OVER).isMuted(), Matchers.is(true));
    EditorPresenter editorPresenter = getEditorPresenter();

    editorPresenter.initPreviewFromProject();

    verify(mockedVideonaPlayerView).setVoiceOverVolume(0.0f);
  }

  private EditorPresenter getEditorPresenter() {
    EditorPresenter editorPresenter = new EditorPresenter(
            mockedEditorActivityView, mockedVideonaPlayerView, mockedSharedPreferences,
            mockedContext, mockedUserEventTracker, mockedCreateDefaultProjectUseCase,
            mockedGetMediaListFromProjectUseCase, mockedRemoveVideoFromProjectUseCase,
            mockedGetAudioFromProjectUseCase, mocekdGetPreferencesTransitionFromProjectUseCase,
            mockedRelaunchTranscoderTempBackgroundUseCase, mockedProjectRepository,
            mockedNewClipImporter, mockedBillingManager, mockedProjectInstanceCache, mockedSaveComposition);
    editorPresenter.currentProject = currentProject;
    return editorPresenter;
  }

  public void setAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
