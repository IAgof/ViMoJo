package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundPresenter extends VimojoPresenter implements VideoTranscodingErrorNotifier,
    ElementChangedListener {

  private String LOG_TAG = getClass().getSimpleName();
  private SoundView soundView;
  private ModifyTrackUseCase modifyTrackUseCase;
  private final ProjectInstanceCache projectInstanceCache;
  private static final float VOLUME_MUTE = 0f;
  protected Project currentProject;
  private CompositionApiClient compositionApiClient;

  @Inject
    public SoundPresenter(SoundView soundView, ModifyTrackUseCase modifyTrackUseCase,
                          ProjectInstanceCache projectInstanceCache, CompositionApiClient
                          compositionApiClient) {
        this.soundView = soundView;
        this.projectInstanceCache = projectInstanceCache;
        this.modifyTrackUseCase = modifyTrackUseCase;
        this.compositionApiClient = compositionApiClient;
    }

    public void updatePresenter() {
      this.currentProject = projectInstanceCache.getCurrentProject();
      this.currentProject.addListener(this);
      checkVoiceOverFeatureToggle(BuildConfig.FEATURE_VOICE_OVER);
      // TODO:(alvaro.martinez) 22/03/17 Player should be in charge of these checks from
      // VMComposition
      retrieveTracks();
    }

  private void retrieveTracks() {
    if (currentProject.getVMComposition().hasVideos()) {
      Track videoTrack = currentProject.getVMComposition().getMediaTrack();
      setupTrack(videoTrack);
      updateClipPlayed(Constants.INDEX_MEDIA_TRACK);
      soundView.showTrackVideo();
    }
    if (currentProject.getVMComposition().hasMusic()) {
      Track musicTrack = currentProject.getVMComposition().getAudioTracks()
          .get(Constants.INDEX_AUDIO_TRACK_MUSIC);
      setupTrack(musicTrack);
      updateClipPlayed(Constants.INDEX_AUDIO_TRACK_MUSIC);
      if (musicTrack.getPosition() == 1) {
        soundView.showTrackAudioFirst();
      } else {
        soundView.showTrackAudioSecond();
      }
    }
    if (currentProject.getVMComposition().hasVoiceOver()) {
      Track voiceOverTrack = currentProject.getVMComposition().getAudioTracks()
          .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
      setupTrack(voiceOverTrack);
      updateClipPlayed(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
      if (voiceOverTrack.getPosition()==1) {
        soundView.showTrackAudioFirst();
      } else {
        soundView.showTrackAudioSecond();
      }
    }
  }

  private void setupTrack(Track track) {
    soundView.bindTrack(track);
    updatePlayerMute(track.getId(), track.isMuted());
  }

  protected void checkVoiceOverFeatureToggle(boolean featureVoiceOver) {
    if(featureVoiceOver){
      soundView.addVoiceOverOptionToFab();
    } else {
      soundView.hideVoiceOverCardView();
    }
  }

  public void setTrackVolume(int id, int seekBarProgress){
    Track track = getTrackById(id);
    float volume = (float) (seekBarProgress * 0.01);
    modifyTrackUseCase.setTrackVolume(currentProject, track, volume);
    updatePlayerVolume(id, volume);
    updateCompositionWithPlatform(currentProject);
  }

  private void updatePlayerVolume(int id, float volume) {
    switch (id) {
      case Constants.INDEX_MEDIA_TRACK:
        soundView.setVideoVolume(volume);
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        soundView.setMusicVolume(volume);
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        soundView.setVoiceOverVolume(volume);
        break;
    }
  }

  private Track getTrackById(int id) {
    switch (id){
      case Constants.INDEX_MEDIA_TRACK:
        return currentProject.getVMComposition().getMediaTrack();
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        return currentProject.getVMComposition().getAudioTracks().get(id);
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        return currentProject.getVMComposition().getAudioTracks().get(id);
      default:
        return null;
    }
  }

  public void setTrackMute(int id, boolean isMute){
    Track track = getTrackById(id);
    modifyTrackUseCase.setTrackMute(currentProject, track, isMute);
    updatePlayerMute(id, isMute);
    updateCompositionWithPlatform(currentProject);
  }

  private void updateCompositionWithPlatform(Project currentProject) {
    ListenableFuture<Project> compositionFuture = executeUseCaseCall(new Callable<Project>() {
      @Override
      public Project call() throws Exception {
        return compositionApiClient.addComposition(currentProject);
      }
    });
    Futures.addCallback(compositionFuture, new FutureCallback<Project>() {
      @Override
      public void onSuccess(@Nullable Project result) {
        Log.d(LOG_TAG, "Success uploading composition to server ");
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(LOG_TAG, "Error uploading composition to server " + t.getMessage());
      }
    });
  }
  private void updatePlayerMute(int id, boolean isMute) {
    switch (id) {
      case Constants.INDEX_MEDIA_TRACK:
        if(isMute){
          soundView.setVideoVolume(VOLUME_MUTE);
        } else {
          soundView.setVideoVolume(getTrackById(id).getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        if(isMute){
          soundView.setMusicVolume(VOLUME_MUTE);
        } else {
          soundView.setMusicVolume(getTrackById(id).getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if(isMute){
          soundView.setVoiceOverVolume(VOLUME_MUTE);
        } else {
          soundView.setVoiceOverVolume(getTrackById(id).getVolume());
        }
        break;
    }
  }

  @Override
  public void showWarningTempFile(ArrayList<Video> failedVideos) {
    // TODO(jliarte): 23/07/17 should modify view with failed clips?
    soundView.showWarningTempFile();
  }

  @Override
  public void setWarningMessageTempFile(String messageTempFile) {
    soundView.setWarningMessageTempFile(messageTempFile);
  }

  public void updateClipPlayed(int trackId) {
    Track track = getTrackById(trackId);
    switch (trackId){
      case Constants.INDEX_MEDIA_TRACK:
        if (track.isMuted()) {
          soundView.setVideoVolume(VOLUME_MUTE);
        } else {
          soundView.setVideoVolume(track.getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        if (track.isMuted()) {
          soundView.setMusicVolume(VOLUME_MUTE);
        } else {
          soundView.setMusicVolume(track.getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if (track.isMuted()) {
          soundView.setVoiceOverVolume(VOLUME_MUTE);
        } else {
          soundView.setVoiceOverVolume(track.getVolume());
        }
        break;
    }

  }

  @Override
  public void onObjectUpdated() {
    soundView.updatePlayer();
  }
}
