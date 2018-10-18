package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.RemoveTrack;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateTrack;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverVolumeView;
import com.videonasocialmedia.vimojo.utils.ConstantsTest;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
public class VoiceOverVolumePresenterTest {

  @Mock VoiceOverVolumeView mockedVoiceOverVolumeView;
  @Mock ModifyTrackUseCase mockedModifyTrackUseCase;
  @Mock Context mockedContext;
  @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
  @Mock GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionFromPRojectUseCase;
  @Mock GetAudioFromProjectUseCase mockedGetAudioFromProjectUseCase;
  @Mock RemoveAudioUseCase mockedRemoveAudioUseCase;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock UpdateComposition mockedUpdateComposition;

  private Project currentProject;
  private boolean amIAVerticalApp;
  @Mock UpdateTrack mockedUpdateTrack;
  @Mock RemoveTrack mockedRemoveTrack;
  @Mock BackgroundExecutor mockedBackgroundExecutor;
  @Mock UserEventTracker mockedUserEventTracker;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
    setAProject();
  }

  @Test
  public void setVolumeCallsNavigateToSoundTrackingAndUpdateProject()
      throws IllegalItemOnTrack, InterruptedException {
    float volume = 0.7f;
    int defaultDuration = 100;
    String mediaPath = "somePath";
    Music voiceOver = new Music(mediaPath, volume, defaultDuration);
    currentProject.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    AudioTrack voiceOverTrack = currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);
    VoiceOverVolumePresenter voiceOverVolumePresenter = getVoiceOverVolumePresenter();
    when(mockedBackgroundExecutor.submit(any(Runnable.class))).then((Answer<Runnable>) invocation -> {
      Runnable runnable = invocation.getArgument(0);
      runnable.run();
      return null;
    });

    voiceOverVolumePresenter.setVoiceOverVolume(volume);

    verify(mockedVoiceOverVolumeView).goToSoundActivity();
    verify(mockedModifyTrackUseCase).setTrackVolume(currentProject.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER), volume);
    verify(mockedUpdateComposition).updateComposition(currentProject);
  }

  private void setAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }

  private VoiceOverVolumePresenter getVoiceOverVolumePresenter() {
    VoiceOverVolumePresenter voiceOverVolumePresenter = new VoiceOverVolumePresenter(mockedContext,
        mockedVoiceOverVolumeView,
        mockedGetMediaListFromProjectUseCase, mockedGetPreferencesTransitionFromPRojectUseCase,
        mockedGetAudioFromProjectUseCase, mockedModifyTrackUseCase, mockedRemoveAudioUseCase,
        mockedProjectInstanceCache, mockedUpdateComposition, amIAVerticalApp, mockedUpdateTrack,
        mockedRemoveTrack, mockedBackgroundExecutor, mockedUserEventTracker);
    voiceOverVolumePresenter.currentProject = currentProject;
    return voiceOverVolumePresenter;
  }

}