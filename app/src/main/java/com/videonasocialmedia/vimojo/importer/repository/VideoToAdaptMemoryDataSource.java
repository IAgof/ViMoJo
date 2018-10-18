package com.videonasocialmedia.vimojo.importer.repository;

import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jliarte on 24/07/17.
 */

public class VideoToAdaptMemoryDataSource implements VideoToAdaptDataSource {
  private HashMap<String, VideoToAdapt> videoListToAdaptAndPosition = new HashMap<>();

  @Override
  public void add(VideoToAdapt item) {
    videoListToAdaptAndPosition.put(item.getVideo().getMediaPath(), item);
  }

  @Override
  public void add(Iterable<VideoToAdapt> items) {
    for (VideoToAdapt videoToAdapt : items) {
      add(videoToAdapt);
    }
  }

  @Override
  public void update(VideoToAdapt item) {
    videoListToAdaptAndPosition.put(item.getVideo().getMediaPath(), item);
  }

  @Override
  public void remove(VideoToAdapt item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<VideoToAdapt> query(Specification specification) {
    return null;
  }

  @Override
  public VideoToAdapt getById(String id) {
    return getByMediaPath(id);
  }

  @Override
  public int getItemCount() {
    return videoListToAdaptAndPosition.size();
  }

  @Override
  public List<VideoToAdapt> getAllVideos() {
    return new ArrayList<>(videoListToAdaptAndPosition.values());
  }

  @Override
  public VideoToAdapt remove(String mediaPath) {
    return videoListToAdaptAndPosition.remove(mediaPath);
  }

  @Override
  public VideoToAdapt getByMediaPath(String mediaPath) {
    return videoListToAdaptAndPosition.get(mediaPath);
  }
}
