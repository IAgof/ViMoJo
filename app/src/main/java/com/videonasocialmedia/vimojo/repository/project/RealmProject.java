package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
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
  public String musicTitle;
  public float musicVolume = Music.DEFAULT_MUSIC_VOLUME;
  public boolean isAudioFadeTransitionActivated;
  public boolean isVideoFadeTransitionActivated;
  public RealmList<RealmVideo> videos;

  public RealmProject() {
    this.videos = new RealmList<RealmVideo>();
  }

  public RealmProject(String uuid, String title, String lastModification, String projectPath,
                      String quality, String resolution, String frameRate, int duration,
                      boolean isAudioFadeTransitionActivated,
                      boolean isVideoFadeTransitionActivated) {
    this.uuid = uuid;
    this.title = title;
    this.lastModification = lastModification;
    this.projectPath = projectPath;
    this.quality = quality;
    this.resolution = resolution;
    this.frameRate = frameRate;
    this.duration = duration;
    this.videos = new RealmList<RealmVideo>();
    this.isAudioFadeTransitionActivated = isAudioFadeTransitionActivated;
    this.isVideoFadeTransitionActivated = isVideoFadeTransitionActivated;
  }
}
