package com.videonasocialmedia.vimojo.repository.music;

import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 12/04/17.
 */

public class MusicRealmRepositoryTest {

  @Test
  public void testMusicRealmRepositoryConstructorSetsMappers() {
    MusicRealmRepository repo = new MusicRealmRepository();

    assertThat(repo.toMusicMapper, notNullValue());
    assertThat(repo.toRealmMusicMapper, notNullValue());
  }
}
