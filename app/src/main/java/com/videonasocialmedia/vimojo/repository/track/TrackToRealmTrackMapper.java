package com.videonasocialmedia.vimojo.repository.track;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by alvaro on 10/04/17.
 */

public class TrackToRealmTrackMapper implements Mapper<Track, RealmTrack>
{
  @Override
  public RealmTrack map(Track track) {
    RealmTrack realmTrack = new RealmTrack(track.getUuid(), track.getId(), track.getVolume(),
        track.isMute(), track.isSolo(), track.getPosition());
    return realmTrack;
  }
}
