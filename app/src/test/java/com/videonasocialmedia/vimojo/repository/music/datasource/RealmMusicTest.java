package com.videonasocialmedia.vimojo.repository.music.datasource;

import com.videonasocialmedia.vimojo.repository.music.datasource.RealmMusic;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import io.realm.RealmObject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 12/04/17.
 */

public class RealmMusicTest {

  @Test
  public void testRealmMusicExtendsRealmObject() {
    RealmMusic realmTrack = new RealmMusic();
    assertThat(realmTrack, CoreMatchers.instanceOf(RealmObject.class));
  }


  @Test
  public void testRealmMusicFields() {
    RealmMusic realmMusic = new RealmMusic();
    realmMusic.uuid = "qwerty";
    realmMusic.musicPath = "dcim/somePath";
    realmMusic.title = "title song";
    realmMusic.author = "author song";
    realmMusic.iconResourceId = 1;
    realmMusic.duration = 45;
    realmMusic.volume = 0.55f;

    assertThat(realmMusic.uuid, is("qwerty"));
    assertThat(realmMusic.musicPath, is("dcim/somePath"));
    assertThat(realmMusic.title, is("title song"));
    assertThat(realmMusic.author, is("author song"));
    assertThat(realmMusic.iconResourceId, is(1));
    assertThat(realmMusic.duration, is(45));
    assertThat(realmMusic.volume, is(0.55f));

  }

}
