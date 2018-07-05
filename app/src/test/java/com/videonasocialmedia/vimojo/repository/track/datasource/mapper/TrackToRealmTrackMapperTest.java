package com.videonasocialmedia.vimojo.repository.track.datasource.mapper;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.track.datasource.RealmTrack;
import com.videonasocialmedia.vimojo.repository.track.datasource.mapper.TrackToRealmTrackMapper;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 11/04/17.
 */

public class TrackToRealmTrackMapperTest {

  @Test
  public void testMapReturnsARealmTrackInstance() {
    Track track = new Track(0, 0.55f, false, 1);
    TrackToRealmTrackMapper mapper = new TrackToRealmTrackMapper();
    RealmTrack realmTrack = mapper.map(track);
    assertThat(realmTrack, instanceOf(RealmTrack.class));
  }

  @Test
  public void testMapReturnsTrackObjectWithMappedFields() {
    Track track = new Track(0, 0.55f, false, 1);
    TrackToRealmTrackMapper mapper = new TrackToRealmTrackMapper();

    RealmTrack realmTrack = mapper.map(track);

    assertThat(realmTrack.id, is(0));
    assertThat(realmTrack.volume, is(0.55f));
    assertThat(realmTrack.mute, is(false));
    assertThat(realmTrack.position, is(1));
  }
}
