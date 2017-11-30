package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.util.Log;
import android.util.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 21/11/17.
 */

public class Camera2FrameRateHelper {
  private static final String TAG = Camera2FocusHelper.class.getCanonicalName();
  private final Camera2Wrapper camera2Wrapper;
  private boolean isFrameRate24fpsSupported;
  private boolean isFrameRate25fpsSupported;
  private boolean isFrameRate30fpsSupported;
  private boolean isFrameRateSupported;

  public Camera2FrameRateHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
  }

  public void setup() {
    setupSupportedValues();
  }

  private void setupSupportedValues() {
    try {
      Range<Integer>[] fpsRangeSupported = camera2Wrapper.getCurrentCameraCharacteristics()
          .get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
      List<Range<Integer>> constantsFpsRangeSupported = new ArrayList<>();
      for(Range<Integer> fps: fpsRangeSupported) {
        if(fps.getLower() == fps.getUpper()){
          constantsFpsRangeSupported.add(fps);
          if(fps.getLower() == 24) {
            isFrameRate24fpsSupported = true;
          } else {
            if(fps.getLower() == 25) {
              isFrameRate25fpsSupported = true;
            } else {
              if(fps.getLower() == 30) {
                isFrameRate30fpsSupported = true;
              }
            }
          }
        }
      }
      if(isFrameRate24fpsSupported || isFrameRate25fpsSupported || isFrameRate30fpsSupported) {
        isFrameRateSupported = true;
      }
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    }
  }

  public boolean isFrameRateSupported() {
    return isFrameRateSupported;
  }

}