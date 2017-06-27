package com.videonasocialmedia.camera.camera2;

import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;

public class Camera2ZoomHelper {
  private static final String TAG = Camera2ZoomHelper.class.getCanonicalName();
  private final Camera2Wrapper camera2Wrapper;
  private float maxzoom = 0;

  // zoom, move to custom view
  public float fingerSpacing = 0;
  public double currentZoomLevel = 1;


  public Camera2ZoomHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
    try {
      maxzoom = (camera2Wrapper.getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)) * 5;
    } catch (CameraAccessException e) {
      maxzoom = 0;
    }
  }

  /**
   * Sets zoom using finger spacing.
   *
   * @param currentFingerSpacing finger spacing metered on screen
   * @return zoom level set for updating zoom seekbar
   * @throws CameraAccessException If camera characteristics cannot be accessed
   */
  public float onTouchZoom(float currentFingerSpacing) throws CameraAccessException {
    if (fingerSpacing != 0) {
      if (currentFingerSpacing > fingerSpacing && maxzoom > currentZoomLevel) {
        currentZoomLevel++;
      } else if (currentFingerSpacing < fingerSpacing && currentZoomLevel > 1) {
        currentZoomLevel--;
      }
    }
    fingerSpacing = currentFingerSpacing;
    setCurrentZoom();
    return (float) (currentZoomLevel / maxzoom);
  }

  /**
   * Sets zoom using zoom seekbar.
   * @param zoomValue in zoom seekbar
   * @throws CameraAccessException If camera characteristics cannot be accessed
   */
  public void seekBarZoom(float zoomValue) throws CameraAccessException {
    currentZoomLevel = zoomValue * maxzoom;
    setCurrentZoom();
  }

  void setCurrentZoom() throws CameraAccessException {
    Rect activeArraySize = camera2Wrapper.getCurrentCameraCharacteristics()
            .get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
    int minW = (int) (activeArraySize.width() / maxzoom);
    int minH = (int) (activeArraySize.height() / maxzoom);
    int difW = activeArraySize.width() - minW;
    int difH = activeArraySize.height() - minH;
    int cropW = difW / 100 * (int) currentZoomLevel;
    int cropH = difH / 100 * (int) currentZoomLevel;
    cropW -= cropW & 3;
    cropH -= cropH & 3;
    Rect zoom = new Rect(cropW, cropH, activeArraySize.width()
            - cropW, activeArraySize.height() - cropH);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.SCALER_CROP_REGION, zoom);

    try {
      camera2Wrapper.getPreviewSession().setRepeatingRequest(
              camera2Wrapper.getPreviewBuilder().build(), null, null);
    } catch (CameraAccessException cae) {
      cae.printStackTrace();
      Log.e(TAG, "Error setting zoom - camera access", cae);
    }
  }

}