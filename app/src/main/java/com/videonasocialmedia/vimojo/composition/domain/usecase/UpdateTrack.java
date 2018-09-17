package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by jliarte on 14/09/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.composition.repository.TrackRepository;

import javax.inject.Inject;

/**
 * Use Case for updating an existing {@link Track} into repository.
 */
public class UpdateTrack {

  private TrackRepository trackRepository;

  @Inject
  public UpdateTrack(TrackRepository trackRepository) {
    this.trackRepository = trackRepository;
  }

  public void update(Track track) {
    trackRepository.update(track);
  }
}
