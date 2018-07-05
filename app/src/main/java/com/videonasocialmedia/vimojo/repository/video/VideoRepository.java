package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.Repository;
import com.videonasocialmedia.vimojo.repository.project.datasource.RealmProject;

import java.util.List;

/**
 * Created by Alejandro on 21/10/16.
 */

public interface VideoRepository extends Repository<Video>{
  void update(Video item, RealmProject realmProject);

  List<Video> getAllVideos();

  void removeAllVideos();

  void setSuccessTranscodingVideo(Video video);

  void setErrorTranscodingVideo(Video video, String cause);
}
