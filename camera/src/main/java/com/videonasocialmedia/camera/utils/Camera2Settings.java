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

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.camera.SCamera;
import com.samsung.android.sdk.camera.SCameraManager;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCharacteristics;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by alvaro on 25/01/17.
 */

public class Camera2Settings {

  public static final int BACK_CAMERA_ID = 0;
  public static final int FRONT_CAMERA_ID = 1;
  private final WeakReference<Context> contextWeakReference;

  private boolean isBackCamera720pSupported = false;
  private boolean isBackCamera1080pSupported = false;
  private boolean isBackCamera2160pSupported = false;

  private boolean isFrontCamera720pSupported = false;
  private boolean isFrontCamera1080pSupported = false;
  private boolean isFrontCamera2160pSupported = false;

  private VideonaCameraManager manager;
  private Size[] supportedVideoSizes;

  private SCamera sCamera;
  private SCameraManager sCameraManager;

  public Camera2Settings(Context context) throws CameraAccessException {

    contextWeakReference = new WeakReference<>(context);
    setupSamsungCamera();
  }

  private void setupSamsungCamera() {
    if (contextWeakReference.get() != null) {
      sCamera = new SCamera();
      try {
        sCamera.initialize(contextWeakReference.get());
        sCameraManager = sCamera.getSCameraManager();
        manager = new VideonaCameraManager(sCameraManager);
        return;
      } catch (SsdkUnsupportedException sCameraInitError) {
        if (sCameraInitError.getType() == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED) {
          // The device is not a Samsung device.
        } else if (sCameraInitError.getType() == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
          // The device does not support Camera.
//      } else if (sCameraInitError.getType() == SsdkUnsupportedException.SDK_VERSION_MISMATCH) {
          // There is a SDK version mismatch.
        }
      }
    }
  }

  private String getCameraId(int cameraIdSelected) throws CameraAccessException {
    getCameraManager();
    return manager.getCameraIdList()[cameraIdSelected];
  }

  VideonaCameraCharacteristics getCurrentCameraCharacteristics(int cameraId) throws CameraAccessException {
    return manager.getCameraCharacteristics(getCameraId(cameraId));
  }

  private void getCameraManager() {
    if (manager != null) {
      return;
    }
    if (contextWeakReference.get() != null) {
      final Activity activity = (Activity) contextWeakReference.get();
      if (sCameraManager != null) {
        manager = new VideonaCameraManager(sCameraManager);
      } else {
        manager = new VideonaCameraManager
            ((CameraManager) activity.getSystemService(activity.CAMERA_SERVICE));
      }
    }
  }

  public Range<Integer>[] getFPSRange(int cameraId) throws CameraAccessException {
    VideonaCameraCharacteristics characteristics = getCurrentCameraCharacteristics(cameraId);
    return characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
  }

  public Size[] getSupportedVideoSizes(int cameraId) throws CameraAccessException {
    VideonaCameraCharacteristics characteristics = getCurrentCameraCharacteristics(cameraId);
    StreamConfigurationMap map = characteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    return map.getOutputSizes(MediaRecorder.class);
  }

  public boolean hasFrontCamera() throws CameraAccessException {
    return manager.getCameraIdList().length > 0;
  }
}
