package com.videonasocialmedia.vimojo.importer.repository;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by jliarte on 24/07/17.
 */

public class RealmVideoToAdapt extends RealmObject {
  @PrimaryKey
  @Required
  private String mediaPath;
  private int position;
  @Required private String video_uuid;
  private int rotation;
  @Required private String destVideoPath;
  private int numTriesAdaptingVideo = 0;

  public int getPosition() {
    return position;
  }

  public String getVideo_uuid() {
    return video_uuid;
  }

  public String getMediaPath() {
    return mediaPath;
  }

  public int getRotation() {
    return rotation;
  }

  public String getDestVideoPath() {
    return destVideoPath;
  }

  public int getNumTriesAdaptingVideo() {
    return numTriesAdaptingVideo;
  }

  public RealmVideoToAdapt(int position, String video_uuid, String mediaPath, int rotation,
                           String destVideoPath, int numTriesAdaptingVideo) {
    this.position = position;
    this.video_uuid = video_uuid;
    this.mediaPath = mediaPath;
    this.rotation = rotation;
    this.destVideoPath = destVideoPath;
    this.numTriesAdaptingVideo = numTriesAdaptingVideo;
  }

  public RealmVideoToAdapt() {

  }
}
