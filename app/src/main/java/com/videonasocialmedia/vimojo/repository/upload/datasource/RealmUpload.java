/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload.datasource;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alvaro on 6/6/18.
 *
 * Realm class for video upload to platform
 * Needed upload video file, title, description and productType
 */

public class RealmUpload extends RealmObject {
  @PrimaryKey @Required
  public String uuid;
  public int id;
  @Required
  public String mediaPath;
  @Required
  public String title;
  @Required
  public String description;
  @Required
  public String productTypeList;
  public int numTries;
  public boolean isAcceptedUploadWithMobileNetwork;
  public boolean isUploading;

  public RealmUpload() {
  }

  public RealmUpload(String uuid, int id, String mediaPath, String title, String description,
                     String productTypeList, int numTries,
                     boolean isAcceptedUploadWithMobileNetwork, boolean isUploading) {
    this.uuid = uuid;
    this.id = id;
    this.mediaPath = mediaPath;
    this.title = title;
    this.description = description;
    this.productTypeList = productTypeList;
    this.numTries = numTries;
    this.isAcceptedUploadWithMobileNetwork = isAcceptedUploadWithMobileNetwork;
    this.isUploading = isUploading;
  }

}
