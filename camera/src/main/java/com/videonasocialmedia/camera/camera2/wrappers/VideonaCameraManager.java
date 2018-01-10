package com.videonasocialmedia.camera.camera2.wrappers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.samsung.android.sdk.camera.SCameraDevice;
import com.samsung.android.sdk.camera.SCameraManager;

import java.lang.ref.WeakReference;

/**
 * Created by jliarte on 16/11/17.
 */
public class VideonaCameraManager {
  // TODO(jliarte): 16/11/17 move this class up? a parameter for context weak reference would be needed
  private CameraManager systemCameraManager;
  private SCameraManager sCameraManager;
  private boolean isSamsungCamera = false;

  public VideonaCameraManager(CameraManager systemCameraManager) {
    this.systemCameraManager = systemCameraManager;
  }

  public VideonaCameraManager(SCameraManager sCameraManager) {
    isSamsungCamera = true;
    this.sCameraManager = sCameraManager;
  }

  public VideonaCameraCharacteristics getCameraCharacteristics(String cameraId)
          throws CameraAccessException {
    // TODO(jliarte): 14/11/17 maybe we need to wrap CameraCharacteristics here too
    if (isSamsungCamera) {
      return new VideonaCameraCharacteristics(
              sCameraManager.getCameraCharacteristics(cameraId));
    } else {
      return new VideonaCameraCharacteristics(
              systemCameraManager.getCameraCharacteristics(cameraId));
    }
  }

  public String[] getCameraIdList() throws CameraAccessException {
    if (isSamsungCamera) {
      return sCameraManager.getCameraIdList();
    } else {
      return systemCameraManager.getCameraIdList();
    }
  }

  public void openCamera(String cameraId, final VideonaCameraDevice.StateCallback stateCallback,
                         Handler handler, WeakReference<Context> contextWeakReference) throws CameraAccessException {
    if (isSamsungCamera) {
      // TODO(jliarte): 14/11/17 implement this method
      SCameraDevice.StateCallback samsungStateCallback = new SCameraDevice.StateCallback() {
        @Override
        public void onOpened(SCameraDevice sCameraDevice) {
          stateCallback.onOpened(new VideonaCameraDevice(sCameraDevice));
        }

        @Override
        public void onDisconnected(SCameraDevice sCameraDevice) {
          stateCallback.onDisconnected(new VideonaCameraDevice(sCameraDevice));
        }

        @Override
        public void onError(SCameraDevice sCameraDevice, int error) {
          stateCallback.onError(new VideonaCameraDevice(sCameraDevice), error);
        }
      };
      sCameraManager.openCamera(cameraId, samsungStateCallback, handler);
    } else {
      CameraDevice.StateCallback systemCameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
          stateCallback.onOpened(new VideonaCameraDevice(cameraDevice));
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
          stateCallback.onDisconnected(new VideonaCameraDevice(cameraDevice));
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
          stateCallback.onError(new VideonaCameraDevice(cameraDevice), error);
        }
      };
      if (contextWeakReference.get() != null) {
        if (ActivityCompat.checkSelfPermission(contextWeakReference.get(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
          return;
        }
        systemCameraManager.openCamera(cameraId, systemCameraStateCallback, handler);
      }
    }
  }
}
