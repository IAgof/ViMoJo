package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;

import java.util.Collections;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE;

/**
 * Created by alvaro on 1/06/17.
 */

public class RemoveAudioUseCase {
  private Project currentProject;
  private final int FIRST_POSITION = 1;
  private final int RESET_POSITION = 0;

  @Inject
  public RemoveAudioUseCase() {
  }

  // Remove audio only delete track if it is not music track.
  public void removeMusic(Project currentProject, Music music,
                          OnRemoveMediaFinishedListener listener) {
    this.currentProject = currentProject;
    int trackIndex;
    if (music.getTitle().equals(MUSIC_AUDIO_VOICEOVER_TITLE)){
      trackIndex = Constants.INDEX_AUDIO_TRACK_VOICE_OVER;
    } else {
      trackIndex = Constants.INDEX_AUDIO_TRACK_MUSIC;
    }
    Track audioTrack = currentProject.getAudioTracks().get(trackIndex);
    removeMusicInTrack(listener, audioTrack);
    if (trackIndex == Constants.INDEX_AUDIO_TRACK_VOICE_OVER) {
      removeEmptyAudioTrack(audioTrack, currentProject, listener);
    }
    updateOtherAudioTrackPosition(trackIndex, listener);
  }

  private void removeEmptyAudioTrack(Track audioTrack, Project currentProject,
                                     OnRemoveMediaFinishedListener listener) {
    currentProject.getAudioTracks().remove(audioTrack);
    listener.onTrackRemoved(audioTrack);
  }

  private void updateOtherAudioTrackPosition(int trackIndex,
                                             OnRemoveMediaFinishedListener listener) {
    Track musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    switch (trackIndex) {
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        // Reset default audio track music
        resetDefaultAudioTrackMusic(musicTrack);
        if (currentProject.hasVoiceOver()) {
          Track voiceOverTrack = currentProject.getAudioTracks()
              .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
          voiceOverTrack.setPosition(FIRST_POSITION);
          listener.onTrackUpdated(voiceOverTrack);
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if (currentProject.hasMusic()) {
          musicTrack.setPosition(FIRST_POSITION);
          listener.onTrackUpdated(musicTrack);
        }
        break;
    }
  }

  private void resetDefaultAudioTrackMusic(Track musicTrack) {
    musicTrack.setPosition(RESET_POSITION);
    musicTrack.setMute(false);
    musicTrack.setVolume(Music.DEFAULT_VOLUME);
  }

  private void removeMusicInTrack(OnRemoveMediaFinishedListener listener,
                                  Track audioTrack) {
    try {
      Media media = audioTrack.getItems().get(0);
      audioTrack.deleteItem(media);
      listener.onRemoveMediaItemFromTrackSuccess(Collections.singletonList(media));
    } catch (IllegalItemOnTrack | IllegalOrphanTransitionOnTrack exception) {
      listener.onRemoveMediaItemFromTrackError();
    }
  }

}
