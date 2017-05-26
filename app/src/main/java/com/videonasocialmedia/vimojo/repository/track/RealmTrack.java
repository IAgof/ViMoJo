package com.videonasocialmedia.vimojo.repository.track;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alvaro on 10/04/17.
 */

public class RealmTrack extends RealmObject {

  @PrimaryKey
  public String uuid;
  public int id;
  public float volume;
  public boolean mute;
  public boolean solo;
  public int position;

  public RealmTrack(){

  }

  public RealmTrack(String uuid, int id, float volume, boolean mute, boolean solo, int position){
    this.uuid = uuid;
    this.id = id;
    this.volume = volume;
    this.mute = mute;
    this.solo = solo;
    this.position = position;
  }
}
