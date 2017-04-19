package com.videonasocialmedia.vimojo.repository.track;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by alvaro on 10/04/17.
 */

public class RealmTrackToTrackMapper implements Mapper<RealmTrack,Track> {
  @Override
  public Track map(RealmTrack realmTrack) {
    Track track = new Track(realmTrack.id, realmTrack.volume, realmTrack.mute, realmTrack.solo,
        realmTrack.position);
    track.setUuid(realmTrack.uuid);
    return track;
  }
}
