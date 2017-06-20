package com.videonasocialmedia.camera.camera2;

import java.util.List;

/**
 * Created by jliarte on 19/06/17.
 */

public class CameraFeatures {
  public static class SupportedValues {
    public final List<String> values;
    public String selectedValue;
    public SupportedValues(List<String> values, String selectedValue) {
      this.values = values;
      this.selectedValue = selectedValue;
    }
  }
}
