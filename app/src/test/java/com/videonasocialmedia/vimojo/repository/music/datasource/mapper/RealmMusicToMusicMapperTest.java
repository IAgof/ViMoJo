package com.videonasocialmedia.vimojo.repository.music.datasource.mapper;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.repository.music.datasource.RealmMusic;
import com.videonasocialmedia.vimojo.repository.music.datasource.mapper.RealmMusicToMusicMapper;

import org.junit.Test;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 12/04/17.
 */

public class RealmMusicToMusicMapperTest {

  @Test
  public void testMapReturnsMusicObject() {
    RealmMusic realmMusic = new RealmMusic();
    RealmMusicToMusicMapper mapper = new RealmMusicToMusicMapper();

    Music music = mapper.map(realmMusic);

    assertThat(music, notNullValue());
  }

  @Test
  public void testMapReturnsMusicWithFieldsMapped() {
    RealmMusic realMusic = new RealmMusic("qwerty", "path", "title", "author", 1, 45, 0.85f);
    RealmMusicToMusicMapper mapper = new RealmMusicToMusicMapper();

    Music music = mapper.map(realMusic);

    assertThat(music.getUuid(), is("qwerty"));
    assertThat(music.getMediaPath(), is("path"));
    assertThat(music.getMusicTitle(), is("title"));
    assertThat(music.getAuthor(), is("author"));
    assertThat(music.getIconResourceId(), is(1));
    assertThat(music.getDuration(), is(45));
    assertThat(music.getVolume(), is(0.85f));
  }
}
