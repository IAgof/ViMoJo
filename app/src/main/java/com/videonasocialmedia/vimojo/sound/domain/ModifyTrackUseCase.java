package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;

import javax.inject.Inject;

/**
 * Created by alvaro on 10/04/17.
 */

public class ModifyTrackUseCase {
  @Inject public ModifyTrackUseCase() {
  }

  public void setTrackVolume(Track track, float volume) {
    // TODO(jliarte): 21/08/18 review this for setting media volume individually
    for (Media item: track.getItems()) {
      item.setVolume(volume);
    }
    track.setVolume(volume);
  }

  public void setTrackMute(Track track, boolean isMuted) {
    track.setMute(isMuted);
  }

}
