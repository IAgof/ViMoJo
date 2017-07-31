package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
public class SoundVolumePresenterTest {

  @Mock private SoundVolumeView mockedSoundVolumeView;
  @InjectMocks SoundVolumePresenter injectedPresenter;
  @Mock SoundVolumePresenter mockedSoundVolumePresenter;
  @Mock private MusicRepository mockedMusicRepository;
  @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock private GetAudioFromProjectUseCase mockedGetAudioFromProjectUseCase;
  @Mock private GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionsFromProject;
  @Mock private AddAudioUseCase mockedAddAudioUseCase;
  @Mock private RemoveAudioUseCase mockedRemoveAudioUseCase;
  @Mock Music mockedMusic;
  @Mock OnAddMediaFinishedListener mockedAddMediaFinishedListener;
  @Mock private Context mockedContext;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  @Test
  public void addVoiceOverCallsGoToSoundActivityOnAddMediaItemToTrackSuccess(){
    final float defaultVolume = 0.5f;
    int defaultDuration = 100;
    final Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        OnAddMediaFinishedListener listener =
            invocation.getArgumentAt(2, OnAddMediaFinishedListener.class);
        listener.onAddMediaItemToTrackSuccess(voiceOver);
        return null;
      }
    }).when(mockedAddAudioUseCase).addMusic(eq(voiceOver),
        eq(Constants.INDEX_AUDIO_TRACK_VOICE_OVER),
        any(OnAddMediaFinishedListener.class));

    injectedPresenter.addVoiceOver(voiceOver);

    verify(mockedSoundVolumeView).goToSoundActivity();
  }

  @Test
  public void addVoiceOverCallsShowErrorOnAddMediaItemToTrackError(){
    final float defaultVolume = 0.5f;
    int defaultDuration = 100;
    final Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        OnAddMediaFinishedListener listener =
            invocation.getArgumentAt(2, OnAddMediaFinishedListener.class);
        listener.onAddMediaItemToTrackError();
        return null;
      }
    }).when(mockedAddAudioUseCase).addMusic(eq(voiceOver),
        eq(Constants.INDEX_AUDIO_TRACK_VOICE_OVER),
        any(OnAddMediaFinishedListener.class));

    injectedPresenter.addVoiceOver(voiceOver);

    verify(mockedSoundVolumeView).showError(anyString());
  }

  @Test
  public void removePreviousVoiceOverCallsShowErrorOnRemoveMediaItemFromTrackError() throws IllegalItemOnTrack {
    Project project = getAProject();
    final float defaultVolume = 0.5f;
    int defaultDuration = 100;
    final Music voiceOver = new Music("somePath", defaultVolume, defaultDuration);
    project.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).insertItem(voiceOver);
    assertThat("Project has voice over", project.hasVoiceOver(), is(true));
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        OnRemoveMediaFinishedListener listener =
            invocation.getArgumentAt(2, OnRemoveMediaFinishedListener.class);
        listener.onRemoveMediaItemFromTrackError();
        return null;
      }
    }).when(mockedRemoveAudioUseCase).removeMusic(eq(voiceOver),
        eq(Constants.INDEX_AUDIO_TRACK_VOICE_OVER),
        Matchers.any(OnRemoveMediaFinishedListener.class));

    injectedPresenter.deletePreviousVoiceOver();

    verify(mockedSoundVolumeView).showError(anyString());
  }

  @Test
  public void getVoiceOverAsMusicCreateVoiceOverObject(){

    Music voiceOver = injectedPresenter.getVoiceOverAsMusic("media/path", 0.55f);

    assertThat("Voice over has correct title", voiceOver.getMusicTitle(),
        is(com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE));
    assertThat("Voice over has icon resource", voiceOver.getIconResourceId(),
        is(R.drawable.activity_edit_audio_voice_over_icon));
  }

  @NonNull
  private SoundVolumePresenter getSoundVolumePresenter() {
    return new SoundVolumePresenter(mockedSoundVolumeView, mockedGetMediaListFromProjectUseCase,
        mockedGetPreferencesTransitionsFromProject, mockedGetAudioFromProjectUseCase,
        mockedAddAudioUseCase, mockedRemoveAudioUseCase, mockedContext);
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path", Profile.getInstance(VideoResolution.
            Resolution.HD720, VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

}