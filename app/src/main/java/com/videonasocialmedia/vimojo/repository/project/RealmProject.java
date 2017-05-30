package com.videonasocialmedia.vimojo.repository.project;


import com.videonasocialmedia.vimojo.repository.music.RealmMusic;
import com.videonasocialmedia.vimojo.repository.track.RealmTrack;
import com.videonasocialmedia.vimojo.repository.video.RealmVideo;

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
  public RealmList<RealmTrack> tracks;
  public RealmList<RealmMusic> musics;

  public RealmProject() {
    this.videos = new RealmList<RealmVideo>();
    this.tracks = new RealmList<RealmTrack>();
    this.musics = new RealmList<RealmMusic>();
  }

  public RealmProject(String uuid, String title, String lastModification, String projectPath,
                      String quality, String resolution, String frameRate, int duration,
                      boolean isAudioFadeTransitionActivated,
                      boolean isVideoFadeTransitionActivated, boolean isWatermarkActivated) {
    this.uuid = uuid;
    this.title = title;
    this.lastModification = lastModification;
    this.projectPath = projectPath;
    this.quality = quality;
    this.resolution = resolution;
    this.frameRate = frameRate;
    this.duration = duration;
    this.videos = new RealmList<RealmVideo>();
    this.tracks = new RealmList<RealmTrack>();
    this.musics = new RealmList<RealmMusic>();
    this.isAudioFadeTransitionActivated = isAudioFadeTransitionActivated;
    this.isVideoFadeTransitionActivated = isVideoFadeTransitionActivated;
    this.isWatermarkActivated = isWatermarkActivated;
  }
}
