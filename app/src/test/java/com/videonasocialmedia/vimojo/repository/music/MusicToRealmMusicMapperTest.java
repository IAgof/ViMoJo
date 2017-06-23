package com.videonasocialmedia.vimojo.repository.music;

import com.videonasocialmedia.videonamediaframework.model.media.Music;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 12/04/17.
 */

public class MusicToRealmMusicMapperTest {

  @Test
  public void testMapReturnsARealmMusicInstance() {
    Music music = new Music("path", 0.55f, 45);
    MusicToRealmMusicMapper mapper = new MusicToRealmMusicMapper();
    RealmMusic realmMusic = mapper.map(music);
    assertThat(realmMusic, instanceOf(RealmMusic.class));
  }

  @Test
  public void testMapReturnsMusicObjectWithMappedFields() {
    Music music = new Music("path", 0.55f, 45);
    MusicToRealmMusicMapper mapper = new MusicToRealmMusicMapper();

    RealmMusic realmMusic = mapper.map(music);

    assertThat(realmMusic.uuid, is(music.getUuid()));
    assertThat(realmMusic.musicPath, is("path"));
    assertThat(realmMusic.title, is(music.getMusicTitle()));
    assertThat(realmMusic.author, is(music.getAuthor()));
    assertThat(realmMusic.iconResourceId, is(music.getIconResourceId()));
    assertThat(realmMusic.volume, is(0.55f));
    assertThat(realmMusic.duration, is(45));
  }
}
