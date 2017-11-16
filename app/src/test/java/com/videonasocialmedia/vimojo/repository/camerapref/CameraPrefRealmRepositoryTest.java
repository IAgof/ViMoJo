package com.videonasocialmedia.vimojo.repository.camerapref;

import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class CameraPrefRealmRepositoryTest {

  @Test
  public void testMusicRealmRepositoryConstructorSetsMappers() {
    CameraPrefRealmRepository repository = new CameraPrefRealmRepository();

    assertThat(repository.toCameraPreferencesMapper, notNullValue());
    assertThat(repository.toRealmCameraMapper, notNullValue());
  }
}
