package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverVolumeView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;


/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
public class VoiceOverVolumePresenterTest {

  @InjectMocks  VoiceOverVolumePresenter injectedPresenter;

  @Mock VoiceOverVolumeView mockedVoiceOverVolumeView;
  @Mock ModifyTrackUseCase mockedModifyTrackUseCase;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void setVolumeCallsModifyTrackUseCase() throws IllegalItemOnTrack {
    float volume = 0.7f;
    Project project = getAProject();
    int defaultDuration = 100;
    String mediaPath = "somePath";
    Music voiceOver = new Music(mediaPath, volume, defaultDuration);
    project.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    AudioTrack voiceOverTrack = project.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);

    injectedPresenter.setVoiceOverVolume(volume);

    verify(mockedModifyTrackUseCase).setTrackVolume(project.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER), volume);
  }

  @Test
  public void setVolumeNavigateToSoundActivity() throws IllegalItemOnTrack {
    float volume = 0.7f;
    Project project = getAProject();
    int defaultDuration = 100;
    String mediaPath = "somePath";
    Music voiceOver = new Music(mediaPath, volume, defaultDuration);
    project.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    AudioTrack voiceOverTrack = project.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    voiceOverTrack.insertItem(voiceOver);

    injectedPresenter.setVoiceOverVolume(volume);

    verify(mockedVoiceOverVolumeView).goToSoundActivity();
  }

  public Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", "private/path", compositionProfile);
  }

}