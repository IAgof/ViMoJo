package com.videonasocialmedia.vimojo.composition.repository.datasource.mapper;

/**
 * Created by jliarte on 12/07/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.MediaToMediaDtoMapper;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.TrackDto;

import javax.inject.Inject;

/**
 * Class to provide model conversions between {@link Track} and {@link TrackDto}
 */
class TrackToTrackDtoMapper extends KarumiMapper<Track, TrackDto> {
  // TODO(jliarte): 13/07/18 maybe inject this?
  private MediaToMediaDtoMapper mediaToMediaDtoMapper = new MediaToMediaDtoMapper();

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
    if (track.getItems().size() > 0) {
      for (Media item : track.getItems()) {
        trackDto.mediaItems.add(mediaToMediaDtoMapper.map(item));
      }
    }
    return trackDto;
  }

  @Override
  public Track reverseMap(TrackDto trackDto) {
    return null;
  }
}
