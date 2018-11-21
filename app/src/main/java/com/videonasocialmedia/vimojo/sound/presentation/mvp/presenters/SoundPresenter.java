package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.FutureCallback;
import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.asset.domain.usecase.RemoveMedia;
import com.videonasocialmedia.vimojo.composition.domain.RemoveTrack;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateTrack;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicListActivity;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import static com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundPresenter extends VimojoPresenter implements VideoTranscodingErrorNotifier,
    ElementChangedListener {
  private String LOG_TAG = getClass().getSimpleName();
  private Context context;
  private SoundView soundView;
  private UserEventTracker userEventTracker;
  private ModifyTrackUseCase modifyTrackUseCase;
  private final ProjectInstanceCache projectInstanceCache;
  private static final float VOLUME_MUTE = 0f;
  protected Project currentProject;
  private UpdateComposition updateComposition;
  protected boolean voiceOverAvailable;
  private RemoveAudioUseCase removeAudioUseCase;
  private RemoveMedia removeMedia;
  private UpdateTrack updateTrack;
  private RemoveTrack removeTrack;

  @Inject
  public SoundPresenter(
      Context context, SoundView soundView, ModifyTrackUseCase modifyTrackUseCase,
      ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
      RemoveAudioUseCase removeAudioUseCase, RemoveMedia removeMedia, UpdateTrack updateTrack,
      RemoveTrack removeTrack, @Named("voiceOverAvailable") boolean voiceOverAvailable,
      BackgroundExecutor backgroundExecutor, UserEventTracker userEventTracker) {
    super(backgroundExecutor, userEventTracker);
    this.context = context;
    this.soundView = soundView;
    this.userEventTracker = userEventTracker;
    this.projectInstanceCache = projectInstanceCache;
    this.modifyTrackUseCase = modifyTrackUseCase;
    this.updateComposition = updateComposition;
    this.removeAudioUseCase = removeAudioUseCase;
    this.removeMedia = removeMedia;
    this.updateTrack = updateTrack;
    this.removeTrack = removeTrack;
    this.voiceOverAvailable = voiceOverAvailable;

  }

    public void updatePresenter() {
      this.currentProject = projectInstanceCache.getCurrentProject();
      this.currentProject.addListener(this);
      checkVoiceOverFeatureToggle();
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

  protected void checkVoiceOverFeatureToggle() {
    if (voiceOverAvailable) {
      soundView.addVoiceOverOptionToToolbar();
    } else {
      soundView.hideVoiceOverTrack();
    }
  }

  public void setTrackVolume(int id, int seekBarProgress) {
    Track track = getTrackById(id);
    float volume = (float) (seekBarProgress * 0.01);
    modifyTrackUseCase.setTrackVolume(track, volume);
    updatePlayerVolume(id, volume);
    executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
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
    switch (id) {
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

  public void setTrackMute(int id, boolean isMute) {
    Track track = getTrackById(id);
    modifyTrackUseCase.setTrackMute(track, isMute);
    updatePlayerMute(id, isMute);
    executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
  }

  private void updatePlayerMute(int id, boolean isMute) {
    switch (id) {
      case Constants.INDEX_MEDIA_TRACK:
        if (isMute) {
          soundView.setVideoVolume(VOLUME_MUTE);
        } else {
          soundView.setVideoVolume(getTrackById(id).getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        if (isMute) {
          soundView.setMusicVolume(VOLUME_MUTE);
        } else {
          soundView.setMusicVolume(getTrackById(id).getVolume());
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if (isMute) {
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
    switch (trackId) {
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

  public void deleteMusic() {
    removeItem(currentProject.getMusic());
  }

  public void deleteVoiceOver() {
    removeItem(currentProject.getVoiceOver());
  }

  private void removeItem(final Music music) {
    removeAudioUseCase.removeMusic(currentProject, music,
        new OnRemoveMediaFinishedListener() {
          @Override
          public void onRemoveMediaItemFromTrackSuccess(List<Media> removedMedias) {
            addCallback(
                executeUseCaseCall(() -> {
                  removeMedia.removeMedias(removedMedias);
                  updateComposition.updateComposition(currentProject);
                }),
                new FutureCallback<Object>() {
                  @Override
                  public void onSuccess(@Nullable Object result) {
                    Log.d(LOG_TAG, "onRemoveMediaItemFromTrackSuccess onSuccess");
                    if(music.getTitle().equals(MUSIC_AUDIO_VOICEOVER_TITLE)) {
                      userEventTracker.trackVoiceOverRemoved(currentProject);
                    } else {
                      userEventTracker.trackMusicRemoved(currentProject);
                    }
                    soundView.updateAudioTracks();
                    soundView.resetPlayer();
                    updatePresenter();
                  }
                  @Override
                  public void onFailure(Throwable t) {
                    Log.d(LOG_TAG, "onRemoveMediaItemFromTrackSuccess onFailure");
                    Log.e(LOG_TAG, "Error updating composition on removeMusic.success", t);
                    Crashlytics.log("Error updating composition on removeMusic.success " + t);
                  }
                });
          }
          @Override
          public void onRemoveMediaItemFromTrackError() {
            Log.d(LOG_TAG, "onRemoveMediaItemFromTrackError");
            Crashlytics.log("Problem removing music in project, " +
                "onRemoveMediaItemFromTrackError");
            soundView.showError(context
                .getString(R.string.error_removing_audio));
          }

          @Override
          public void onTrackUpdated(Track track) {
            Log.d(LOG_TAG, "onTrackUpdated");
            updateTrack.update(track);
          }

          @Override
          public void onTrackRemoved(Track track) {
            Log.d(LOG_TAG, "onTrackRemoved");
            removeTrack.remove(track);
          }
        });
  }
}
