package com.videonasocialmedia.camera.camera2.wrappers;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.samsung.android.sdk.camera.SCameraCaptureSession;
import com.samsung.android.sdk.camera.SCaptureRequest;
import com.samsung.android.sdk.camera.STotalCaptureResult;

/**
 * Created by jliarte on 14/11/17.
 */

public class VideonaCameraCaptureSession {
  private CameraCaptureSession systemCameraCaptureSession;
  private SCameraCaptureSession samsungCameraCaptureSession;
  private boolean isSamsungDevice = false;

  public VideonaCameraCaptureSession(SCameraCaptureSession samsungCameraCaptureSession) {
    this.isSamsungDevice = true;
    this.samsungCameraCaptureSession = samsungCameraCaptureSession;
  }

  public VideonaCameraCaptureSession(CameraCaptureSession cameraCaptureSession) {
    this.systemCameraCaptureSession = cameraCaptureSession;
  }

  public void stopRepeating() throws CameraAccessException {
    if (isSamsungDevice) {
      samsungCameraCaptureSession.stopRepeating();
    } else {
      systemCameraCaptureSession.stopRepeating();
    }
  }

  public void abortCaptures() throws CameraAccessException {
    if (isSamsungDevice) {
      samsungCameraCaptureSession.abortCaptures();
    } else {
      samsungCameraCaptureSession.abortCaptures();
    }
  }

  public void close() {
    if (isSamsungDevice) {
      samsungCameraCaptureSession.close();
    } else {
      systemCameraCaptureSession.close();
    }
  }

  public void setRepeatingRequest(VideonaCaptureRequest captureRequest,
                                  final VideonaCameraCaptureSession.CaptureCallback captureCallback,
                                  Handler handler) throws CameraAccessException {
    if (isSamsungDevice) {
      SCameraCaptureSession.CaptureCallback samsungCaptureCallback =
              new SCameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(SCameraCaptureSession session,
                                               SCaptureRequest request,
                                               STotalCaptureResult result) {
                  super.onCaptureCompleted(session, request, result);
                  captureCallback.onCaptureCompleted(new VideonaCameraCaptureSession(session),
                          new VideonaCaptureRequest(request),
                          new VideonaCaptureResult(result));
                }
              };
      samsungCameraCaptureSession.setRepeatingRequest(captureRequest.getSamsungBuild(),
              samsungCaptureCallback, handler);
    } else {
      CameraCaptureSession.CaptureCallback systemCaptureCallback =
              new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                  super.onCaptureCompleted(session, request, result);
                  captureCallback.onCaptureCompleted(new VideonaCameraCaptureSession(session),
                          new VideonaCaptureRequest(request),
                          new VideonaCaptureResult(result));
                }
              };
      systemCameraCaptureSession.setRepeatingRequest(captureRequest.getSystemBuild(),
              systemCaptureCallback, handler);
    }
  }

  public void capture(VideonaCaptureRequest request, CaptureCallback captureCallback,
                      Handler handler)
          throws CameraAccessException {
    if (isSamsungDevice) {
      SCameraCaptureSession.CaptureCallback samsungCaptureCallback =
              new VideonaSamsungCaptureCallback(captureCallback);
      samsungCameraCaptureSession.capture(request.getSamsungBuild(), samsungCaptureCallback,
              handler);
    } else {
      CameraCaptureSession.CaptureCallback systemCaptureCallback =
              new VideonaSystemCaptureCallback(captureCallback);
      systemCameraCaptureSession.capture(request.getSystemBuild(), systemCaptureCallback, handler);
    }
  }

  public abstract static class StateCallback {
    public abstract void onConfigured(VideonaCameraCaptureSession cameraCaptureSession);

    public abstract void onConfigureFailed(VideonaCameraCaptureSession cameraCaptureSession);
  }

  public abstract static class CaptureCallback {
    public abstract void onCaptureCompleted(@NonNull VideonaCameraCaptureSession session,
                                            @NonNull VideonaCaptureRequest request,
                                            @NonNull VideonaCaptureResult result);
  }

  static class VideonaSamsungCaptureCallback extends SCameraCaptureSession.CaptureCallback {
    private CaptureCallback captureCallback;

    VideonaSamsungCaptureCallback(CaptureCallback captureCallback) {
      this.captureCallback = captureCallback;
    }
    // TODO(jliarte): 15/11/17 implement this method here and in CaptureCallback
    //        @Override
    //        public void onCaptureStarted(SCameraCaptureSession session, SCaptureRequest request, long timestamp, long frameNumber) {
    //          super.onCaptureStarted(session, request, timestamp, frameNumber);
    //
    //        }

    // TODO(jliarte): 15/11/17 implement this method here and in CaptureCallback
    //        @Override
    //        public void onCaptureProgressed(SCameraCaptureSession session, SCaptureRequest request,
    //                                        SCaptureResult partialResult) {
    //          super.onCaptureProgressed(session, request, partialResult);
    //        }

    @Override
    public void onCaptureCompleted(SCameraCaptureSession session, SCaptureRequest request,
                                   STotalCaptureResult result) {
      super.onCaptureCompleted(session, request, result);
      captureCallback.onCaptureCompleted(new VideonaCameraCaptureSession(session),
              new VideonaCaptureRequest(request),
              new VideonaCaptureResult(result));
    }

    // TODO(jliarte): 15/11/17 implement this method here and in CaptureCallback
    //        @Override
    //        public void onCaptureFailed(SCameraCaptureSession session, SCaptureRequest request, SCaptureFailure failure) {
    //          super.onCaptureFailed(session, request, failure);
    //        }

    // TODO(jliarte): 15/11/17 implement this method here and in CaptureCallback
    //        @Override
    //        public void onCaptureSequenceCompleted(SCameraCaptureSession session, int sequenceId, long frameNumber) {
    //          super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
    //        }

    // TODO(jliarte): 15/11/17 implement this method here and in CaptureCallback
    //        @Override
    //        public void onCaptureSequenceAborted(SCameraCaptureSession session, int sequenceId) {
    //          super.onCaptureSequenceAborted(session, sequenceId);
    //        }
  }

  static class VideonaSystemCaptureCallback extends CameraCaptureSession.CaptureCallback {

    private final CaptureCallback captureCallback;

    public VideonaSystemCaptureCallback(CaptureCallback captureCallback) {
      this.captureCallback = captureCallback;
    }

    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                   TotalCaptureResult result) {
      super.onCaptureCompleted(session, request, result);
      captureCallback.onCaptureCompleted(new VideonaCameraCaptureSession(session),
              new VideonaCaptureRequest(request),
              new VideonaCaptureResult(result));
    }
  }
}
