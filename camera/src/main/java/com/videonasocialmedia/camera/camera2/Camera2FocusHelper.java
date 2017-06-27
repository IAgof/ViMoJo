package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_AUTO;
import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_OFF;

public class Camera2FocusHelper {
  private static final String TAG = Camera2FocusHelper.class.getCanonicalName();
  public static final int DEFAULT_FOCUS_SELECTION_MODE = CameraMetadata.CONTROL_AF_MODE_AUTO;
  public static final String AF_MODE_AUTO = "auto";
  public static final String AF_MODE_OFF = "off";
  public static final String AF_MODE_MANUAL = "manual";
  public static final String AF_MODE_REGIONS = "selective";
  private static final int AF_METERING_AREA_SIZE = 50;

  private final Camera2Wrapper camera2Wrapper;
  private final HashMap<Integer, String> focusSelectionMap = new HashMap<>();
  private CameraFeatures.SupportedValues supportedFocusSelectionValues;

  public Camera2FocusHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
    initFocusSelectionMap();
    setupSupportedValues();
  }

  private void initFocusSelectionMap() {
    this.focusSelectionMap.put(CameraMetadata.CONTROL_AF_MODE_AUTO, AF_MODE_AUTO);
    this.focusSelectionMap.put(CameraMetadata.CONTROL_AF_MODE_OFF, AF_MODE_MANUAL);
    this.focusSelectionMap.put(CameraMetadata.CONTROL_AF_MODE_AUTO, AF_MODE_REGIONS);
  }

  private void setupSupportedValues() {
    try {
      ArrayList<String> focusSelectionStringArrayList = new ArrayList<>();
      focusSelectionStringArrayList.add(AF_MODE_OFF);
      int [] returnedValues = camera2Wrapper.getCurrentCameraCharacteristics()
          .get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);

      for (int focusSelectionSetting : returnedValues) {
        if(focusSelectionSetting == CONTROL_AF_MODE_OFF) {
          focusSelectionStringArrayList.add(AF_MODE_MANUAL);
        }
        if(focusSelectionSetting == CONTROL_AF_MODE_AUTO){
          focusSelectionStringArrayList.add(AF_MODE_AUTO);
          if(camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1){
            focusSelectionStringArrayList.add(AF_MODE_REGIONS);
          }
        }
      }
      this.supportedFocusSelectionValues = new CameraFeatures.SupportedValues(
          focusSelectionStringArrayList, getDefaultFocusSelectionSetting());
    } catch (CameraAccessException e) {
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    }
  }

  private String getDefaultFocusSelectionSetting() {
    return focusSelectionMap.get(DEFAULT_FOCUS_SELECTION_MODE);
  }

  public boolean isFocusSelectionSupported() {
    return supportedFocusSelectionValues.values.size() > 1;
  }

  public CameraFeatures.SupportedValues getSupportedFocusSelectionModes() {
    return supportedFocusSelectionValues;
  }

  public void setCurrentFocusSelectionMode() {
    setFocusSelectionMode(supportedFocusSelectionValues.selectedValue);
  }

  public void resetFocusSelectionMode() {
    setFocusSelectionMode(AF_MODE_REGIONS);
  }

  public void setFocusSelectionMode(String afMode) {
    if (isFocusSelectionSupported() && modeIsSupported(afMode)) {
      supportedFocusSelectionValues.selectedValue = afMode;
      Log.d(TAG, "---------------- set focus selection to "+afMode+" .............");
      if(afMode == AF_MODE_AUTO){
        setFocusModeAuto();
      }
    }
  }

  private boolean modeIsSupported(String focusSelectionMode) {
    return supportedFocusSelectionValues.values.contains(focusSelectionMode);
  }

  public void setFocusModeRegion(int touchEventX, int touchEventY, int viewWidth,
                                 int viewHeight) {

    MeteringRectangle[] focusMeteringRectangle = camera2Wrapper.getMeteringRectangles(touchEventX,
        touchEventY, viewWidth, viewHeight, AF_METERING_AREA_SIZE);

    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
        CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_AUTO);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_REGIONS,
        focusMeteringRectangle);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
        CameraMetadata.CONTROL_AF_TRIGGER_START);
    camera2Wrapper.updatePreview();
  }

  public void setFocusModeManual(int seekbarProgress) {
    float minimumLens = 0;
    try {
      minimumLens = camera2Wrapper.getCurrentCameraCharacteristics()
          .get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    }
    float num = (((float) seekbarProgress) * minimumLens / 100);

    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_OFF);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.LENS_FOCUS_DISTANCE, num);
    camera2Wrapper.updatePreview();

    Log.d(TAG, "Control mode " + camera2Wrapper.getPreviewBuilder().get(CaptureRequest.CONTROL_MODE) +
        ", AF mode: " + camera2Wrapper.getPreviewBuilder().get(CaptureRequest.CONTROL_AF_MODE) +
        ", Focus value; " +camera2Wrapper.getPreviewBuilder().get(CaptureRequest.LENS_FOCUS_DISTANCE));
  }

  public void setFocusModeAuto(){
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
        CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
    camera2Wrapper.updatePreview();
  }
}