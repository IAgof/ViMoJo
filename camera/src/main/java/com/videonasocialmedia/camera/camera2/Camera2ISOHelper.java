package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.Range;

import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureRequest;

/**
 * Created by jliarte on 26/05/17.
 */

class Camera2ISOHelper {
  private static final String LOG_TAG = Camera2ISOHelper.class.getCanonicalName();
  private static Integer currentIso = 0;
  private final Camera2Wrapper camera2Wrapper;
  private Range<Integer> sensitivityRange;
  private Integer minSensitivity = 0;
  private Integer maxSensitivity = 0;
  private float[] apertureRange;
//  private Range<Long> exposureTimeRange;
  private boolean isSubExposed = false;
  private double subExposureRatio;

  public Camera2ISOHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
  }

  public void setup() {
    setupSupportedValues();
  }

  void setupSupportedValues() {
    try { 
      apertureRange = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
//      exposureTimeRange = camera2Wrapper.getCurrentCameraCharacteristics()
//              .get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);

      sensitivityRange = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
      minSensitivity = sensitivityRange.getLower();
      maxSensitivity = sensitivityRange.getUpper();
      Integer maxSensitivity = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY);
      Log.e(LOG_TAG, "Max sensitivity. " + maxSensitivity);
    } catch (CameraAccessException e) {
      Log.e(LOG_TAG, "failed to get camera characteristics");
      Log.e(LOG_TAG, "reason: " + e.getReason());
      Log.e(LOG_TAG, "message: " + e.getMessage());
    } catch (NullPointerException npe) {
      Log.d(LOG_TAG, "Caught NullPointerException while getting camera metering capabilities", npe);
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
    VideonaCaptureRequest.Builder previewBuilder = this.camera2Wrapper.getPreviewBuilder();
    if (iso == 0) {
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
    } else {
      long exposureTime = camera2Wrapper.getNewExposureTime(currentIso);
      Long currentExposureTime = previewBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
      Log.d(LOG_TAG, "Manual exposure settings: current exposure time " + currentExposureTime
              + " def setting: " + exposureTime);
      Log.d(LOG_TAG, "Setting ISO value to: " + iso);

      previewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
      previewBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
    }
    this.camera2Wrapper.updatePreview();
  }

}
