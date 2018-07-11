package com.videonasocialmedia.vimojo.cut.repository.datasource;

import com.videonasocialmedia.vimojo.repository.music.datasource.RealmMusic;
import com.videonasocialmedia.vimojo.repository.track.datasource.RealmTrack;
import com.videonasocialmedia.vimojo.repository.video.datasource.RealmVideo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jliarte on 20/10/16.
 */
public class RealmProject extends RealmObject {
  @PrimaryKey
  public String uuid;
  public String title;
  public String description;
  public String lastModification;
  public String projectPath;
  public String quality;
  public String resolution;
  public String frameRate;
  public int duration;
  public String pathLastVideoExported;
  public String dateLastVideoExported;
  public boolean isAudioFadeTransitionActivated;
  public boolean isVideoFadeTransitionActivated;
  public boolean isWatermarkActivated;
  public RealmList<RealmVideo> videos;
  public RealmList<RealmMusic> musics;
  public RealmList<RealmTrack> tracks;
  public RealmList<String> productTypeList;

  public RealmProject() {
    this.videos = new RealmList<RealmVideo>();
    this.musics = new RealmList<RealmMusic>();
    this.tracks = new RealmList<RealmTrack>();
    this.productTypeList = new RealmList<String>();
  }

  public RealmProject(String uuid, String title, String description,
                      String lastModification, String projectPath,
                      String quality, String resolution, String frameRate, int duration,
                      boolean isAudioFadeTransitionActivated,
                      boolean isVideoFadeTransitionActivated, boolean isWatermarkActivated) {
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.lastModification = lastModification;
    this.projectPath = projectPath;
    this.quality = quality;
    this.resolution = resolution;
    this.frameRate = frameRate;
    this.duration = duration;
    this.videos = new RealmList<RealmVideo>();
    this.musics = new RealmList<RealmMusic>();
    this.tracks = new RealmList<RealmTrack>();
    this.productTypeList = new RealmList<String>();
    this.isAudioFadeTransitionActivated = isAudioFadeTransitionActivated;
    this.isVideoFadeTransitionActivated = isVideoFadeTransitionActivated;
    this.isWatermarkActivated = isWatermarkActivated;
  }
}
