package com.videonasocialmedia.vimojo.repository.music;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alvaro on 12/04/17.
 */

public class RealmMusic extends RealmObject {

  @PrimaryKey
  @Required
  public String uuid;
  @Required
  public String musicPath;
  @Required
  public String title;
  @Required
  public String author;
  public int iconResourceId;
  public int duration;
  public float volume;

  public RealmMusic() {

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
