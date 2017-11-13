package com.videonasocialmedia.camera.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.util.Range;
import android.util.Size;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by alvaro on 25/01/17.
 */

public class Camera2Settings {

  public static final int BACK_CAMERA_ID = 0;
  public static final int FRONT_CAMERA_ID = 1;
  private final Context context;

  private boolean isBackCamera720pSupported = false;
  private boolean isBackCamera1080pSupported = false;
  private boolean isBackCamera2160pSupported = false;

  private boolean isFrontCamera720pSupported = false;
  private boolean isFrontCamera1080pSupported = false;
  private boolean isFrontCamera2160pSupported = false;

  private boolean isFrameRateSupported = false;
  private boolean isFrameRate24fpsSupported = false;
  private boolean isFrameRate25fpsSupported = false;
  private boolean isFrameRate30fpsSupported = false;

  private CameraManager manager;
  private Size[] supportedVideoSizes;

  public Camera2Settings(Context context) throws CameraAccessException {

    this.context = context;

    final Activity activity = (Activity) context;
    if (null == activity || activity.isFinishing()) {
      return;
    }
    manager = (CameraManager) activity.getSystemService(context.CAMERA_SERVICE);

    checkVideoSize(BACK_CAMERA_ID);
    if(manager.getCameraIdList().length > 0) {
      checkVideoSize(FRONT_CAMERA_ID);
    }
    checkFrameRateSupport();
  }

  private void checkVideoSize(int cameraId) throws CameraAccessException {
   String camera = manager.getCameraIdList()[cameraId];
    // Choose the sizes for camera preview and video isRecording
    CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera);
    StreamConfigurationMap map = characteristics
        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    supportedVideoSizes = map.getOutputSizes(MediaRecorder.class);
    for(Size size: supportedVideoSizes){
      if(size.getWidth() == 1280 && size.getHeight() == 720){
            if(cameraId == BACK_CAMERA_ID) {
              isBackCamera720pSupported = true;
            } else {
              if(cameraId == FRONT_CAMERA_ID){
                isFrontCamera720pSupported = true;
              }
            }
      }
      if(size.getWidth() == 1920 && size.getHeight() == 1080){
        if(cameraId == BACK_CAMERA_ID) {
          isBackCamera1080pSupported = true;
        } else {
          if(cameraId == FRONT_CAMERA_ID){
            isFrontCamera1080pSupported = true;
          }
        }
      }
      if(size.getWidth() == 3840 && size.getHeight() == 2160){
        if(cameraId == BACK_CAMERA_ID) {
          isBackCamera2160pSupported = true;
        } else {
          if(cameraId == FRONT_CAMERA_ID){
            isFrontCamera2160pSupported = true;
          }
        }
      }
    }
  }

  public boolean isBackCamera720pSupported() {
    return isBackCamera720pSupported;
  }

  public boolean isBackCamera1080pSupported() {
    return isBackCamera1080pSupported;
  }

  public boolean isBackCamera2160pSupported() {
    return isBackCamera2160pSupported;
  }

  public boolean isFrontCamera720pSupported() {
    return isFrontCamera720pSupported;
  }

  public boolean isFrontCamera1080pSupported() {
    return isFrontCamera1080pSupported;
  }

  public boolean isFrontCamera2160pSupported() {
    return isFrontCamera2160pSupported;
  }

  private void checkFrameRateSupport() throws CameraAccessException {
    Range<Integer>[] fpsRangeSupported = getFPSRange();
    List<Range<Integer>> constantsFpsRangeSupported = new ArrayList<>();
    for(Range<Integer> fps: fpsRangeSupported) {
      if(fps.getLower() == fps.getUpper()){
        constantsFpsRangeSupported.add(fps);
        if(fps.getLower() == 24) {
          isFrameRate24fpsSupported = true;
        } else {
          if(fps.getLower() == 25) {
            isFrameRate25fpsSupported = true;
          } else {
            if(fps.getLower() == 30) {
              isFrameRate30fpsSupported = true;
            }
          }
        }
      }
    }
    if(constantsFpsRangeSupported.size() > 0) {
      isFrameRateSupported = true;
    }

  }

  private Range<Integer>[] getFPSRange() throws CameraAccessException {
    String camera = manager.getCameraIdList()[0];
    CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera);
    return characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
  }

  public boolean isFrameRateSupported() {
    return isFrameRateSupported;
  }

  public boolean isFrameRate24fpsSupported() {
    return isFrameRate24fpsSupported;
  }

  public boolean isFrameRate25fpsSupported() {
    return isFrameRate25fpsSupported;
  }

  public boolean isFrameRate30fpsSupported() {
    return isFrameRate30fpsSupported;
  }
}
