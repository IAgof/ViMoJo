package com.videonasocialmedia.camera.utils;

/**
 * Created by alvaro on 25/01/17.
 */

public class VideoFormat {

  private int videoWidth = 1280;
  private int videoHeight = 720;
  private int videoBitrate = 5 * 1000 * 1000;
  private int audioBitrate = 192 * 1000;
  private int audioChannels = 2;
  private int samplingRate = 48 * 1000;

  public VideoFormat(int videoWidth, int videoHeight, int videoBitrate){
    this.videoWidth = videoWidth;
    this.videoHeight = videoHeight;
    this.videoBitrate = videoBitrate;
  }

  public int getVideoWidth() {
    return videoWidth;
  }

  public int getVideoHeight() {
    return videoHeight;
  }

  public int getVideoBitrate() {
    return videoBitrate;
  }

  public int getAudioBitrate() {
    return audioBitrate;
  }

  public int getAudioChannels() {
    return audioChannels;
  }

  public int getAudioSamplingRate() {
    return samplingRate;
  }
}
