package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

/**
 * Created by alvaro on 10/04/17.
 */

public class UpdateVideoTrackProjectUseCase {
  TrackRepository trackRepository;

  public UpdateVideoTrackProjectUseCase(TrackRepository trackRepository){
    this.trackRepository = trackRepository;
  }

  public void setVideoTrackVolume(MediaTrack track, float volume){
    track.setVolume(volume);
    trackRepository.update(track);
  }

  public void setVideoTrackMute(MediaTrack track, boolean isMute){
    track.setMute(isMute);
    trackRepository.update(track);
  }

  public void setVideoTrackSolo(MediaTrack track, boolean isSolo){
    track.setSolo(isSolo);
    trackRepository.update(track);
  }
}
