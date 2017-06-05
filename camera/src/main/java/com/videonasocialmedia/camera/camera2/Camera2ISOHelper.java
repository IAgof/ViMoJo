package com.videonasocialmedia.camera.camera2;

/**
 * Created by jliarte on 26/05/17.
 */

class Camera2ISOHelper {
  private final Camera2Wrapper camera2Wrapper;

  public Camera2ISOHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
  }

  public boolean ISOSelectionSupported() {
    return false;
  }
}
