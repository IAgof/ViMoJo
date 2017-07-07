package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.Range;

/**
 * Created by jliarte on 26/05/17.
 */

class Camera2ISOHelper {
  private static final String TAG = Camera2ISOHelper.class.getCanonicalName();
  private static Integer currentIso = 0;
  private static Long MINIMUM_VIDEO_EXPOSURE_TIME = Long.valueOf(20000000); // (jliarte): 28/06/17 1/50 - in nanoseconds
  private final Camera2Wrapper camera2Wrapper;
  private Range<Integer> sensitivityRange;
  private Integer minSensitivity = 0;
  private Integer maxSensitivity = 0;
  private float[] apertureRange;
  private Range<Long> exposureTimeRange;
  private boolean isSubExposed = false;
  private double subExposureRatio;

  public Camera2ISOHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
//    setupSupportedValues();
  }

  public void setup() {
    setupSupportedValues();
  }

  void setupSupportedValues() {
    try {
      apertureRange = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
      exposureTimeRange = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);

      sensitivityRange = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
      minSensitivity = sensitivityRange.getLower();
      maxSensitivity = sensitivityRange.getUpper();
      Integer maxSensitivity = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY);
      Log.e(TAG, "Max sensitivity. " + maxSensitivity);
    } catch (CameraAccessException e) {
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    } catch (NullPointerException npe) {
      Log.d(TAG, "Caught NullPointerException while getting camera metering capabilities", npe);
    }
  }

  public boolean ISOSelectionSupported() {
    return minSensitivity != maxSensitivity;
  }

  public Range<Integer> getSupportedISORange() {
    return sensitivityRange;
  }

  public Integer getMaximumSensitivity() {
    return maxSensitivity;
  }

  public void setCurrentISOValue() {
    setISO(currentIso);
  }

  void setISO(Integer iso) {
    currentIso = iso;
    CaptureRequest.Builder previewBuilder = this.camera2Wrapper.getPreviewBuilder();
    if (iso == 0) {
      previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
    } else {
      long exposureTime = getNewExposureTime();
      Long currentExposureTime = previewBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
      previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF);
      Log.d(TAG, "Manual exposure settings: current exposure time " + currentExposureTime
              + " def setting: " + exposureTime);
      Log.d(TAG, "Setting ISO value to: " + iso);

      previewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
      previewBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
    }
    this.camera2Wrapper.updatePreview();
  }

  private long getNewExposureTime() {
    // (jliarte): 27/06/17 default value to the middle of the supported range
    long exposureTime = (exposureTimeRange.getUpper() - exposureTimeRange.getLower()) / 2;
    if (camera2Wrapper.getCaptureResultSettings().captureResultHasIso
            && camera2Wrapper.getCaptureResultSettings().captureResultHasExposureTime) {
      double iso_ratio = (double) camera2Wrapper.getCaptureResultSettings().captureResultIso
              / (double) currentIso;
      long exposureTime1;// TODO(jliarte): 27/06/17 when shutter speed will be enabled on
      // ISO change, this will be the correct setting and we also will include eventually the
      // exposure compensation
      exposureTime1 = (long) (
              camera2Wrapper.getCaptureResultSettings().captureResultExposureTime * iso_ratio);
      if (exposureTime1 < MINIMUM_VIDEO_EXPOSURE_TIME) {
        isSubExposed = true;
        subExposureRatio = (double) exposureTime1 / (double) MINIMUM_VIDEO_EXPOSURE_TIME;
        Log.d(TAG, "Subexposure ratio: " + subExposureRatio);
      }
//    exposureTime = camera2Wrapper.getCaptureResultSettings().captureResultExposureTime;
//    previewBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
      exposureTime = Math.min(exposureTime1, MINIMUM_VIDEO_EXPOSURE_TIME);
//      exposureTime = exposureTime1;
      exposureTime = Math.min(camera2Wrapper.getCaptureResultSettings().captureResultExposureTime,
              MINIMUM_VIDEO_EXPOSURE_TIME);
    }
    return exposureTime;
  }
}
