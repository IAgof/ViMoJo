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

public class Camera2ExposureTimeHelper {
  private static final String LOG_TAG = Camera2ExposureTimeHelper.class.getCanonicalName();
  public static Long MINIMUM_VIDEO_EXPOSURE_TIME = Long.valueOf(20000000); // (jliarte): 28/06/17 1/50 - in nanoseconds
  private final Camera2Wrapper camera2Wrapper;
  private Range<Long> exposureTimeRange;
  private Long minExposureTime;
  private Long maxExposureTime;
  private int currentExposureTime;

  public Camera2ExposureTimeHelper(Camera2Wrapper camera2Wrapper) {
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
      Log.d(LOG_TAG, "Supported exposure time range: " + minExposureTime / 1000000000d + " - "
              + maxExposureTime / 1000000000d);
    } catch (CameraAccessException e) {
      Log.e(LOG_TAG, "failed to get camera characteristics");
      Log.e(LOG_TAG, "reason: " + e.getReason());
      Log.e(LOG_TAG, "message: " + e.getMessage());
    } catch (NullPointerException npe) {
      Log.d(LOG_TAG, "Caught NullPointerException while getting camera shutter speed capabilities",
              npe);
    }
  }

  public int getMaximumExposureTime() {
    return maxExposureTime.intValue();
  }

  public int getMinimunExposureTime() {
    return minExposureTime.intValue();
  }

  public void setExposureTime(int seekbarProgress) {
    currentExposureTime = seekbarProgress;
    VideonaCaptureRequest.Builder previewBuilder = this.camera2Wrapper.getPreviewBuilder();
    previewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, (long) currentExposureTime);
    this.camera2Wrapper.updatePreview();

    Log.d(LOG_TAG, "Set exposure time to " + seekbarProgress);
  }

  public long getAverageSupportedExposure() {
    return (exposureTimeRange.getUpper() - exposureTimeRange.getLower()) / 2;
  }

  public long getNewExposureTime(Integer currentIso) {
    // (jliarte): 27/06/17 default value to the middle of the supported range
    long exposureTime = camera2Wrapper.getAverageSupportedExposure();
    if (camera2Wrapper.getLastCaptureResultParams().captureResultHasIso
            && camera2Wrapper.getLastCaptureResultParams().captureResultHasExposureTime) {
      double iso_ratio = (double) camera2Wrapper.getLastCaptureResultParams().captureResultIso
              / (double) currentIso;
      long exposureTime1;// TODO(jliarte): 27/06/17 when shutter speed will be enabled on
      // ISO change, this will be the correct setting (will we also include eventually the
      // exposure compensation?)
      exposureTime1 = (long) (
              camera2Wrapper.getLastCaptureResultParams().captureResultExposureTime * iso_ratio);
      if (exposureTime1 < Camera2ExposureTimeHelper.MINIMUM_VIDEO_EXPOSURE_TIME) {
        boolean isSubExposed = true;
        double subExposureRatio = (double) exposureTime1 /
                (double) Camera2ExposureTimeHelper.MINIMUM_VIDEO_EXPOSURE_TIME;
        Log.d(LOG_TAG, "Subexposure ratio: " + subExposureRatio);
      }
//    exposureTime = camera2Wrapper.getLastCaptureResultParams().captureResultExposureTime;
//    previewBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
      exposureTime = Math.min(exposureTime1, Camera2ExposureTimeHelper.MINIMUM_VIDEO_EXPOSURE_TIME);
//      exposureTime = exposureTime1;
      exposureTime = Math.min(camera2Wrapper.getLastCaptureResultParams().captureResultExposureTime,
              Camera2ExposureTimeHelper.MINIMUM_VIDEO_EXPOSURE_TIME);
    }
    return exposureTime;
  }

  public int getCurrentExposureTime() {
    return currentExposureTime;
  }

  public void setCurrentExposureTime() {
    setExposureTime(currentExposureTime);
  }

  public void setExposureTime(long exposureTime) {
    setExposureTime((int) exposureTime);
  }

  public void resetExposureTime() {
    // TODO(jliarte): 12/12/17 is there need to implement this method? as it's tied to resetISO
  }
}
