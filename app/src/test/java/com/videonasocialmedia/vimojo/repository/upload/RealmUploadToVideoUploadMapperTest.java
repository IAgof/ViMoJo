/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload;

import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
/**
 * Created by alvaro on 6/6/18.
 */

public class RealmUploadToVideoUploadMapperTest {
  @Test
  public void testMapReturnsVideoUploadWithFieldsMapped() {
    RealmUpload realmUpload = new RealmUpload("randomUUID",1234, "/some/path",
        "title","description", "interviews, live on tape",
        1, false, false);

    RealmUploadToUploadMapper mapper = new RealmUploadToUploadMapper();

    VideoUpload videoUpload = mapper.map(realmUpload);

    assertThat(videoUpload.getUuid(), is("randomUUID"));
    assertThat(videoUpload.getId(), is(1234));
    assertThat(videoUpload.getMediaPath(), is("/some/path"));
    assertThat(videoUpload.getTitle(), is("title"));
    assertThat(videoUpload.getDescription(), is("description"));
    assertThat(videoUpload.getProductTypeList(), is("interviews, live on tape"));
    assertThat(videoUpload.getNumTries(), is(0));
    assertThat(videoUpload.isAcceptedUploadMobileNetwork(), is(false));
    assertThat(videoUpload.isUploading(), is(false));
  }
}
