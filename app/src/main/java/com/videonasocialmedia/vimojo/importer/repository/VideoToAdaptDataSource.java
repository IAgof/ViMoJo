package com.videonasocialmedia.vimojo.importer.repository;

import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;

import java.util.List;

/**
 * Created by jliarte on 24/07/17.
 */

public interface VideoToAdaptDataSource extends DataSource<VideoToAdapt> {
  int getItemCount();
  List<VideoToAdapt> getAllVideos();

  VideoToAdapt remove(String mediaPath);

  VideoToAdapt getByMediaPath(String mediaPath);
}
