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
  public String description;
  public boolean directFalseTypeSelected;
  public boolean rawVideoTypeSelected;
  public boolean spoolTypeSelected;
  public boolean totalTypeSelected;
  public boolean graphicTypeSelected;
  public boolean pieceTypeSelected;
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

  public RealmProject() {
    this.videos = new RealmList<RealmVideo>();
    this.musics = new RealmList<RealmMusic>();
    this.tracks = new RealmList<RealmTrack>();
  }

  public RealmProject(String uuid, String title, String description,
                      boolean directFalseTypeSelected, boolean rawVideoTypeSelected,
                      boolean spoolTypeSelected, boolean totalTypeSelected,
                      boolean graphicTypeSelected, boolean pieceTypeSelected,
                      String lastModification, String projectPath,
                      String quality, String resolution, String frameRate, int duration,
                      boolean isAudioFadeTransitionActivated,
                      boolean isVideoFadeTransitionActivated, boolean isWatermarkActivated) {
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.directFalseTypeSelected = directFalseTypeSelected;
    this.rawVideoTypeSelected = rawVideoTypeSelected;
    this.spoolTypeSelected = spoolTypeSelected;
    this.totalTypeSelected = totalTypeSelected;
    this.graphicTypeSelected = graphicTypeSelected;
    this.pieceTypeSelected = pieceTypeSelected;
    this.lastModification = lastModification;
    this.projectPath = projectPath;
    this.quality = quality;
    this.resolution = resolution;
    this.frameRate = frameRate;
    this.duration = duration;
    this.videos = new RealmList<RealmVideo>();
    this.musics = new RealmList<RealmMusic>();
    this.tracks = new RealmList<RealmTrack>();
    this.isAudioFadeTransitionActivated = isAudioFadeTransitionActivated;
    this.isVideoFadeTransitionActivated = isVideoFadeTransitionActivated;
    this.isWatermarkActivated = isWatermarkActivated;
  }
}
