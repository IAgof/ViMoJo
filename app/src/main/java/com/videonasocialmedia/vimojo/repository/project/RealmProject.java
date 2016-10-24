package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.repository.video.RealmVideo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jliarte on 20/10/16.
 */
public class RealmProject extends RealmObject {
  @PrimaryKey
  public String title;
  public String projectPath;
  public String quality;
  public String resolution;
  public String framerate;
  public String musicTitle;
  public float musicVolume = Music.DEFAULT_MUSIC_VOLUME;
  public RealmList<RealmVideo> videos;

  public RealmProject() {
    this.videos = new RealmList<RealmVideo>();
  }

  public RealmProject(String title, String projectPath, String quality, String resolution, String framerate) {
    this.title = title;
    this.projectPath = projectPath;
    this.quality = quality;
    this.resolution = resolution;
    this.framerate = framerate;
    this.videos = new RealmList<RealmVideo>();
  }
}
