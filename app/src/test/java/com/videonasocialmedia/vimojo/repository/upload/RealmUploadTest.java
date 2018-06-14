/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import io.realm.RealmObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by alvaro on 6/6/18.
 */

public class RealmUploadTest {

  @Test
  public void testRealmUploadExtendsRealmObject() {
    RealmUpload realmUpload = new RealmUpload();

    assertThat(realmUpload, CoreMatchers.instanceOf(RealmObject.class));
  }

  @Test
  public void testRealmUploadFields() {
    RealmUpload realmUpload = new RealmUpload();
    realmUpload.uuid = "qwerty1234";
    realmUpload.id = 1234;
    realmUpload.mediaPath = "/some/path";
    realmUpload.title = "title";
    realmUpload.description = "description";
    realmUpload.productTypeList = "interviews, live on tape";
    realmUpload.numTries = 1;
    realmUpload.isAcceptedUploadWithMobileNetwork = false;
    realmUpload.isUploading = false;

    assertThat(realmUpload.uuid, is("qwerty1234"));
    assertThat(realmUpload.id, is(1234));
    assertThat(realmUpload.mediaPath, is("/some/path"));
    assertThat(realmUpload.title, is("title"));
    assertThat(realmUpload.description, is("description"));
    assertThat(realmUpload.productTypeList, is("interviews, live on tape"));
    assertThat(realmUpload.numTries, is(1));
    assertThat(realmUpload.isAcceptedUploadWithMobileNetwork, is(false));
    assertThat(realmUpload.isUploading, is(false));
  }

  @Test
  public void testRealmUploadHasIdentifierPrimaryKey() {
    RealmUpload realmUpload = new RealmUpload();
    realmUpload.uuid = "randomUUID";

    assertThat(realmUpload.uuid, is("randomUUID"));
  }

}
