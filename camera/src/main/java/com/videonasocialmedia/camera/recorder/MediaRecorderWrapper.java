package com.videonasocialmedia.camera.recorder;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import com.crashlytics.android.Crashlytics;
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
    // TODO: 12/12/2017 Update Profile. Get audio source preference from CameraSetting, MIC or CAMCORDER
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    // Camera2 video source, surface.
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    if (nextVideoAbsolutePath == null || nextVideoAbsolutePath.isEmpty()) {
      nextVideoAbsolutePath = videoPath;
    }
    int camcorderQuality = getCamcorderProfile(videoCameraFormat.getVideoWidth(),
        videoCameraFormat.getVideoHeight());
    mediaRecorder.setProfile(getCamcorderProfile(camcorderQuality));
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
    Crashlytics.log("error in media recorder detected: " + what + " ex: " + extra);
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
      Crashlytics.log("it was a MEDIA_INFO_UNKNOWN");
    }
    else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
    {
      Log.d(LOG_TAG, "it was a MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
      Crashlytics.log("it was a MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
    }
    else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
    {
      stop();
      Log.d(LOG_TAG, "it was a MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
      Crashlytics.log("it was a MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
    }
    else
    {
      Log.d(LOG_TAG, "unknown info");
      Crashlytics.log("unknown info");
    }
  }

  @NonNull
  public CamcorderProfile getCamcorderProfile(int camcorderQuality) {
    CamcorderProfile  camcorderProfile = CamcorderProfile.get(cameraIdSelected, camcorderQuality);
    // Width and height are done by VideoSource.Surface, configured with correct resolution.
    camcorderProfile.videoBitRate = videoCameraFormat.getVideoBitrate();
    camcorderProfile.videoFrameWidth = videoCameraFormat.getVideoWidth();
    camcorderProfile.videoFrameHeight = videoCameraFormat.getVideoHeight();
    camcorderProfile.videoCodec = videoCameraFormat.getVideoCodec();
    // TODO:(alvaro.martinez) 19/01/17 Update Profile, get Default num_channels, SamplingRate, BitRate. Get from CameraSetting user preference
    camcorderProfile.audioChannels = videoCameraFormat.getAudioChannels();
    camcorderProfile.audioSampleRate = videoCameraFormat.getAudioSamplingRate();
    camcorderProfile.audioBitRate = videoCameraFormat.getAudioBitrate();
    camcorderProfile.audioCodec = videoCameraFormat.getAudioCodec();
    camcorderProfile.fileFormat = videoCameraFormat.getFileFormat();
    camcorderProfile.quality = camcorderQuality;
    return camcorderProfile;
  }

  private int getCamcorderProfile(int videoWidth, int videoHeight) {
    VideoResolution videoResolution = new VideoResolution(videoWidth, videoHeight);
    if(videoResolution.is720p()){
      return CamcorderProfile.QUALITY_720P;
    }
    if(videoResolution.is1080p()) {
      return CamcorderProfile.QUALITY_1080P;
    }
    if(videoResolution.is4k()) {
      return CamcorderProfile.QUALITY_2160P;
    }
    return CamcorderProfile.QUALITY_HIGH;
  }

  private static class VideoResolution {
    private final int height;
    private final int width;

    public VideoResolution(int width, int height) {
      this.height = height;
      this.width = width;
    }

    public boolean is720p(){
      return (width == 1280 && height == 720) || (width == 720 && height == 1280);
    }

    public boolean is1080p(){
      return (width == 1920 && height == 1080) || (width == 1080 && height == 1920) ;
    }

    public boolean is4k(){
      return ((width == 4096 || width == 3840) && height == 2160)
          || (width == 2160 && (height == 4096 || height == 3840));
    }

  }
}
