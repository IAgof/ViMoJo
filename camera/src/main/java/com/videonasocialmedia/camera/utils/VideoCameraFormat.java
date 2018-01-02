package com.videonasocialmedia.camera.utils;

import static android.media.MediaRecorder.AudioEncoder.AAC;
import static android.media.MediaRecorder.OutputFormat.MPEG_4;
import static android.media.MediaRecorder.VideoEncoder.H264;

/**
 * Created by alvaro on 25/01/17.
 */

public class VideoCameraFormat {

  private int videoWidth = 1280;
  private int videoHeight = 720;
  private int videoBitrate = 16 * 1000 * 1000;
  private int audioBitrate = 192 * 1000;
  private int audioChannels = 1;
  private int samplingRate = 48 * 1000;
  private int frameRate = 30;
  private int videoCodec = H264;
  private int audioCodec = AAC;
  private int fileFormat = MPEG_4;

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

  public int getVideoCodec() {
    return videoCodec;
  }

  public int getAudioCodec() {
    return audioCodec;
  }

  public int getFileFormat() {
    return fileFormat;
  }

}
