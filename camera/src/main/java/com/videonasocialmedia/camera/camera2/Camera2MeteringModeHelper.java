package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Log;
import android.util.Range;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jliarte on 26/05/17.
 */

public class Camera2MeteringModeHelper {
  private static final String TAG = Camera2MeteringModeHelper.class.getCanonicalName();
  public static final String AE_MODE_ON = "ae_on";
  public static final String AE_MODE_OFF = "ae_off";
  public static final String AE_MODE_EXPOSURE_COMPENSATION = "ae_exposure_compensation";
  public static final String AE_MODE_REGIONS = "ae_mode_regions";
  private static final String DEFAULT_AE_MODE = AE_MODE_ON;
  public static final int AE_METERING_AREA_SIZE = 50;
  private final Camera2Wrapper camera2Wrapper;
  private CameraFeatures.SupportedValues supportedAEValues;
  private int[] aeAvailableModes;
  private int minExposureCompensation = 0;
  private int maxExposureCompensation = 0;
  private float exposureStep;
  private int maxAERegions;
  private int currentExposureCompensation = 0;

  public Camera2MeteringModeHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
    setupSupportedValues();
  }

  private void setupSupportedValues() {
    ArrayList<String> aeStringArrayList = new ArrayList<>();
    aeStringArrayList.add(AE_MODE_ON);
    getCameraMeteringCapabilities();
    if (Arrays.asList(aeAvailableModes).contains(CameraMetadata.CONTROL_AE_MODE_OFF)) {
      aeStringArrayList.add(AE_MODE_OFF);
    }
    if (minExposureCompensation != maxExposureCompensation) {
      aeStringArrayList.add(AE_MODE_EXPOSURE_COMPENSATION);
    }
    if (maxAERegions > 0) {
      aeStringArrayList.add(AE_MODE_REGIONS);
    }
    this.supportedAEValues = new CameraFeatures.SupportedValues(
            aeStringArrayList, getDefaultAESetting());
  }

  private void getCameraMeteringCapabilities() {
    try {
      CameraCharacteristics cameraCharacteristics = camera2Wrapper.getCurrentCameraCharacteristics();
      aeAvailableModes = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
      Range<Integer> exposure_range = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
      minExposureCompensation = exposure_range != null ? exposure_range.getLower() : 0;
      maxExposureCompensation = exposure_range != null ? exposure_range.getUpper() : 0;
      exposureStep = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();
//      this.aeRegions = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_REGIONS);
      this.maxAERegions = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
    } catch (CameraAccessException e) {
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    } catch (NullPointerException npe) {
      Log.e(TAG, "Caught NullPointerException while getting camera metering capabilities", npe);
    }
  }

  public void resetMeteringMode() {
    if (supportedAEValues.selectedValue.equals(AE_MODE_EXPOSURE_COMPENSATION)) {
      currentExposureCompensation = 0;
      setExposureCompensation(0);
    }
  }

  public void setCurrentMeteringMode() {
    if (supportedAEValues.selectedValue.equals(AE_MODE_EXPOSURE_COMPENSATION)) {
      setExposureCompensation(currentExposureCompensation);
    }
  }

  public void setExposureCompensation(int exposureCompensation) {
    supportedAEValues.selectedValue = AE_MODE_EXPOSURE_COMPENSATION;
    currentExposureCompensation = exposureCompensation;
    Log.d(TAG, "---------------- set exposure compensation to "
            +exposureCompensation+" .............");
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
            exposureCompensation);
    camera2Wrapper.updatePreview();
  }

  public void setMeteringPoint(int touchEventX, int touchEventY, int viewWidth, int viewHeight) {
    MeteringRectangle[] meteringRectangles = camera2Wrapper
            .getMeteringRectangles(touchEventX, touchEventY,
                    viewWidth, viewHeight, AE_METERING_AREA_SIZE);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
            CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_REGIONS,
            meteringRectangles);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
            CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
    camera2Wrapper.updatePreview();
  }

  private String getDefaultAESetting() {
    return DEFAULT_AE_MODE;
  }

  public boolean metteringModeSelectionSupported() {
    return supportedAEValues.values.size() > 1;
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

  public CameraFeatures.SupportedValues getSupportedMeteringModes() {
    return supportedAEValues;
  }
}
