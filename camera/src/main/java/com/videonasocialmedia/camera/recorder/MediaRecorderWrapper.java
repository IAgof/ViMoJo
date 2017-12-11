package com.videonasocialmedia.camera.recorder;

import android.media.MediaRecorder;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import com.videonasocialmedia.camera.utils.VideoCameraFormat;

import java.io.File;
import java.io.IOException;

/**
 * Created by alvaro on 18/01/17.
 */

public class MediaRecorderWrapper implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {

  private static final String LOG_TAG = MediaRecorderWrapper.class.getCanonicalName();
  private final String videoPath;
  private final int cameraIdSelected;
  private final long freeStorage;

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
                              int sensorOrientation, int rotation, String videoPath,
                              VideoCameraFormat videoCameraFormat, long freeStorage){

    this.mediaRecorder = mediaRecorder;
    this.cameraIdSelected = cameraIdSelected;
    this.sensorOrientation = sensorOrientation;
    this.rotation = rotation;
    this.videoPath = videoPath;
    this.videoCameraFormat = videoCameraFormat;
    this.freeStorage = freeStorage;
  }

  public void setUpMediaRecorder() throws IOException {
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    if (nextVideoAbsolutePath == null || nextVideoAbsolutePath.isEmpty()) {
      nextVideoAbsolutePath = videoPath;
    }
    mediaRecorder.setOutputFile(nextVideoAbsolutePath);
    mediaRecorder.setVideoEncodingBitRate(videoCameraFormat.getVideoBitrate());
    // TODO:(alvaro.martinez) 25/01/17 Check and support different bit rate
    mediaRecorder.setVideoFrameRate(30);
    //mediaRecorder.setCaptureRate(30);
    mediaRecorder.setVideoSize(videoCameraFormat.getVideoWidth(), videoCameraFormat.getVideoHeight());
    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    // TODO:(alvaro.martinez) 19/01/17 Update Profile, get Default num_channels, SamplingRate, BitRate
    mediaRecorder.setAudioChannels(videoCameraFormat.getAudioChannels());
    mediaRecorder.setAudioSamplingRate(videoCameraFormat.getAudioSamplingRate());
    mediaRecorder.setAudioEncodingBitRate(videoCameraFormat.getAudioBitrate());
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
    mediaRecorder.setMaxFileSize(freeStorage);
    mediaRecorder.setOnErrorListener(this);
    mediaRecorder.setOnInfoListener(this);
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

  @Override
  public void onError(MediaRecorder mr, int what, int extra) {
    Log.d(LOG_TAG, "error in media recorder detected: " + what + " ex: " + extra);
    if (what == MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN)
    {
      Log.d(LOG_TAG, "it was a media recorder error unknown");
    }
    else
    {
      Log.d(LOG_TAG, "unknown media error");
    }
  }

  @Override
  public void onInfo(MediaRecorder mr, int what, int extra) {
    Log.d(LOG_TAG, "info in media recorder detected: " + what + " ex: " + extra);
    if (what == MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN)
    {
      Log.d(LOG_TAG, "it was a MEDIA_INFO_UNKNOWN");
    }
    else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
    {
      Log.d(LOG_TAG, "it was a MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
    }
    else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
    {
      Log.d(LOG_TAG, "it was a MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
    }
    else
    {
      Log.d(LOG_TAG, "unknown info");
    }
  }
}
