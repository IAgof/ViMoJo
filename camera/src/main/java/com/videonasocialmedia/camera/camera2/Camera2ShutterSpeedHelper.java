package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.Range;

import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureRequest;

/**
 * Created by jliarte on 22/11/17.
 */

public class Camera2ShutterSpeedHelper {
  private static final String LOG_TAG = Camera2ShutterSpeedHelper.class.getCanonicalName();
  public static Long MINIMUM_VIDEO_EXPOSURE_TIME = Long.valueOf(20000000); // (jliarte): 28/06/17 1/50 - in nanoseconds
  private final Camera2Wrapper camera2Wrapper;
  private Range<Long> exposureTimeRange;
  private Long minExposureTime;
  private Long maxExposureTime;

  public Camera2ShutterSpeedHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
  }

  public void setup() {
    setupSupportedValues();
  }

  private void setupSupportedValues() {
    try {
      exposureTimeRange = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);

      minExposureTime = exposureTimeRange.getLower();
      maxExposureTime = exposureTimeRange.getUpper();
      Log.d(LOG_TAG, "Supported exposure time range: " + minExposureTime / 1000000000d + " - " + maxExposureTime / 1000000000d);
    } catch (CameraAccessException e) {
      Log.e(LOG_TAG, "failed to get camera characteristics");
      Log.e(LOG_TAG, "reason: " + e.getReason());
      Log.e(LOG_TAG, "message: " + e.getMessage());
    } catch (NullPointerException npe) {
      Log.d(LOG_TAG, "Caught NullPointerException while getting camera shutter speed capabilities", npe);
    }

  }

  public int getMaximumExposureTime() {
    return maxExposureTime.intValue();
  }

  public int getMinimunExposureTime() {
    return minExposureTime.intValue();
  }

  public void setShuttedSpeed(int seekbarProgress) {
    int currentExposureTime = seekbarProgress;
    VideonaCaptureRequest.Builder previewBuilder = this.camera2Wrapper.getPreviewBuilder();
    previewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, (long) currentExposureTime);
    this.camera2Wrapper.updatePreview();

    Log.d(LOG_TAG, "Set exposure time to " + seekbarProgress);
  }
}
