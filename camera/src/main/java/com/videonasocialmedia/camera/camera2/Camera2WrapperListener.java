package com.videonasocialmedia.camera.camera2;

import android.graphics.Rect;

/**
 * Created by alvaro on 19/01/17.
 */

public interface Camera2WrapperListener {

  void setFlashSupport();

  void setZoom(Rect rectValue);

  // future use, setFlashMode supported, set 3A modes supported
}
