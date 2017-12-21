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
    Log.d(LOG_TAG, "Setting ISO value to: " + iso);
    VideonaCaptureRequest.Builder previewBuilder = this.camera2Wrapper.getPreviewBuilder();
    if (iso == 0) {
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
    } else {
      if (currentIso == 0) {
        long exposureTime = previewBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME).longValue();
        Log.d(LOG_TAG, "Manual exposure settings: current exposure time " + exposureTime);
        if (currentIso == 0) {
          exposureTime = camera2Wrapper.getNewExposureTime(iso);
          Log.d(LOG_TAG, "Calculated new exposure time: " + exposureTime);
        }
        previewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
        camera2Wrapper.exposureTimeChanged(exposureTime);
      }
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
      previewBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
    }
    currentIso = iso;
    this.camera2Wrapper.updatePreview();
  }

  public void resetISO() {
    setISO(0);
  }
}
