package com.videonasocialmedia.vimojo.sound.model;

/**
 * Created by alvaro on 7/12/16.
 */

public class VoiceOver {

  private final String path;
  private final float volume;

  public VoiceOver(String path, float volume){
    this.path = path;
    this.volume = volume;
  }

  public String getPath(){
    return path;
  }

  public float getVolume(){
    return volume;
  }
}
