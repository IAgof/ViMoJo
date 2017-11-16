package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCaptureSession;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureRequest;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureResult;

import java.util.ArrayList;
import java.util.HashMap;

import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO;
import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_OFF;

public class Camera2FocusHelper {
  private static final String TAG = Camera2FocusHelper.class.getCanonicalName();
  public static final int DEFAULT_FOCUS_SELECTION_MODE = CameraMetadata
      .CONTROL_AF_MODE_CONTINUOUS_VIDEO;
  public static final String AF_MODE_AUTO = "auto";
  public static final String AF_MODE_MANUAL = "manual";
  public static final String AF_MODE_REGIONS = "selective";
  private static final int AF_METERING_AREA_SIZE = 50;

  private final Camera2Wrapper camera2Wrapper;
  private final HashMap<Integer, String> focusSelectionMap = new HashMap<>();
  private CameraFeatures.SupportedValues supportedFocusSelectionValues;
  private int seekBarProgress;
  private MeteringRectangle[] focusMeteringRectangle;
  private Integer maxAFRegions;

  public Camera2FocusHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
    initFocusSelectionMap();
  }

  private void initFocusSelectionMap() {
    this.focusSelectionMap.put(CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO, AF_MODE_AUTO);
    this.focusSelectionMap.put(CameraMetadata.CONTROL_AF_MODE_OFF, AF_MODE_MANUAL);
    this.focusSelectionMap.put(CameraMetadata.CONTROL_AF_MODE_AUTO, AF_MODE_REGIONS);
  }

  public void setup() {
    setupSupportedValues();
    focusMeteringRectangle = camera2Wrapper.getFullSensorAreaMeteringRectangle();
    seekBarProgress = 50;
  }

  void setupSupportedValues() {
    try {
      ArrayList<String> focusSelectionStringArrayList = new ArrayList<>();
      int[] returnedValues;
      returnedValues = camera2Wrapper.getCurrentCameraCharacteristics()
          .get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
      maxAFRegions = camera2Wrapper.getCurrentCameraCharacteristics()
          .get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
      for (int focusSelectionSetting : returnedValues) {
        if(focusSelectionSetting == CONTROL_AF_MODE_OFF) {
          focusSelectionStringArrayList.add(AF_MODE_MANUAL);
        }
        if (focusSelectionSetting == CONTROL_AF_MODE_CONTINUOUS_VIDEO) {
          focusSelectionStringArrayList.add(AF_MODE_AUTO);
          if (isFocusModeSelectiveSupported()) {
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

  private boolean isFocusModeSelectiveSupported() {
    return maxAFRegions >= 1;
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
    focusMeteringRectangle = camera2Wrapper.getFullSensorAreaMeteringRectangle();
    seekBarProgress = 50;
    if (isFocusModeSelectiveSupported()) {
      setFocusSelectionMode(AF_MODE_REGIONS);
    }
    setFocusModeAuto();
  }

  public void setFocusSelectionMode(String afMode) {
    if (isFocusSelectionSupported() && modeIsSupported(afMode)) {
      supportedFocusSelectionValues.selectedValue = afMode;
      Log.d(TAG, "---------------- set focus selection to "+afMode+" .............");
      if (afMode.compareTo(AF_MODE_AUTO) == 0) {
        setFocusModeAuto();
      }
      if (afMode.compareTo(AF_MODE_MANUAL) == 0) {
        setFocusModeManual(seekBarProgress);
      }
      if (afMode.compareTo(AF_MODE_REGIONS) == 0) {
        applyModeFocusRegion(focusMeteringRectangle);
      }
    }
  }

  private boolean modeIsSupported(String focusSelectionMode) {
    return supportedFocusSelectionValues.values.contains(focusSelectionMode);
  }

  public void setFocusModeRegion(int touchEventX, int touchEventY, int viewWidth, int viewHeight) {
    focusMeteringRectangle = camera2Wrapper.getMeteringRectangles(touchEventX,
        touchEventY, viewWidth, viewHeight, AF_METERING_AREA_SIZE);

    applyModeFocusRegion(focusMeteringRectangle);
  }

  private void applyModeFocusRegion(MeteringRectangle[] focusMeteringRectangle)  {
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_REGIONS,
            focusMeteringRectangle);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_AUTO);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_START);
    try {
      camera2Wrapper.getPreviewSession().capture(camera2Wrapper.getPreviewBuilder().build(),
              new VideonaCameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull VideonaCameraCaptureSession session,
                                       @NonNull VideonaCaptureRequest request,
                                       @NonNull VideonaCaptureResult result) {
          camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER, null);
          camera2Wrapper.updatePreview();
        }
      }, camera2Wrapper.getBackgroundHandler());
      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
              CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  public void setFocusModeManual(int seekbarProgress) {
    seekBarProgress = seekbarProgress;
    float minimumLens = 0;
    try {
      minimumLens = camera2Wrapper.getCurrentCameraCharacteristics()
          .get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    } catch (NullPointerException npe) {
      Log.e(TAG, "failed to get minimun focus distance");
    }
    float num = (((float) (100 - seekbarProgress)) * minimumLens / 100);

    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_OFF);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.LENS_FOCUS_DISTANCE, num);
    camera2Wrapper.updatePreview();
  }

  public void setFocusModeAuto(){
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
        CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
    camera2Wrapper.updatePreview();
  }
}