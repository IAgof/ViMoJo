package com.videonasocialmedia.vimojo.composition.domain;

/**
 * Created by jliarte on 14/09/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.composition.repository.TrackRepository;

import javax.inject.Inject;

/**
 * Use Case for removing an existing {@link Track} from repository.
 */
public class RemoveTrack {

  private TrackRepository trackRepository;

  @Inject
  public RemoveTrack(TrackRepository trackRepository) {
    this.trackRepository = trackRepository;
  }

  public void remove(Track track) {
    trackRepository.remove(track);
  }
}
