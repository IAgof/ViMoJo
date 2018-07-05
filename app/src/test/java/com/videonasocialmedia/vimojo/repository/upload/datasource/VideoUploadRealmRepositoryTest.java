/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload.datasource;

import com.videonasocialmedia.vimojo.repository.upload.datasource.UploadRealmRepository;

import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 6/6/18.
 */

public class VideoUploadRealmRepositoryTest {
  @Test
  public void testUploadRealmRepositoryConstructorSetsMappers() {
    UploadRealmRepository repo = new UploadRealmRepository();

    assertThat(repo.toRealmUploadMapper, notNullValue());
    assertThat(repo.toVideoUploadMapper, notNullValue());
  }
}
