package com.videonasocialmedia.vimojo.asset.repository.datasource;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.RealmProject;

import java.util.List;

/**
 * Created by Alejandro on 21/10/16.
 */

public interface VideoDataSource extends DataSource<Video> {
  void update(Video item, RealmProject realmProject);

  List<Video> getAllVideos();

  void removeAllVideos();

}
