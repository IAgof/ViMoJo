/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload.datasource.mapper;

import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.upload.datasource.RealmUpload;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

/**
 * Created by alvaro on 6/6/18.
 */

public class VideoUploadToRealmUploadMapper implements Mapper<VideoUpload,RealmUpload> {
  @Override
  public RealmUpload map(VideoUpload videoUpload) {
    RealmUpload realmUpload = new RealmUpload(videoUpload.getUuid(), videoUpload.getId(),
        videoUpload.getMediaPath(), videoUpload.getTitle(), videoUpload.getDescription(),
        videoUpload.getProductTypeList(), videoUpload.getNumTries(),
        videoUpload.isAcceptedUploadMobileNetwork(),videoUpload.isUploading());
    return realmUpload;
  }
}
