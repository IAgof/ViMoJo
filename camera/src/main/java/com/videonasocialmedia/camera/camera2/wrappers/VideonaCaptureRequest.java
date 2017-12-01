package com.videonasocialmedia.camera.camera2.wrappers;

import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.view.Surface;

import com.crashlytics.android.Crashlytics;
import com.samsung.android.sdk.camera.SCameraMetadata;
import com.samsung.android.sdk.camera.SCaptureRequest;

import java.util.HashMap;

/**
 * Created by jliarte on 15/11/17.
 */
public class VideonaCaptureRequest {
  private SCaptureRequest samsungCaptureRequestBuild;
  private CaptureRequest systemCaptureRequestBuild;
  private boolean isSamsungDevice = false;

  public VideonaCaptureRequest(SCaptureRequest build) {
    this.isSamsungDevice = true;
    this.samsungCaptureRequestBuild = build;
  }

  public VideonaCaptureRequest(CaptureRequest build) {
    this.systemCaptureRequestBuild = build;
  }

  public SCaptureRequest getSamsungBuild() {
    return samsungCaptureRequestBuild;
  }

  public CaptureRequest getSystemBuild() {
    return systemCaptureRequestBuild;
  }

  public static final class Builder {
    private CaptureRequest.Builder systemCameraCaptureRequest;
    private SCaptureRequest.Builder samsungCaptureRequest;
    private boolean isSamsungCamera = false;

    private HashMap<CaptureRequest.Key, SCaptureRequest.Key> captureResultMap =
            new HashMap<>();

    protected void captureResultMapInit() {
      captureResultMap.put(CaptureRequest.CONTROL_AF_MODE, SCaptureRequest.CONTROL_AF_MODE);
      captureResultMap.put(CaptureRequest.CONTROL_AF_REGIONS, SCaptureRequest.CONTROL_AF_REGIONS);
      captureResultMap.put(CaptureRequest.CONTROL_AF_TRIGGER, SCaptureRequest.CONTROL_AF_TRIGGER);
      captureResultMap.put(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, SCaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION);
      captureResultMap.put(CaptureRequest.CONTROL_AE_LOCK, SCaptureRequest.CONTROL_AE_LOCK);
      captureResultMap.put(CaptureRequest.CONTROL_AE_MODE, SCaptureRequest.CONTROL_AE_MODE);
      captureResultMap.put(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, SCaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER);
      captureResultMap.put(CaptureRequest.CONTROL_AE_REGIONS, SCaptureRequest.CONTROL_AE_REGIONS);
      captureResultMap.put(CaptureRequest.CONTROL_AWB_MODE, SCaptureRequest.CONTROL_AWB_MODE);
      captureResultMap.put(CaptureRequest.CONTROL_MODE, SCaptureRequest.CONTROL_MODE);
      captureResultMap.put(CaptureRequest.FLASH_MODE, SCaptureRequest.FLASH_MODE);
      captureResultMap.put(CaptureRequest.LENS_FOCUS_DISTANCE, SCaptureRequest.LENS_FOCUS_DISTANCE);
      captureResultMap.put(CaptureRequest.SENSOR_EXPOSURE_TIME, SCaptureRequest.SENSOR_EXPOSURE_TIME);
      captureResultMap.put(CaptureRequest.SENSOR_SENSITIVITY, SCaptureRequest.SENSOR_SENSITIVITY);
      captureResultMap.put(CaptureRequest.SCALER_CROP_REGION, SCaptureRequest.SCALER_CROP_REGION);
      captureResultMap.put(CaptureRequest.SENSOR_FRAME_DURATION, SCaptureRequest.SENSOR_FRAME_DURATION);
      captureResultMap.put(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, SCaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
//        captureResultMap.put(, S);
    }

    private HashMap<Integer, Integer> captureMetadataMap = new HashMap<>();

    private void captureMetadataMapInit() {
      //      captureMetadataMap = new HashMap<>();
      captureMetadataMap.put(CaptureRequest.CONTROL_AE_MODE_ON, SCaptureRequest.CONTROL_AE_MODE_ON);
      captureMetadataMap.put(CaptureRequest.CONTROL_AE_MODE_OFF, SCaptureRequest.CONTROL_AE_MODE_OFF);
      captureMetadataMap.put(CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START, SCameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
      captureMetadataMap.put(CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL, SCameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL);
      captureMetadataMap.put(CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE, SCameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
      captureMetadataMap.put(CaptureRequest.CONTROL_AF_MODE_AUTO, SCaptureRequest.CONTROL_AF_MODE_AUTO);
      captureMetadataMap.put(CaptureRequest.CONTROL_AF_MODE_OFF, SCaptureRequest.CONTROL_AF_MODE_OFF);
      captureMetadataMap.put(CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO, SCaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
      captureMetadataMap.put(CameraMetadata.CONTROL_AF_TRIGGER_CANCEL, SCameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
      captureMetadataMap.put(CameraMetadata.CONTROL_AF_TRIGGER_START, SCameraMetadata.CONTROL_AF_TRIGGER_START);
      captureMetadataMap.put(CameraMetadata.CONTROL_AF_TRIGGER_IDLE, SCameraMetadata.CONTROL_AF_TRIGGER_IDLE);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_OFF, SCameraMetadata.CONTROL_AWB_MODE_OFF);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_AUTO, SCameraMetadata.CONTROL_AWB_MODE_AUTO);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT, SCameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT, SCameraMetadata.CONTROL_AWB_MODE_DAYLIGHT);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT, SCameraMetadata.CONTROL_AWB_MODE_FLUORESCENT);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT, SCameraMetadata.CONTROL_AWB_MODE_INCANDESCENT);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_SHADE, SCameraMetadata.CONTROL_AWB_MODE_SHADE);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_TWILIGHT, SCameraMetadata.CONTROL_AWB_MODE_TWILIGHT);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT, SCameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT);
      captureMetadataMap.put(CameraMetadata.CONTROL_AWB_MODE_OFF, SCameraMetadata.CONTROL_AWB_MODE_OFF);
      captureMetadataMap.put(CaptureRequest.CONTROL_MODE_AUTO, SCaptureRequest.CONTROL_MODE_AUTO);
      captureMetadataMap.put(CaptureRequest.CONTROL_MODE_OFF, SCaptureRequest.CONTROL_MODE_OFF);
      captureMetadataMap.put(CaptureRequest.FLASH_MODE_OFF, SCaptureRequest.FLASH_MODE_OFF);
      captureMetadataMap.put(CaptureRequest.FLASH_MODE_SINGLE, SCaptureRequest.FLASH_MODE_SINGLE);
      captureMetadataMap.put(CaptureRequest.FLASH_MODE_TORCH, SCaptureRequest.FLASH_MODE_TORCH);
