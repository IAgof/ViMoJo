package com.videonasocialmedia.vimojo.repository.track.datasource.mapper;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.composition.repository.datasource.RealmTrack;

/**
 * Created by alvaro on 10/04/17.
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
