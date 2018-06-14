/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload;

import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

/**
 * Created by alvaro on 6/6/18.
 */

public class RealmUploadToUploadMapper implements Mapper<RealmUpload,VideoUpload> {
  @Override
  public VideoUpload map(RealmUpload realmUpload) {
    VideoUpload videoUpload = new VideoUpload(realmUpload.id, realmUpload.mediaPath,
        realmUpload.title, realmUpload.description, realmUpload.productTypeList,
        realmUpload.isAcceptedUploadWithMobileNetwork, realmUpload.isUploading);
    videoUpload.setUuid(realmUpload.uuid);
    return videoUpload;
  }
}
