package com.videonasocialmedia.camera.camera2.wrappers;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.samsung.android.sdk.camera.SCameraCaptureSession;
import com.samsung.android.sdk.camera.SCameraDevice;

import java.util.List;

/**
 * Created by jliarte on 14/11/17.
 */
public class VideonaCameraDevice {
  private CameraDevice systemCameraDevice;
  private SCameraDevice sCameraDevice;
  private boolean isSamsungDevice = false;

  public VideonaCameraDevice(CameraDevice systemCameraDevice) {
    this.systemCameraDevice = systemCameraDevice;
  }

  public VideonaCameraDevice(SCameraDevice sCameraDevice) {
    isSamsungDevice = true;
    this.sCameraDevice = sCameraDevice;
  }

  public void close() {
    if (isSamsungDevice) {
      sCameraDevice.close();
    } else {
      systemCameraDevice.close();
    }
  }

  public VideonaCaptureRequest.Builder createCaptureRequest(int templatePreview)
          throws CameraAccessException {
    if (isSamsungDevice) {
      return new VideonaCaptureRequest.Builder(
              sCameraDevice.createCaptureRequest(templatePreview));
    } else {
      return new VideonaCaptureRequest.Builder(
              systemCameraDevice.createCaptureRequest(templatePreview));
    }
  }

  public void createCaptureSession(List<Surface> surfaceList,
                                   final VideonaCameraCaptureSession.StateCallback stateCallback,
                                   Handler handler) throws CameraAccessException {
    if (isSamsungDevice) {
      // TODO(jliarte): 14/11/17 explore generics for stateCallback wrapper implementation
      SCameraCaptureSession.StateCallback samsungStateCallback = new SCameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(SCameraCaptureSession sCameraCaptureSession) {
          stateCallback.onConfigured(new VideonaCameraCaptureSession(sCameraCaptureSession));
        }

        @Override
        public void onConfigureFailed(SCameraCaptureSession sCameraCaptureSession) {
          stateCallback.onConfigureFailed(new VideonaCameraCaptureSession(sCameraCaptureSession));
        }
      };
      sCameraDevice.createCaptureSession(surfaceList, samsungStateCallback, handler);
    } else {
      CameraCaptureSession.StateCallback systemStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
          stateCallback.onConfigured(new VideonaCameraCaptureSession(cameraCaptureSession));
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
          stateCallback.onConfigureFailed(new VideonaCameraCaptureSession(cameraCaptureSession));
        }
      };
      systemCameraDevice.createCaptureSession(surfaceList, systemStateCallback, handler);
    }
  }

  public abstract static class StateCallback {
    public abstract void onOpened(VideonaCameraDevice cameraDevice);

    public abstract void onDisconnected(VideonaCameraDevice cameraDevice);

    public abstract void onError(VideonaCameraDevice cameraDevice, int error);
  }
}
