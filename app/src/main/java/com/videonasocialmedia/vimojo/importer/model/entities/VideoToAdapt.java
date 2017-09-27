package com.videonasocialmedia.vimojo.importer.model.entities;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by jliarte on 24/07/17.
 */
public class VideoToAdapt {
  private final int position;
  private final Video video;
  private final int rotation;
  private final String destVideoPath;
  public int numTriesAdaptingVideo = 0;

  public VideoToAdapt(Video video, String destVideoPath, int position, int cameraRotation, int retries) {
    this.video = video;
    this.destVideoPath = destVideoPath;
    this.position = position;
    this.rotation = cameraRotation;
    this.numTriesAdaptingVideo = retries;
  }

  public String getDestVideoPath() {
    return destVideoPath;
  }

  public int getPosition() {
    return position;
  }

  public Video getVideo() {
    return video;
  }

  public int getRotation() {
    return rotation;
  }
}
