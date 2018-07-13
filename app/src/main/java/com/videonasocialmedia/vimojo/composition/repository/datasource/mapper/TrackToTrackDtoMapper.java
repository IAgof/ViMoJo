package com.videonasocialmedia.vimojo.composition.repository.datasource.mapper;

/**
 * Created by jliarte on 12/07/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.TrackDto;

import javax.inject.Inject;

/**
 * Class to provide model conversions between {@link Track} and {@link TrackDto}
 */
class TrackToTrackDtoMapper extends KarumiMapper<Track, TrackDto> {
  @Inject public TrackToTrackDtoMapper() {
  }

  @Override
  public TrackDto map(Track track) {
    TrackDto trackDto = new TrackDto();
    trackDto.id = track.getUuid();
    trackDto.uuid = track.getUuid();
    trackDto.trackId = track.getId();
    trackDto.volume = track.getVolume();
    trackDto.mute = track.isMuted();
    trackDto.position = track.getPosition();
    return trackDto;
  }

  @Override
  public Track reverseMap(TrackDto trackDto) {
    return null;
  }
}
