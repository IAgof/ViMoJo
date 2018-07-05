package com.videonasocialmedia.vimojo.importer.repository;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.repository.video.datasource.VideoUUIDSpecification;

import java.util.List;

/**
 * Created by jliarte on 24/07/17.
 */

class RealmVideoToAdaptToVideoToAdaptMapper implements Mapper<RealmVideoToAdapt, VideoToAdapt> {

  private final VideoRepository videoRepository;

  public RealmVideoToAdaptToVideoToAdaptMapper(VideoRepository videoRepository) {
    this.videoRepository = videoRepository;
  }

  @Override
  public VideoToAdapt map(RealmVideoToAdapt realmVideoToAdapt) {
    if (realmVideoToAdapt == null) {
      return null;
    }
    List<Video> retrievedVideos = videoRepository
            .query(new VideoUUIDSpecification(realmVideoToAdapt.getVideo_uuid()));
    Video video = retrievedVideos.size() > 0 ? retrievedVideos.get(0) : null;
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, realmVideoToAdapt.getDestVideoPath(),
            realmVideoToAdapt.getPosition(), realmVideoToAdapt.getRotation(),
            realmVideoToAdapt.getNumTriesAdaptingVideo());
    return videoToAdapt;
  }
}
