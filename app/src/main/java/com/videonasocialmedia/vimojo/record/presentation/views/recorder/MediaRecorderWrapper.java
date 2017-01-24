package com.videonasocialmedia.vimojo.record.presentation.views.recorder;

import android.media.MediaRecorder;
import android.util.SparseIntArray;
import android.view.Surface;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import java.io.IOException;

/**
 * Created by alvaro on 18/01/17.
 */

public class MediaRecorderWrapper {

  private final String videoPath;
  private final int cameraIdSelected;
  /**
   * MediaRecorder
   */
  private MediaRecorder mediaRecorder;

  private String nextVideoAbsolutePath;

  private int rotation;
  private int sensorOrientation;

  Project currentProject;

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
                              int sensorOrientation, int rotation, String videoPath){

    this.mediaRecorder = mediaRecorder;
    this.cameraIdSelected = cameraIdSelected;
    this.sensorOrientation = sensorOrientation;
    this.rotation = rotation;
    this.videoPath = videoPath;
    currentProject = Project.getInstance(null,null,null);
  }

  public void setUpMediaRecorder() throws IOException {

    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    if (nextVideoAbsolutePath == null || nextVideoAbsolutePath.isEmpty()) {
      nextVideoAbsolutePath = videoPath;
    }
    mediaRecorder.setOutputFile(nextVideoAbsolutePath);
    mediaRecorder.setVideoEncodingBitRate(currentProject.getProfile().getVideoQuality()
        .getVideoBitRate());
    mediaRecorder.setVideoFrameRate(currentProject.getProfile().getVideoFrameRate().getFrameRate());
    mediaRecorder.setCaptureRate(currentProject.getProfile().getVideoFrameRate().getFrameRate());
    mediaRecorder.setVideoSize(currentProject.getProfile().getVideoResolution().getWidth(),
        currentProject.getProfile().getVideoResolution().getHeight());
    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    // TODO:(alvaro.martinez) 19/01/17 Update Profile, get Default num_channels, SamplingRate, BitRate
    mediaRecorder.setAudioChannels(1);
    mediaRecorder.setAudioSamplingRate(48000);
    mediaRecorder.setAudioEncodingBitRate(192000);
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

  public void start(){
    mediaRecorder.start();
  }

  public void stop(){
    mediaRecorder.stop();
  }

  public void reset(){
    mediaRecorder.reset();
  }

  public void release(){
    mediaRecorder.release();
  }

  public Surface getSurface(){
    return mediaRecorder.getSurface();
  }

}
