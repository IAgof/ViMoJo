package com.videonasocialmedia.vimojo.cameraSettings.repository;

import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class CameraPrefRealmRepositoryTest {

  @Test
  public void cameraPrefRealmRepositoryConstructorSetsMappers() {
    CameraPrefRealmRepository repository = new CameraPrefRealmRepository();

    assertThat(repository.toCameraPreferencesMapper, notNullValue());
    assertThat(repository.toRealmCameraMapper, notNullValue());
  }
}