//        captureMetadataMap.put(, S);
    }

    public Builder(CaptureRequest.Builder systemCameraCaptureRequest) {
      captureResultMapInit();
      captureMetadataMapInit();
      this.systemCameraCaptureRequest = systemCameraCaptureRequest;
    }

    public Builder(SCaptureRequest.Builder captureRequest) {
      captureResultMapInit();
      captureMetadataMapInit();
      this.isSamsungCamera = true;
      this.samsungCaptureRequest = captureRequest;
    }

    public void addTarget(Surface previewSurface) {
      if (isSamsungCamera) {
        samsungCaptureRequest.addTarget(previewSurface);
      } else {
        systemCameraCaptureRequest.addTarget(previewSurface);
      }
    }

    public <T> void set(CaptureRequest.Key<T> key, T value) {
      if (isSamsungCamera) {
        samsungCaptureRequest.set(getSamsungKey(key), getSamsungMetadata(value));
      } else {
        systemCameraCaptureRequest.set(key, value);
      }
    }

    private <T> T getSamsungMetadata(T value) {
      T metadataValue = (T) captureMetadataMap.get(value);
      return metadataValue == null ? value : metadataValue;
    }

    private <T> SCaptureRequest.Key<T> getSamsungKey(CaptureRequest.Key<T> key) {
      return captureResultMap.get(key);
    }

    public VideonaCaptureRequest build() {
      if (isSamsungCamera) {
        return new VideonaCaptureRequest(samsungCaptureRequest.build());
      } else {
        return new VideonaCaptureRequest(systemCameraCaptureRequest.build());
      }
    }

    public <T> T get(CaptureRequest.Key<T> key) {
      if (isSamsungCamera) {
        return samsungCaptureRequest.get(getSamsungKey(key));
      } else {
        return systemCameraCaptureRequest.get(key);
      }
    }

    public void disableSamsungJunk() {
      if (isSamsungCamera && samsungCaptureRequest != null) {
        try {
          samsungCaptureRequest.set(SCaptureRequest.METERING_MODE, SCaptureRequest.METERING_MODE_MANUAL);
        } catch (IllegalArgumentException invalidKey) {
          Crashlytics.log("Samsung camera initialized on " + Build.MODEL);
          Crashlytics.logException(invalidKey);
        }
      }
    }
  }
}
