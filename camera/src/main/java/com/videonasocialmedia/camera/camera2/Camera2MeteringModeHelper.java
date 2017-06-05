package com.videonasocialmedia.camera.camera2;

/**
 * Created by jliarte on 26/05/17.
 */

class Camera2MeteringModeHelper {
  private final Camera2Wrapper camera2Wrapper;

  public Camera2MeteringModeHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
  }

  public boolean metteringModeSelectionSupported() {
    return false;
  }
}
