package com.videonasocialmedia.vimojo.repository.music;

import com.videonasocialmedia.vimojo.repository.track.RealmTrack;

import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alvaro on 12/04/17.
 */

public class RealmMusic extends RealmObject {

  @PrimaryKey
  public String uuid;
  public String musicPath;
  public String title;
  public String author;
  public int iconResourceId;
  public int duration;
  public float volume;

  public RealmMusic(){

  }

  public RealmMusic(String uuid, String musicPath, String title, String author, int iconResourceId,
                    int duration, float volume){
    this.uuid = uuid;
    this.musicPath = musicPath;
    this.title = title;
    this.author = author;
    this.iconResourceId = iconResourceId;
    this.duration = duration;
    this.volume = volume;
  }
}
