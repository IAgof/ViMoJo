package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;

/**
 * Created by jliarte on 26/05/17.
 */

public class Camera2WhiteBalanceHelper {
  private static final String TAG = Camera2WhiteBalanceHelper.class.getCanonicalName();
  public static final int DEFAULT_WHITE_BALANCE_MODE = CameraMetadata.CONTROL_AWB_MODE_AUTO;
  public static final String WB_MODE_OFF = "off";
  public static final String WB_MODE_AUTO = "auto";
  public static final String WB_MODE_CLOUDY_DAYLIGHT = "cloudy-daylight";
  public static final String WB_MODE_DAYLIGHT = "daylight";
  public static final String WB_MODE_FLUORESCENT = "fluorescent";
  public static final String WB_MODE_INCANDESCENT = "incandescent";
  public static final String WB_MODE_SHADE = "shade";
  public static final String WB_MODE_TWILIGHT = "twilight";
  public static final String WB_MODE_FLASH = "flash";
  public static final String WB_MODE_WARM_FLUORESCENT = "warm-fluorescent";
  public static final String WB_MODE_MANUAL = "manual";
  private final Camera2Wrapper camera2Wrapper;
  private final HashBiMap<Integer, String> whiteBalanceMap = HashBiMap.create();
  private CameraFeatures.SupportedValues supportedWhiteBalanceValues;

  public Camera2WhiteBalanceHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
    initWhiteBalanceMap();
  }

  private void initWhiteBalanceMap() {
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_OFF, WB_MODE_OFF);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_AUTO, WB_MODE_AUTO);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT,
            WB_MODE_CLOUDY_DAYLIGHT);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT, WB_MODE_DAYLIGHT);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT, WB_MODE_FLUORESCENT);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT, WB_MODE_INCANDESCENT);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_SHADE, WB_MODE_SHADE);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_TWILIGHT, WB_MODE_TWILIGHT);
    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT,
            WB_MODE_WARM_FLUORESCENT);
//    this.whiteBalanceMap.put(CameraMetadata.CONTROL_AWB_MODE_OFF, WB_MODE_MANUAL);
  }

  public void setup() {
    setupSupportedValues();
  }

  private String getDefaultWhiteBalanceSetting() {
    return whiteBalanceMap.get(DEFAULT_WHITE_BALANCE_MODE);
  }

  void setupSupportedValues() {
    try {
      ArrayList<String> whiteBalanceStringArrayList = new ArrayList<>();
      int [] returnedValues = camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
      for (int whiteBalanceSetting : returnedValues) {
        whiteBalanceStringArrayList.add(convertWhiteBalanceToString(whiteBalanceSetting));
      }
      if (whiteBalanceStringArrayList.size() == 0) {
        whiteBalanceStringArrayList.add(WB_MODE_AUTO);
      }
      this.supportedWhiteBalanceValues = new CameraFeatures.SupportedValues(
              whiteBalanceStringArrayList, getDefaultWhiteBalanceSetting());
    } catch (CameraAccessException e) {
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    }
  }

  private String convertWhiteBalanceToString(int whiteBalanceSetting) {
    return whiteBalanceMap.get(whiteBalanceSetting);
  }

  public boolean whiteBalanceSelectionSupported() {
    return supportedWhiteBalanceValues.values.size() > 1;
  }

  public CameraFeatures.SupportedValues getSupportedWhiteBalanceModes() {
    return supportedWhiteBalanceValues;
  }

  public void resetWhiteBalanceMode() {
    setWhiteBalanceMode(WB_MODE_AUTO);
  }

  public void setCurrentWhiteBalanceMode() {
    setWhiteBalanceMode(supportedWhiteBalanceValues.selectedValue);
  }

  public void setWhiteBalanceMode(String whiteBalanceMode) {
    if (whiteBalanceSelectionSupported() && modeIsSupported(whiteBalanceMode)) {
      supportedWhiteBalanceValues.selectedValue = whiteBalanceMode;
      Log.d(TAG, "---------------- set white balance to "+whiteBalanceMode+" ............." +
              " with value " + getCameraMetadataWBFromString(whiteBalanceMode));
      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_MODE,
              CaptureRequest.CONTROL_MODE_AUTO);
      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AWB_MODE,
              getCameraMetadataWBFromString(whiteBalanceMode));
      camera2Wrapper.updatePreview();
    }
  }

  private Integer getCameraMetadataWBFromString(String whiteBalanceMode) {
    return whiteBalanceMap.inverse().get(whiteBalanceMode);
  }

  private boolean modeIsSupported(String whiteBalanceMode) {
    return supportedWhiteBalanceValues.values.contains(whiteBalanceMode);
  }
}


