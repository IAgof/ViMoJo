package com.videonasocialmedia.camera.camera2;

/**
 * Created by alvaro on 19/01/17.
 */

public interface Camera2WrapperListener {

  void setFlashSupport();

  void setZoom(float zoomValue);

  void exposureTimeChanged(long exposureTime);

  void setError(String message);

  // future use, setFlashMode supported, set 3A modes supported
}
