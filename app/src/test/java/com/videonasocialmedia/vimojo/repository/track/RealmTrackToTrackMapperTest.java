package com.videonasocialmedia.vimojo.repository.track;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 11/04/17.
 */

public class RealmTrackToTrackMapperTest {
  @Test
  public void testMapReturnsTrackObject() {
    RealmTrack realmTrack = new RealmTrack();
    RealmTrackToTrackMapper mapper = new RealmTrackToTrackMapper();

    Track track = mapper.map(realmTrack);

    assertThat(track, notNullValue());
  }

  @Test
  public void testMapReturnsTrackWithFieldsMapped() {
    RealmTrack realmTrack = new RealmTrack("asdfsdf", 1 , 0.55f, false, 1);
    RealmTrackToTrackMapper mapper = new RealmTrackToTrackMapper();

    Track track = mapper.map(realmTrack);

    assertThat(track.getUuid(), is("asdfsdf"));
    assertThat(track.getId(), is(1));
    assertThat(track.getVolume(), is(0.55f));
    assertThat(track.isMuted(), is(false));
    assertThat(track.getPosition(), is(1));
  }
}
