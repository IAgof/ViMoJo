package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

/**
 * Created by alvaro on 10/04/17.
 */

public class ModifyTrackUseCase {
  TrackRepository trackRepository;

  public ModifyTrackUseCase(TrackRepository trackRepository){
    this.trackRepository = trackRepository;
  }

  public void setTrackVolume(Track track, float volume){
    track.setVolume(volume);
    trackRepository.update(track);
  }

  public void setTrackMute(Track track, boolean isMute){
    track.setMute(isMute);
    trackRepository.update(track);
  }

}
