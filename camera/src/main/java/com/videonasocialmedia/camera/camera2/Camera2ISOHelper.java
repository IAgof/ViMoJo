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
  private final Camera2Wrapper camera2Wrapper;
  private Range<Integer> sensitivityRange;
  private Integer minSensitivity = 0;
  private Integer maxSensitivity = 0;
  private float[] apertureRange;
  private Range<Long> exposureTimeRange;

  public Camera2ISOHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
    setupSupportedValues();
  }

  private void setupSupportedValues() {
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

  public void setISO(Integer isoValue) {
    currentIso = isoValue;
    setCurrentISO();
  }

  void setCurrentISO() {
    CaptureRequest.Builder previewBuilder = this.camera2Wrapper.getPreviewBuilder();
    if (currentIso == 0) {
      previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
    } else {
      // (jliarte): 27/06/17 default value to the middle of the supported range
      long exposureTime = (exposureTimeRange.getUpper() - exposureTimeRange.getLower()) / 2;
      if (camera2Wrapper.getCaptureResultSettings().captureResultHasIso && camera2Wrapper.getCaptureResultSettings().captureResultHasExposureTime) {
        double iso_ratio = (double)camera2Wrapper.getCaptureResultSettings().captureResultIso / (double)currentIso;
        // TODO(jliarte): 27/06/17 when shutter speed will be enabled on ISO change,
        // this will be the correct setting and we also will include eventually the exposure
        // compensation
//        exposureTime = (long) (camera2Wrapper.getCaptureResultSettings().captureResultExposureTime * iso_ratio);
//      previewBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
        exposureTime = camera2Wrapper.getCaptureResultSettings().captureResultExposureTime;
      }
      Long currentExposureTime = previewBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
      previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
      previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF);
      Log.d(TAG, "Manual exposure settings: current exposure time " + currentExposureTime
              + " def setting: " + exposureTime);
      Log.d(TAG, "Setting ISO value to: " + currentIso);

      previewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
      previewBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, currentIso);
    }
    this.camera2Wrapper.updatePreview();
  }
}
