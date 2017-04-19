package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

/**
 * Created by alvaro on 10/04/17.
 */

public class UpdateAudioTrackProjectUseCase {

  public static final int SECOND_POSITION = 2;
  public static final int FIRST_POSITION = 1;
  protected TrackRepository trackRepository;
  protected Project currentProject;

  public UpdateAudioTrackProjectUseCase(TrackRepository trackRepository){
    this.trackRepository = trackRepository;
    this.currentProject = Project.getInstance(null, null, null);
  }

  public void setAudioTrackVolume(AudioTrack track, float volume){
    track.setVolume(volume);
    trackRepository.update(track);
  }

  public void setAudioTrackMute(AudioTrack track, boolean isMute){
    track.setMute(isMute);
    trackRepository.update(track);
  }

  public void setAudioTrackSolo(AudioTrack track, boolean isSolo){
    track.setSolo(isSolo);
    trackRepository.update(track);
  }

  public void addedNewTrack(int trackIndex){
    int position = areThereAnyAudioTrackAdded() ? SECOND_POSITION : FIRST_POSITION;
    AudioTrack audioTrack = currentProject.getAudioTracks().get(trackIndex);
    audioTrack.setPosition(position);
    trackRepository.update(audioTrack);
  }

  private boolean areThereAnyAudioTrackAdded() {
    return currentProject.hasMusic() || currentProject.hasVoiceOver();
  }

  public void removedTrack(int trackIndex) {
    if(areThereAnyAudioTrackAdded()){
      if(trackIndex == Constants.INDEX_AUDIO_TRACKS_MUSIC){
        updatePositionOtherAudioTrackAdded(Constants.INDEX_AUDIO_TRACKS_VOICE_OVER);
      } else {
        if(trackIndex == Constants.INDEX_AUDIO_TRACKS_VOICE_OVER){
          updatePositionOtherAudioTrackAdded(Constants.INDEX_AUDIO_TRACKS_MUSIC);
        }
      }
    }
  }

  private void updatePositionOtherAudioTrackAdded(int trackIndex) {
    AudioTrack audioTrack = currentProject.getAudioTracks().get(trackIndex);
    audioTrack.setPosition(FIRST_POSITION);
    trackRepository.update(audioTrack);
  }
}
