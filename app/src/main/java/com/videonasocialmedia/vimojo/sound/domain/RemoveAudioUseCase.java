package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackDataSource;

import java.util.Collections;

import javax.inject.Inject;

/**
 * Created by alvaro on 1/06/17.
 */

public class RemoveAudioUseCase {
  private Project currentProject;
  private TrackDataSource trackRepository;
  private final int FIRST_POSITION = 1;
  private final int RESET_POSITION = 0;

  @Inject
  public RemoveAudioUseCase(TrackDataSource trackRepository) {
    this.trackRepository = trackRepository;
  }

  // Remove audio only delete track if it is not music track.
  public void removeMusic(Project currentProject, Music music, int trackIndex,
                          OnRemoveMediaFinishedListener listener) {
    this.currentProject = currentProject;
    Track audioTrack = currentProject.getAudioTracks().get(trackIndex);
    updateTrackPosition(audioTrack, trackIndex);
    removeMusicInTrack(music, listener, audioTrack);
    updateTrackDefaultValues(audioTrack);
    if (audioTrack.getItems().size() == 0
        && audioTrack.getId() == Constants.INDEX_AUDIO_TRACK_VOICE_OVER) {
      currentProject.getAudioTracks().remove(audioTrack);
    }
  }

  // TODO:(alvaro.martinez) 19/06/17 delete this when vimojo support multiple audio items.
  private void updateTrackDefaultValues(Track audioTrack) {
    audioTrack.setVolume(Music.DEFAULT_VOLUME);
    audioTrack.setMute(false);
  }

  private void updateTrackPosition(Track audioTrack, int trackIndex) {
    audioTrack.setPosition(RESET_POSITION);
    trackRepository.update(audioTrack);
    switch (trackIndex) {
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        if (currentProject.hasVoiceOver()) {
          Track voiceOverTrack = currentProject.getAudioTracks()
              .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
          voiceOverTrack.setPosition(FIRST_POSITION);
          trackRepository.update(voiceOverTrack);
        }
        break;
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if (currentProject.hasMusic()) {
          Track musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
          musicTrack.setPosition(FIRST_POSITION);
          trackRepository.update(musicTrack);
        }
        break;
    }
  }

  private void removeMusicInTrack(Music music, OnRemoveMediaFinishedListener listener,
                                  Track audioTrack) {
    for (Media audio: audioTrack.getItems()) {
      if (audio.equals(music)) {
        try {
          audioTrack.deleteItem(audio);
          listener.onRemoveMediaItemFromTrackSuccess(Collections.singletonList(audio));
        } catch (IllegalItemOnTrack | IllegalOrphanTransitionOnTrack exception) {
          listener.onRemoveMediaItemFromTrackError();
        }
      }
    }
  }

}
