package com.videonasocialmedia.camera.recorder;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.SparseIntArray;
import android.view.Surface;

import com.videonasocialmedia.camera.utils.VideoCameraFormat;

import java.io.File;
import java.io.IOException;

/**
 * Created by alvaro on 18/01/17.
 */

public class MediaRecorderWrapper {

  private final String videoPath;
  private final int cameraIdSelected;

  private MediaRecorder mediaRecorder;

  private String nextVideoAbsolutePath;

  private int rotation;
  private int sensorOrientation;

  VideoCameraFormat videoCameraFormat;

  private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
  private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
  private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
  private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

  static {
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
  }

  static {
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
  }

  public MediaRecorderWrapper(MediaRecorder mediaRecorder, int cameraIdSelected,
                              int sensorOrientation, int rotation, String videoPath, VideoCameraFormat
                                  videoCameraFormat){

    this.mediaRecorder = mediaRecorder;
    this.cameraIdSelected = cameraIdSelected;
    this.sensorOrientation = sensorOrientation;
    this.rotation = rotation;
    this.videoPath = videoPath;
    this.videoCameraFormat = videoCameraFormat;
  }

  public void setUpMediaRecorder() throws IOException {
    // TODO: 12/12/2017 Update Profile. Get audio source preference from CameraSetting, MIC or CAMCORDER
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    // Camera2 video source, surface.
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    if (nextVideoAbsolutePath == null || nextVideoAbsolutePath.isEmpty()) {
      nextVideoAbsolutePath = videoPath;
    }
    CamcorderProfile  camcorderProfile = CamcorderProfile.get(cameraIdSelected,
            CamcorderProfile.QUALITY_HIGH);
    // Width and height are done by VideoSource.Surface, configured with correct resolution.
    camcorderProfile.videoBitRate = videoCameraFormat.getVideoBitrate();
    camcorderProfile.videoFrameWidth = videoCameraFormat.getVideoWidth();
    camcorderProfile.videoFrameHeight = videoCameraFormat.getVideoHeight();
    // TODO:(alvaro.martinez) 19/01/17 Update Profile, get Default num_channels, SamplingRate, BitRate. Get from CameraSetting user preference
    camcorderProfile.audioChannels = videoCameraFormat.getAudioChannels();
    camcorderProfile.audioSampleRate = videoCameraFormat.getAudioSamplingRate();
    camcorderProfile.audioBitRate = videoCameraFormat.getAudioBitrate();
    mediaRecorder.setProfile(camcorderProfile);
    mediaRecorder.setOutputFile(nextVideoAbsolutePath);
    if(cameraIdSelected == 1)
      sensorOrientation = (sensorOrientation - 180 + 360) % 360;
    switch (sensorOrientation) {
      case SENSOR_ORIENTATION_DEFAULT_DEGREES:
        mediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
        break;
      case SENSOR_ORIENTATION_INVERSE_DEGREES:
        mediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
        break;
    }
    mediaRecorder.prepare();
  }

  public void start() {
    try {
      mediaRecorder.start();
    } catch (RuntimeException e) {
      new File(videoPath).delete();
      reset();
      release();
    }
  }

  public void stop() {
    try {
      mediaRecorder.stop();
    } catch (IllegalStateException illegalState) {
      reset();
      throw illegalState;
    } catch (RuntimeException noValidDataReceived) {
      // Note that a RuntimeException is intentionally thrown to the application, if no valid
      // audio/video data has been received when stop() is called. This happens if stop() is called
      // immediately after start(). The failure lets the application take action accordingly to
      // clean up the output file (delete the output file, for instance), since the output file is
      // not properly constructed when this happens.
      new File(videoPath).delete();  //you must delete the outputfile when the recorder stop failed.
      reset();
      throw noValidDataReceived;
    }
  }

  public void reset() {
    mediaRecorder.reset();
  }

  public void release(){
    mediaRecorder.release();
  }

  public Surface getSurface(){
    return mediaRecorder.getSurface();
  }

  // Call this only after the setAudioSource().
  public int getMaxAmplitude() {
    return mediaRecorder.getMaxAmplitude();
  }

}
