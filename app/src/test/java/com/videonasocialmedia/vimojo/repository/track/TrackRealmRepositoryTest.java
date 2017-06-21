package com.videonasocialmedia.vimojo.repository.track;

import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 11/04/17.
 */

public class TrackRealmRepositoryTest {

  @Test
  public void testTrackRealmRepositoryConstructorSetsMappers() {
    TrackRealmRepository repo = new TrackRealmRepository();

    assertThat(repo.toTrackMapper, notNullValue());
    assertThat(repo.toRealmTrackMapper, notNullValue());
  }
}
