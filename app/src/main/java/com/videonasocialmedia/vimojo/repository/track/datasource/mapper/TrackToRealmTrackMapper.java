package com.videonasocialmedia.vimojo.repository.track.datasource.mapper;

/**
 * Created by alvaro on 10/04/17.
 */

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.composition.repository.datasource.RealmTrack;

/**
 * Class to provide model conversions between {@link Track} and {@link RealmTrack}
 */
public class TrackToRealmTrackMapper implements Mapper<Track, RealmTrack>
{
  @Override
  public RealmTrack map(Track track) {
    RealmTrack realmTrack = new RealmTrack(track.getUuid(), track.getId(), track.getVolume(),
        track.isMuted(), track.getPosition());
    return realmTrack;
  }
}
