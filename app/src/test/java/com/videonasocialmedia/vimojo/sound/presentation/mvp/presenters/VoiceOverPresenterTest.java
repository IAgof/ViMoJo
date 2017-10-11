package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.util.Log;

import com.videonasocialmedia.avrecorder.AudioRecorder;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.MergeVoiceOverAudiosUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnMergeVoiceOverAudiosListener;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 9/10/17.
 */
@Deprecated
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class VoiceOverPresenterTest {

  @InjectMocks VoiceOverPresenter injectedPresenter;

  @Mock OnMergeVoiceOverAudiosListener mockedOnMergeVoiceOverAudiosListener;
  @Mock VoiceOverView mockedVoiceOverView;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionFromProjectUseCase;
  @Mock MergeVoiceOverAudiosUseCase mockedMergeVoiceOverAudioUseCase;
  @Mock SessionConfig mockedSessionConfig;
  @Mock AudioRecorder mockedAudioRecorder;
  @Mock Log mockedLog;
  @Mock VoiceOverPresenter mockedVoiceOverPresenter;
  @Mock File mockedFile;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setup() {
    PowerMockito.mockStatic(Log.class);
  }

  @After
  public void clearProjectInstance() {
    Project.INSTANCE.clear();
  }

  @Test
  public void addVoiceOverShowErrorIfThereAreNotAudiosRecorded() throws IOException {

    injectedPresenter.addVoiceOver("somePath");

    assertThat(injectedPresenter.getNumVoiceOverRecorded(), is(0));
    verify(mockedVoiceOverView).showError(anyString());
  }

  @Test
  public void addVoiceOverCallUseCaseIfThereAreAudiosRecorded() throws IOException {
    VoiceOverPresenter voiceOverPresenterSpy = Mockito.spy(getVoiceOverPresenter());
    voiceOverPresenterSpy.incrementAudioRecorded();

    voiceOverPresenterSpy.addVoiceOver("somePath");

    verify(mockedMergeVoiceOverAudioUseCase).mergeAudio(anyString(),
        any(OnMergeVoiceOverAudiosListener.class));
    assertThat(voiceOverPresenterSpy.getNumVoiceOverRecorded(), is(1));
  }

  @Test
  public void onMergeVoiceOverAudioSuccessCleanDirectoryAndCleanTempPathDirectory(){
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        OnMergeVoiceOverAudiosListener listener = invocation.getArgument(1);
        listener.onMergeVoiceOverAudioSuccess("outputPath");
        return null;
      }
    }).when(mockedMergeVoiceOverAudioUseCase).mergeAudio
        (anyString(), any(OnMergeVoiceOverAudiosListener.class));

    injectedPresenter.mergeAudio("somePath");

    verify(mockedVoiceOverView).navigateToSoundVolumeActivity(anyString());
    verify(mockedVoiceOverView).cleanTempDirectoryPathVoiceOverRecorded(anyString());
  }

  @Test
  public void onMergeVoiceOverAudioCancelShowError(){
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        OnMergeVoiceOverAudiosListener listener = invocation.getArgument(1);
        listener.onMergeVoiceOverAudioError("error");
        return null;
      }
    }).when(mockedMergeVoiceOverAudioUseCase).mergeAudio
        (anyString(), any(OnMergeVoiceOverAudiosListener.class));

    injectedPresenter.mergeAudio("somePath");

    verify(mockedVoiceOverView).showError(anyString());
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

  private VoiceOverPresenter getVoiceOverPresenter(){
    return new VoiceOverPresenter(mockedVoiceOverView, mockedGetMediaListFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase, mockedMergeVoiceOverAudioUseCase,
        mockedSessionConfig, mockedAudioRecorder);
  }
}
