package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.Range;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jliarte on 26/05/17.
 */

class Camera2MeteringModeHelper {
  private static final String TAG = Camera2MeteringModeHelper.class.getCanonicalName();
  private static final Integer DEFAULT_AE_MODE = CameraMetadata.CONTROL_AE_MODE_ON;
  private static final String AE_MODE_OFF = "ae_off";
  private static final String AE_MODE_ON = "ae_on";
  private static final String AE_MODE_ON_ALWAYS_FLASH = "ae_on_always_flash";
  private static final String AE_MODE_ON_AUTO_FLASH = "ae_on_auto_flash";
  private static final String AE_MODE_ON_AUTO_FLASH_REDEYE = "ae_on_auto_flash_redeye";
  private final Camera2Wrapper camera2Wrapper;
  private HashMap<Integer, String> aeMap;
  private CameraFeatures.SupportedValues supportedAEValues;
  private int aeRegions;
  private int minExposureCompensation = 0;
  private int maxExposureCompensation = 0;
  private float exposureStep;
  private int currentExposureCompensation = 0;

  public Camera2MeteringModeHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
    initAEMap();
    setupSupportedValues();
  }

  private void initAEMap() {
    this.aeMap = new HashMap<>();
    this.aeMap.put(CameraMetadata.CONTROL_AE_MODE_OFF, AE_MODE_OFF);
    this.aeMap.put(CameraMetadata.CONTROL_AE_MODE_ON, AE_MODE_ON);
    this.aeMap.put(CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH, AE_MODE_ON_ALWAYS_FLASH);
    this.aeMap.put(CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH, AE_MODE_ON_AUTO_FLASH);
    this.aeMap.put(CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE,
            AE_MODE_ON_AUTO_FLASH_REDEYE);
  }

  private void setupSupportedValues() {
    try {
      ArrayList<String> aeStringArrayList = new ArrayList<>();
      CameraCharacteristics cameraCharacteristics = camera2Wrapper.getCurrentCameraCharacteristics();
      int [] returnedValues = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
      for (int aeSetting : returnedValues) {
        aeStringArrayList.add(convertAEToString(aeSetting));
      }
      this.supportedAEValues = new CameraFeatures.SupportedValues(
              aeStringArrayList, getDefaultAESetting());

      aeRegions = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);

      Range<Integer> exposure_range = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
      minExposureCompensation = exposure_range.getLower();
      maxExposureCompensation = exposure_range.getUpper();
      exposureStep = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();
      Log.d(TAG, "ae");
    } catch (CameraAccessException e) {
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    }
  }

  public void setExposureCompensation(int exposureCompensation) {
    currentExposureCompensation = exposureCompensation;
    Log.d(TAG, "---------------- set exposure compensation to "
            +exposureCompensation+" .............");
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
            exposureCompensation);
    camera2Wrapper.updatePreview();
  }

  private String getDefaultAESetting() {
    return aeMap.get(DEFAULT_AE_MODE);
  }

  private String convertAEToString(int aeSetting) {
    return null;
  }

  public boolean metteringModeSelectionSupported() {
    return true;
  }

  public int getMinimumExposureCompensation() {
    return minExposureCompensation;
  }

  public int getMaximumExposureCompensation() {
    return maxExposureCompensation;
  }

  public int getCurrentExposureCompensation() {
    return currentExposureCompensation;
  }
}
