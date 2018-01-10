package com.videonasocialmedia.camera.camera2.wrappers;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import com.samsung.android.sdk.camera.SCameraCharacteristics;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jliarte on 14/11/17.
 */
public class VideonaCameraCharacteristics {
  public static boolean isGalaxyS6 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g920") ||
          Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g925");

  public static boolean isGalaxyS7 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g93");
  public static boolean isGalaxyS8 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g950u") ||
          Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g9500u") ||
          Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g9550") ||
          Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g9500") ||
          Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g955u");
  public static boolean isGalaxyS5 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g900");
  public static boolean isGalaxyS4 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("gt-i95");
  public static boolean isGalaxyS4Mini = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("gt-i9190");
  public static boolean isGalaxyNote3 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-n900") ||
          Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-g900");
  public static boolean isGalaxyNote4 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-n910");
  public static boolean isGalaxyNote5 = Build.MODEL.toLowerCase(Locale.US).replace(" ", "").contains("sm-n920");

  public static boolean isSamsung = isGalaxyS4 || isGalaxyS4Mini || isGalaxyS5 || isGalaxyNote5 ||
          isGalaxyS6 || isGalaxyS7 || isGalaxyS8;

  public static HashMap<CameraCharacteristics.Key<?>, SCameraCharacteristics.Key<?>>
          characteristicsMap = new HashMap<>();

  private void characteristicsMapInit() {
    characteristicsMap.put(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE, SCameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
    characteristicsMap.put(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE, SCameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
    characteristicsMap.put(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP, SCameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    characteristicsMap.put(CameraCharacteristics.SENSOR_ORIENTATION, SCameraCharacteristics.SENSOR_ORIENTATION);
    characteristicsMap.put(CameraCharacteristics.FLASH_INFO_AVAILABLE, SCameraCharacteristics.FLASH_INFO_AVAILABLE);

    characteristicsMap.put(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, SCameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
    characteristicsMap.put(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE, SCameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
    characteristicsMap.put(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP, SCameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);
    characteristicsMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AE, SCameraCharacteristics.CONTROL_MAX_REGIONS_AE);
    characteristicsMap.put(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, SCameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    characteristicsMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AF, SCameraCharacteristics.CONTROL_MAX_REGIONS_AF);
    characteristicsMap.put(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE, SCameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
    characteristicsMap.put(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES, SCameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
    characteristicsMap.put(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE, SCameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
    characteristicsMap.put(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE, SCameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
    characteristicsMap.put(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY, SCameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY);
    characteristicsMap.put(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, SCameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
    characteristicsMap.put(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM, SCameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);

    characteristicsMap.put(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES, SCameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
//    captureResultMap.put(, S);
  }

  private SCameraCharacteristics samsungCameraCharacteristics;
  private CameraCharacteristics systemCameraCharacteristics;
  private boolean isSamsungDevice = false;

  public VideonaCameraCharacteristics(CameraCharacteristics systemCameraCharacteristics) {
    characteristicsMapInit();
    this.systemCameraCharacteristics = systemCameraCharacteristics;
  }

  public VideonaCameraCharacteristics(SCameraCharacteristics cameraCharacteristics) {
    characteristicsMapInit();
    this.isSamsungDevice = true;
    this.samsungCameraCharacteristics = cameraCharacteristics;
  }

  public <T> T get(CameraCharacteristics.Key<T> key) {
    if (isSamsungDevice) {
      return (T) samsungCameraCharacteristics.get(getSamsungKey(key));
    } else {
      return systemCameraCharacteristics.get(key);
    }
  }

  private SCameraCharacteristics.Key getSamsungKey(CameraCharacteristics.Key key) {
    return characteristicsMap.get(key);
  }
}
