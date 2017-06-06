package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Log;

public class Camera2FocusHelper {
  private static final String TAG = Camera2FocusHelper.class.getCanonicalName();
  private final Camera2Wrapper camera2Wrapper;

  public Camera2FocusHelper(Camera2Wrapper camera2Wrapper) {
    this.camera2Wrapper = camera2Wrapper;
  }

  /********* Focus component ********/
  public void setFocus(int x, int y) throws CameraAccessException {
    // FIXME(jliarte): 26/05/17 not used vars
//    CameraCharacteristics characteristics = getCurrentCameraCharacteristics();
//    final Rect sensorArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

    //TODO: here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
    // final int y = (int)((motionEvent.getX() / (float)view.getWidth())  * (float)sensorArraySize.height());
    //final int x = (int)((motionEvent.getY() / (float)view.getHeight()) * (float)sensorArraySize.width());

    //TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
    final int halfTouchWidth = 150; //(int)motionEvent.getTouchMajor();
    final int halfTouchHeight = 150; //(int)motionEvent.getTouchMinor();
    MeteringRectangle focusAreaTouch = new MeteringRectangle(Math.max(x - halfTouchWidth, 0),
            Math.max(y - halfTouchHeight, 0),
            halfTouchWidth * 2,
            halfTouchHeight * 2,
            MeteringRectangle.METERING_WEIGHT_MAX - 1);

    CameraCaptureSession.CaptureCallback captureCallbackHandler =
            new CameraCaptureSession.CaptureCallback() {
      @Override
      public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                     TotalCaptureResult result) {
        super.onCaptureCompleted(session, request, result);

        if (request.getTag() == "FOCUS_TAG") {
          //the focus trigger is complete -
          //resume repeating (preview surface will get frames), clear AF trigger
          camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER, null);
          try {
            camera2Wrapper.getPreviewSession().setRepeatingRequest(
                    camera2Wrapper.getPreviewBuilder().build(), null, null);
          } catch (CameraAccessException e) {
            e.printStackTrace();
          }
        }
      }

      @Override
      public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request,
                                  CaptureFailure failure) {
        super.onCaptureFailed(session, request, failure);
        Log.e(TAG, "Manual AF failure: " + failure);
      }
    };

    // FIXME: 23/05/17 Prevent NPE, onTouch focus.
    if (camera2Wrapper.getPreviewSession() == null) {
      return;
    }
    //first stop the existing repeating request
    camera2Wrapper.getPreviewSession().stopRepeating();
    cancelCurrentTriggers(captureCallbackHandler);
    CreateNewTrigger(focusAreaTouch);
    //then we ask for a single request (not repeating!)
    RequestCapture(captureCallbackHandler);
  }

  private void RequestCapture(CameraCaptureSession.CaptureCallback captureCallbackHandler)
          throws CameraAccessException {
    camera2Wrapper.getPreviewSession().capture(camera2Wrapper.getPreviewBuilder().build(),
            captureCallbackHandler, camera2Wrapper.getBackgroundHandler());
  }

  private void CreateNewTrigger(MeteringRectangle focusAreaTouch) throws CameraAccessException {
    // Add a new AF trigger with focus region
    if (isMeteringAreaAFSupported()) {
      camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_REGIONS,
              new MeteringRectangle[]{focusAreaTouch});
    }
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_MODE,
            CameraMetadata.CONTROL_MODE_AUTO);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_AUTO);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_START);
    // We'll capture this later for resuming the preview
    camera2Wrapper.getPreviewBuilder().setTag("FOCUS_TAG");
  }

  private void cancelCurrentTriggers(CameraCaptureSession.CaptureCallback captureCallbackHandler)
          throws CameraAccessException {
    // Cancel any existing AF trigger (repeated touches, etc.)
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    camera2Wrapper.getPreviewBuilder().set(CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_OFF);
    RequestCapture(captureCallbackHandler);
  }

  private boolean isMeteringAreaAFSupported() throws CameraAccessException {
    Integer AFRegions = camera2Wrapper.getCurrentCameraCharacteristics()
            .get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
    return AFRegions != null && AFRegions >= 1;
  }

  public boolean advancedFocusSupported() {
    return false;
  }
}