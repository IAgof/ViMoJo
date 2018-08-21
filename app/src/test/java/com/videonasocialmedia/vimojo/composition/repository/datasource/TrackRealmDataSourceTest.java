package com.videonasocialmedia.vimojo.composition.repository.datasource;

import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 11/04/17.
 */

public class TrackRealmDataSourceTest {
  @Test
  public void testTrackRealmRepositoryConstructorSetsMappers() {
    TrackRealmDataSource repo = new TrackRealmDataSource(videoDataSource, musicDataSource);

    assertThat(repo.toTrackMapper, notNullValue());
    assertThat(repo.toRealmTrackMapper, notNullValue());
  }
}