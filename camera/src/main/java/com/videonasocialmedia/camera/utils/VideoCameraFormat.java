package com.videonasocialmedia.camera.utils;

/**
 * Created by alvaro on 25/01/17.
 */

public class VideoCameraFormat {

  private int videoWidth = 1920;
  private int videoHeight = 1080;
  private int videoBitrate = 32 * 1000 * 1000;
  private int audioBitrate = 192 * 1000;
  private int audioChannels = 1;
  private int samplingRate = 48 * 1000;
  private int frameRate = 30;

  public VideoCameraFormat(int videoWidth, int videoHeight, int videoBitrate, int frameRate){
    this.videoWidth = videoWidth;
    this.videoHeight = videoHeight;
    this.videoBitrate = videoBitrate;
    this.frameRate = frameRate;
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

  public int getFrameRate() {
    return frameRate;
  }
}
