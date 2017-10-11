package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.GenerateVoiceOverUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectweb.asm.tree.analysis.SourceValue;

import java.io.File;
import java.io.IOException;

import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 9/10/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class VoiceOverRecordPresenterTest {

  //@InjectMocks VoiceOverRecordPresenter injectedPresenter;

  @Mock VoiceOverView mockedVoiceOverView;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionFromProjectUseCase;
  @Mock Log mockedLog;
  @Mock File mockedFile;
  @Mock GenerateVoiceOverUseCase mockedGenereateVoiceOverUseCase;
  @Mock Recorder mockedRecorder;
  @Mock AudioRecord mockedAudioRecord;
  @Mock PullableSource mockedPullableSource;
  @Mock PullTransport mockedPullTransport;


  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

/*  @Before
  public void setup() {
    PowerMockito.mockStatic(Log.class);
  }*/

  @Test
  public void addVoiceOverShowErrorIfThereAreNotAudiosRecorded() throws IOException {

//    when(mockedAudioRecord.getMinBufferSize(48000, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)).thenReturn(anyInt());
    //when(mockedFile.exists()).thenReturn(false);
    VoiceOverRecordPresenter voiceOverRecordPresenter = Mockito.spy(getVoiceOverPresenter());
    when(voiceOverRecordPresenter.mic()).thenReturn(mockedPullableSource);
    voiceOverRecordPresenter.setupAudioRecorder();

    voiceOverRecordPresenter.addVoiceOver("somePath");

    verify(mockedVoiceOverView).showError(anyString());
  }

  @Test
  public void addVoiceOverCallUseCaseIfThereAreAudiosRecorded() throws IOException {
  }


  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

  private VoiceOverRecordPresenter getVoiceOverPresenter(){
    return new VoiceOverRecordPresenter(mockedVoiceOverView, mockedGetMediaListFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase, mockedGenereateVoiceOverUseCase);
  }
}
