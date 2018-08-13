package com.videonasocialmedia.vimojo.composition.repository.datasource.mapper;

/**
 * Created by jliarte on 12/07/18.
 */

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.MediaToMediaDtoMapper;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;
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
    trackDto.trackIndex = track.getId();
    trackDto.volume = track.getVolume();
    trackDto.muted = track.isMuted();
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
    Track track;
    if (trackDto.getTrackIndex() == Constants.INDEX_MEDIA_TRACK) {
      track = new MediaTrack(trackDto.getTrackIndex(), trackDto.getVolume(), trackDto.isMuted(),
              trackDto.getPosition());
    } else {
      track = new AudioTrack(trackDto.getTrackIndex(), trackDto.getVolume(), trackDto.isMuted(),
              trackDto.getPosition());
    }
    track.setUuid(trackDto.getId());
    mapTrackDtoMedias(trackDto, track);
    return track;
  }

  private void mapTrackDtoMedias(TrackDto trackDto, Track track) {
    if (trackDto.getMediaItems().size() > 0) {
      for (MediaDto mediaDto : trackDto.getMediaItems())
        try {
          track.insertItem(mediaToMediaDtoMapper.reverseMap(mediaDto));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
          // TODO(jliarte): 7/08/18 check this occurrence
          illegalItemOnTrack.printStackTrace();
        }
    }
  }
}
