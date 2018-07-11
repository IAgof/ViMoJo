/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload;

import com.videonasocialmedia.vimojo.repository.datasource.DataSource;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.util.List;

/**
 * Created by alvaro on 6/6/18.
 */

public interface UploadDataSource extends DataSource<VideoUpload> {
  List<VideoUpload> getAllVideosToUpload();

  VideoUpload getVideoToUploadByUUID(String uuid);

  void removeAllVideosToUpload();
}
