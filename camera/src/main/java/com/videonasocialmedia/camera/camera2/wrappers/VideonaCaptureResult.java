package com.videonasocialmedia.camera.camera2.wrappers;

import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;

import com.samsung.android.sdk.camera.SCaptureResult;
import com.samsung.android.sdk.camera.STotalCaptureResult;

import java.util.HashMap;

/**
 * Created by jliarte on 14/11/17.
 */

public class VideonaCaptureResult {
  private boolean isSamsungDevice = false;
  private STotalCaptureResult samsungCaptureResult;
  private TotalCaptureResult systemCaptureResult;

  public static HashMap<CaptureResult.Key<?>, SCaptureResult.Key<?>>
          captureResultMap;
  static {
    captureResultMap = new HashMap<>();
    captureResultMap.put(CaptureResult.SENSOR_SENSITIVITY, SCaptureResult.SENSOR_SENSITIVITY);
    captureResultMap.put(CaptureResult.CONTROL_AE_STATE, SCaptureResult.CONTROL_AE_STATE);
    captureResultMap.put(CaptureResult.SENSOR_EXPOSURE_TIME, SCaptureResult.SENSOR_EXPOSURE_TIME);
    captureResultMap.put(CaptureResult.SENSOR_FRAME_DURATION, SCaptureResult.SENSOR_FRAME_DURATION);
    captureResultMap.put(CaptureResult.LENS_APERTURE, SCaptureResult.LENS_APERTURE);
    captureResultMap.put(CaptureResult.LENS_FOCAL_LENGTH, SCaptureResult.LENS_FOCAL_LENGTH);
    captureResultMap.put(CaptureResult.LENS_FOCUS_DISTANCE, SCaptureResult.LENS_FOCUS_DISTANCE);
//    captureResultMap.put(, S);
  }

  public VideonaCaptureResult(STotalCaptureResult result) {
    this.isSamsungDevice = true;
    this.samsungCaptureResult = result;
  }

  public VideonaCaptureResult(TotalCaptureResult result) {
    this.systemCaptureResult = result;
  }

  public <T> T get(CaptureResult.Key<T> key) {
    if (isSamsungDevice) {
      return (T) samsungCaptureResult.get(getSamsungKey(key));
    } else {
      return systemCaptureResult.get(key);
    }
  }

  private SCaptureResult.Key getSamsungKey(CaptureResult.Key key) {
    return captureResultMap.get(key);
  }
}
