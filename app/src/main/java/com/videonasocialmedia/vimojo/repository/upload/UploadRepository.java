/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload;

import com.videonasocialmedia.vimojo.repository.Repository;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.util.List;

/**
 * Created by alvaro on 6/6/18.
 */

public interface UploadRepository extends Repository<VideoUpload>{
  List<VideoUpload> getAllVideosToUpload();

  VideoUpload getVideoToUploadByUUID(String uuid);
}
