package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Log;
import android.util.Range;

import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCharacteristics;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jliarte on 26/05/17.
 */

public class Camera2MeteringModeHelper {
  private static final String LOG_TAG = Camera2MeteringModeHelper.class.getCanonicalName();
  public static final String AE_MODE_ON = "ae_on";
  public static final String AE_MODE_OFF = "ae_off";
  public static final String AE_MODE_EXPOSURE_COMPENSATION = "ae_exposure_compensation";
  public static final String AE_MODE_REGIONS = "ae_mode_regions";
  private static final String DEFAULT_AE_MODE = AE_MODE_ON;
  public static final int AE_METERING_AREA_SIZE = 500;
  private final Camera2Wrapper camera2Wrapper;
  private CameraFeatures.SupportedValues supportedAEValues;
  private int[] aeAvailableModes;
  private int minExposureCompensation = 0;
  private int maxExposureCompensation = 0;
  private float exposureStep;
  private int maxAERegions;
  private int currentExposureCompensation = 0;
  private MeteringRectangle[] lastMeteringRectangles;

  public Camera2MeteringModeHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
  }

  public void setup() {
    setupSupportedValues();
  }

  void setupSupportedValues() {
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
      VideonaCameraCharacteristics cameraCharacteristics = camera2Wrapper.getCurrentCameraCharacteristics();
      aeAvailableModes = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
      Range<Integer> exposure_range = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
      minExposureCompensation = exposure_range != null ? exposure_range.getLower() : 0;
      maxExposureCompensation = exposure_range != null ? exposure_range.getUpper() : 0;
      exposureStep = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();
      this.maxAERegions = cameraCharacteristics
              .get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
//      if (maxAERegions > 0) {
//        this.defaultAERegions = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_REGIONS);
//      }
    } catch (CameraAccessException e) {
      Log.e(LOG_TAG, "failed to get camera characteristics");
      Log.e(LOG_TAG, "reason: " + e.getReason());
      Log.e(LOG_TAG, "message: " + e.getMessage());
    } catch (NullPointerException npe) {
      Log.e(LOG_TAG, "Caught NullPointerException while getting camera metering capabilities", npe);
    }
  }

  public void resetMeteringMode() {
    setAutoCameraControlMode();
    if (supportedAEValues.selectedValue.equals(AE_MODE_EXPOSURE_COMPENSATION)) {
      currentExposureCompensation = 0;
      setExposureCompensation(0);
    }
    if (maxAERegions > 0) {
      lastMeteringRectangles = camera2Wrapper.getFullSensorAreaMeteringRectangle();
      setMeteringRectangles(lastMeteringRectangles);
    }
  }

  public void setCurrentMeteringMode() {
    if (supportedAEValues.selectedValue.equals(AE_MODE_EXPOSURE_COMPENSATION)) {
      setExposureCompensation(currentExposureCompensation);
    }
    setMeteringRectangles(lastMeteringRectangles);
  }

  public void setExposureCompensation(int exposureCompensation) {
    setAutoCameraControlMode();
    supportedAEValues.selectedValue = AE_MODE_EXPOSURE_COMPENSATION;
    currentExposureCompensation = exposureCompensation;
    Log.d(LOG_TAG, "---------------- set exposure compensation to "
            +exposureCompensation+" .............");
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
            exposureCompensation);
    camera2Wrapper.updatePreview();
  }

  private void setAutoCameraControlMode() {
    // TODO(jliarte): 29/06/17 should update camera activity ISO submenu?
    camera2Wrapper.disableManualExposure();
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
    camera2Wrapper.setUpCaptureRequestBuilderAutoMode(camera2Wrapper.getPreviewBuilder());
  }

  public void setMeteringPoint(int touchEventX, int touchEventY, int viewWidth, int viewHeight) {
    setAutoCameraControlMode();
    lastMeteringRectangles = camera2Wrapper.getMeteringRectangles(touchEventX, touchEventY,
                    viewWidth, viewHeight, AE_METERING_AREA_SIZE);
    setMeteringRectangles(lastMeteringRectangles);
  }

  private void setMeteringRectangles(MeteringRectangle[] lastMeteringRectangles) {
    if (maxAERegions > 0) {
      camera2Wrapper.getPreviewBuilder().disableSamsungJunk();
      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
              CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
      camera2Wrapper.updatePreview();

      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
              CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_LOCK, false);

      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_REGIONS,
              lastMeteringRectangles);
      if (lastMeteringRectangles != null) {
        Log.e(LOG_TAG, "ae region set to " + lastMeteringRectangles[0]);
      }
      camera2Wrapper.updatePreview();
    }
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

  public float getExposureCompensationStep() {
    return exposureStep;
  }

  public int getCurrentExposureCompensation() {
    return currentExposureCompensation;
  }

  public CameraFeatures.SupportedValues getSupportedMeteringModes() {
    return supportedAEValues;
  }
}
