package com.videonasocialmedia.vimojo.repository.track.datasource;


import com.videonasocialmedia.vimojo.repository.track.datasource.RealmTrack;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import io.realm.RealmObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by alvaro on 11/04/17.
 */

public class RealmTrackTest {

  @Test
  public void testRealmTrackExtendsRealmObject() {
    RealmTrack realmTrack = new RealmTrack();
    assertThat(realmTrack, CoreMatchers.instanceOf(RealmObject.class));
  }

  @Test
  public void testRealmTrackFields() {
    RealmTrack realmTrack = new RealmTrack();
    realmTrack.uuid = "sdfkdfgñ";
    realmTrack.id = 0;
    realmTrack.volume = 0.55f;
    realmTrack.mute = true;

    assertThat(realmTrack.uuid, is("sdfkdfgñ"));
    assertThat(realmTrack.id, is(0));
    assertThat(realmTrack.volume, is(0.55f));
    assertThat(realmTrack.mute, is(true));
  }


}
