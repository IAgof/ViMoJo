package com.videonasocialmedia.vimojo.repository.project;


import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.repository.music.RealmMusic;
import com.videonasocialmedia.vimojo.repository.track.RealmTrack;
import com.videonasocialmedia.vimojo.repository.track.TrackToRealmTrackMapper;
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
  public RealmList<RealmMusic> musics;
  public float volumeVideoTrack;
  public boolean muteVideoTrack;
  public float volumeMusicTrack;
  public boolean muteMusicTrack;
  public int positionMusicTrack;
  public float volumeVoiceOverTrack;
  public boolean muteVoiceOverTrack;
  public int positionVoiceOverTrack;

  public RealmProject() {
    this.videos = new RealmList<RealmVideo>();
    this.musics = new RealmList<RealmMusic>();
  }

  public RealmProject(String uuid, String title, String lastModification, String projectPath,
                      String quality, String resolution, String frameRate, int duration,
                      boolean isAudioFadeTransitionActivated,
                      boolean isVideoFadeTransitionActivated, boolean isWatermarkActivated,
                      float volumeVideoTrack, boolean muteVideoTrack, float volumeMusicTrack,
                      boolean muteMusicTrack, int positionMusicTrack, float volumeVoiceOverTrack,
                      boolean muteVoiceOverTrack, int positionVoiceOverTrack) {
    this.uuid = uuid;
    this.title = title;
    this.lastModification = lastModification;
    this.projectPath = projectPath;
    this.quality = quality;
    this.resolution = resolution;
    this.frameRate = frameRate;
    this.duration = duration;
    this.videos = new RealmList<RealmVideo>();
    this.musics = new RealmList<RealmMusic>();
    this.isAudioFadeTransitionActivated = isAudioFadeTransitionActivated;
    this.isVideoFadeTransitionActivated = isVideoFadeTransitionActivated;
    this.isWatermarkActivated = isWatermarkActivated;
    this.volumeVideoTrack = volumeVideoTrack;
    this.muteVideoTrack = muteVideoTrack;
    this.volumeMusicTrack = volumeMusicTrack;
    this.muteMusicTrack = muteMusicTrack;
    this.positionMusicTrack = positionMusicTrack;
    this.volumeVoiceOverTrack = volumeVoiceOverTrack;
    this.muteVoiceOverTrack = muteVoiceOverTrack;
    this.positionVoiceOverTrack = positionVoiceOverTrack;
  }
}
