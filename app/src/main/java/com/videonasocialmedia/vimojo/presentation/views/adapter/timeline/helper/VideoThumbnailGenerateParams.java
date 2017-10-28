package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.helper;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by jliarte on 27/10/17.
 */
public class VideoThumbnailGenerateParams {
  public final Video video;

  public VideoThumbnailGenerateParams(Video currentVideo) {
    this.video = currentVideo;
  }

  public String getId() {
    // TODO make sure it's unique for every possible instance of GenerateParams
    // because it will affect how the resulting bitmap is cached
    // the below is correct for the current fields, if those change this has to change
    return video.getUuid();
  }
}
