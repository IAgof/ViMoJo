package com.videonasocialmedia.vimojo.repository.track.datasource.mapper;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.track.datasource.RealmTrack;

/**
 * Created by alvaro on 10/04/17.
 */

public class RealmTrackToTrackMapper implements Mapper<RealmTrack,Track> {
  @Override
  public Track map(RealmTrack realmTrack) {
    Track track = null;
    if (realmTrack.id == Constants.INDEX_MEDIA_TRACK) {
      track = new MediaTrack(realmTrack.id, realmTrack.volume, realmTrack.mute, realmTrack.position);
    } else {
      track = new AudioTrack(realmTrack.id, realmTrack.volume, realmTrack.mute, realmTrack.position);
    }
    track.setUuid(realmTrack.uuid);
    return track;
  }
}
