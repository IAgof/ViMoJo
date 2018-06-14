/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload;

import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 6/6/18.
 */

public class VideoUploadToRealmUploadMapperTest {
  @Test
  public void testMapReturnsARealmVideoUploadInstance() {
    VideoUpload videoUpload = new VideoUpload(1234, "/some/path","title",
        "description", "interviews, live on tape",
        false, false);
    VideoUploadToRealmUploadMapper mapper = new VideoUploadToRealmUploadMapper();

    RealmUpload realmUpload = mapper.map(videoUpload);

    assertThat(realmUpload, instanceOf(RealmUpload.class));
  }

  @Test
  public void testMapReturnsVideoUploadObjectWithMappedFields() {
    VideoUpload videoUpload = new VideoUpload(1234, "/some/path","title",
        "description", "interviews, live on tape",
        false, false);
    videoUpload.setUuid("randomUUID");
    VideoUploadToRealmUploadMapper mapper = new VideoUploadToRealmUploadMapper();

    RealmUpload realmUpload = mapper.map(videoUpload);

    assertThat(realmUpload.uuid, Matchers.is("randomUUID"));
    assertThat(realmUpload.id, Matchers.is(1234));
    assertThat(realmUpload.mediaPath, Matchers.is("/some/path"));
    assertThat(realmUpload.title, Matchers.is("title"));
    assertThat(realmUpload.description, Matchers.is("description"));
    assertThat(realmUpload.productTypeList, Matchers.is("interviews, live on tape"));
    assertThat(realmUpload.numTries, Matchers.is(0));
    assertThat(realmUpload.isAcceptedUploadWithMobileNetwork, Matchers.is(false));
    assertThat(realmUpload.isUploading, Matchers.is(false));
  }
}
