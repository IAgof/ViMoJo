package com.videonasocialmedia.vimojo.repository.music.datasource;

import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 12/04/17.
 */

public class MusicRealmDataSourceTest {

  @Test
  public void testMusicRealmRepositoryConstructorSetsMappers() {
    MusicRealmDataSource repo = new MusicRealmDataSource();

    assertThat(repo.toMusicMapper, notNullValue());
    assertThat(repo.toRealmMusicMapper, notNullValue());
  }
}
