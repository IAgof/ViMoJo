package com.videonasocialmedia.vimojo.importer.repository;

import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.repository.Repository;

import java.util.List;

/**
 * Created by jliarte on 24/07/17.
 */

public interface VideoToAdaptRepository extends Repository<VideoToAdapt> {
  int getItemCount();
  List<VideoToAdapt> getAllVideos();

  VideoToAdapt remove(String mediaPath);

  VideoToAdapt getByMediaPath(String mediaPath);
}
