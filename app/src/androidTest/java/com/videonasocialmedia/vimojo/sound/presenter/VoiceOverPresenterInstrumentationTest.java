package com.videonasocialmedia.vimojo.sound.presenter;

import android.support.test.runner.AndroidJUnit4;
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
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverPresenter;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 9/10/17.
 */

@RunWith(AndroidJUnit4.class)
public class VoiceOverPresenterInstrumentationTest {

  @Mock
  OnMergeVoiceOverAudiosListener onMergeVoiceOverAudiosListener;
  @Mock
  VoiceOverView mockedVoiceOverView;
  @Mock
  GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock
  GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionFromProjectUseCase;
  @Mock
  MergeVoiceOverAudiosUseCase mockedMergeVoiceOverAudioUseCase;

  @InjectMocks VoiceOverPresenter injectedVoiceOverPresenter;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void onMergeVoiceOverAudioSuccessCleanDirectory(){
    Project project = getAProject();
    MergeVoiceOverAudiosUseCase mergeVoiceOverUseCase = new MergeVoiceOverAudiosUseCase();

    //VoiceOverPresenter voiceOverPresenter = getInjectedPresenter(mergeVoiceOverUseCase);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        OnMergeVoiceOverAudiosListener listener = invocation.getArgument(1);
        listener.onMergeVoiceOverAudioSuccess("outputPath");
        return null;
      }
    }).when(mockedMergeVoiceOverAudioUseCase).mergeAudio
        (anyString(), any(OnMergeVoiceOverAudiosListener.class));

    injectedVoiceOverPresenter.addVoiceOver("somePath");

    verify(mockedVoiceOverView).navigateToSoundVolumeActivity(anyString());

  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

  private VoiceOverPresenter getInjectedPresenter(MergeVoiceOverAudiosUseCase mergeVoiceOverUseCase){
    return new VoiceOverPresenter(mockedVoiceOverView, mockedGetMediaListFromProjectUseCase,
        mockedGetPreferencesTransitionFromProjectUseCase, mergeVoiceOverUseCase);
  }
}
