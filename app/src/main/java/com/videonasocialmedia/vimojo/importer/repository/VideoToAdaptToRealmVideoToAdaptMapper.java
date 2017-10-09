package com.videonasocialmedia.vimojo.importer.repository;

import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by jliarte on 24/07/17.
 */

class VideoToAdaptToRealmVideoToAdaptMapper implements Mapper<VideoToAdapt,RealmVideoToAdapt> {
  @Override
  public RealmVideoToAdapt map(VideoToAdapt videoToAdapt) {
    return new RealmVideoToAdapt(
            videoToAdapt.getPosition(), videoToAdapt.getVideo().getUuid(),
            videoToAdapt.getVideo().getMediaPath(), videoToAdapt.getRotation(),
            videoToAdapt.getDestVideoPath(), videoToAdapt.numTriesAdaptingVideo);
  }
}
