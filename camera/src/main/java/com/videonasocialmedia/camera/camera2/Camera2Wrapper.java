package com.videonasocialmedia.camera.camera2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.camera.SCamera;
import com.samsung.android.sdk.camera.SCameraManager;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCaptureSession;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCharacteristics;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraDevice;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraManager;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureRequest;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureResult;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.camera.recorder.MediaRecorderWrapper;
import com.videonasocialmedia.camera.utils.Camera2Utils;
import com.videonasocialmedia.camera.utils.VideoCameraFormat;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by alvaro on 18/01/17.
 */

public class Camera2Wrapper implements TextureView.SurfaceTextureListener {
  public static final int CAMERA_ID_REAR = 0;
  public static final int CAMERA_ID_FRONT = 1;
  private final String LOG_TAG = getClass().getSimpleName();

  private Camera2WrapperListener listener;
  private final WeakReference<Context> contextWeakReference;
  private final String directorySaveVideos;
  private final Camera2ZoomHelper camera2ZoomHelper;
  private final Camera2FocusHelper camera2FocusHelper;
  private final Camera2ISOHelper camera2ISOHelper;
  private final Camera2WhiteBalanceHelper camera2WhiteBalanceHelper;
  private final Camera2MeteringModeHelper camera2MeteringModeHelper;

  /**
   * A reference to the opened {@link VideonaCameraDevice}.
   */
  private VideonaCameraDevice cameraDevice;
  /**
   * A reference to the current {@link CameraCaptureSession} for
   * preview.
   */
  private VideonaCameraCaptureSession previewSession;
  /**
   * An additional thread for running tasks that shouldn't block the UI.
   */
  private HandlerThread backgroundThread;

  /**
   * A {@link Handler} for running tasks in the background.
   */
  private Handler backgroundHandler;

  /**
   * A {@link Semaphore} to prevent the app from exiting before closing the camera.
   */
  private Semaphore cameraOpenCloseLock = new Semaphore(1);

  /**
   * The {@link android.util.Size} of camera preview.
   */
  private Size previewSize;

  /**
   * The {@link android.util.Size} of video isRecording.
   */
  private Size videoSize;

  /**
   * MediaRecorderWrapper
   */
  private MediaRecorderWrapper mediaRecorder;

  /**
   * Whether the app is recording video now
   */
  private boolean isRecordingVideo;

  private Integer sensorOrientation;
  private VideonaCaptureRequest.Builder previewBuilder;
  private Surface recorderSurface;

  AutoFitTextureView textureView;

  private VideoCameraFormat videoCameraFormat;

  private int rotation;

  /**
   * {@link VideonaCameraDevice.StateCallback} is called when {@link CameraDevice}
   * changes its status.
   */
  private VideonaCameraDevice.StateCallback stateCallback =
          new VideonaCameraDevice.StateCallback() {
            @Override
            public void onOpened(VideonaCameraDevice cameraDevice) {
              Camera2Wrapper.this.cameraDevice = cameraDevice;
              startPreview();
              cameraOpenCloseLock.release();
              if (textureView != null) {
                configureTransform(textureView.getWidth(), textureView.getHeight());
              }
            }

            @Override
            public void onDisconnected(VideonaCameraDevice cameraDevice) {
              cameraOpenCloseLock.release();
              cameraDevice.close();
              Camera2Wrapper.this.cameraDevice = null;
            }

            @Override
            public void onError(VideonaCameraDevice cameraDevice, int error) {
              cameraOpenCloseLock.release();
              cameraDevice.close();
              Camera2Wrapper.this.cameraDevice = null;
              finishActivity();
            }

            private void finishActivity() {
              if (contextWeakReference.get() != null) {
                ((Activity) contextWeakReference.get()).finish();
              }
            }
          };
  private int cameraIdSelected;
  private VideonaCameraCharacteristics characteristics;
  private String videoPath;
  private VideonaCameraManager manager;
  private boolean isFlashActivated;
  private boolean initializingRecorder = false;

  private int sensorArrayWidth = 0;
  private int sensorArrayHeight = 0;
  private Rect sensorActiveArray;
  private SCamera sCamera;
  private SCameraManager sCameraManager;

  public Camera2Wrapper(Context context, int cameraIdSelected,
                        AutoFitTextureView textureView, String directorySaveVideos,
                        VideoCameraFormat videoCameraFormat) {
    contextWeakReference = new WeakReference<>(context);
    this.cameraIdSelected = cameraIdSelected;
    this.textureView = textureView;
    this.directorySaveVideos = directorySaveVideos;
    this.videoCameraFormat = videoCameraFormat;
    setupSamsungCamera();
    getCameraManager(); // (jliarte): 19/06/17 manager is needed to ask for camera characteristics used in helpers
    // TODO(jliarte): 26/05/17 inject the components
    camera2ZoomHelper = new Camera2ZoomHelper(this);
    camera2FocusHelper = new Camera2FocusHelper(this);
    camera2ISOHelper = new Camera2ISOHelper(this);
    camera2WhiteBalanceHelper = new Camera2WhiteBalanceHelper(this);
    camera2MeteringModeHelper = new Camera2MeteringModeHelper(this);

    setupCamera();
  }

  private void setupCamera() {
    setupSensorParams();
    camera2ZoomHelper.setup();
    camera2FocusHelper.setup();
    camera2ISOHelper.setup();
    camera2WhiteBalanceHelper.setup();
    camera2MeteringModeHelper.setup();
  }

  private void setupSamsungCamera() {
    if (contextWeakReference.get() != null) {
      sCamera = new SCamera();
      try {
        sCamera.initialize(contextWeakReference.get());
        sCameraManager = sCamera.getSCameraManager();
        manager = new VideonaCameraManager(sCameraManager);
        return;
      } catch (SsdkUnsupportedException sCameraInitError) {
        if (sCameraInitError.getType() == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED) {
          // The device is not a Samsung device.
        } else if (sCameraInitError.getType() == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
          // The device does not support Camera.
//      } else if (sCameraInitError.getType() == SsdkUnsupportedException.SDK_VERSION_MISMATCH) {
          // There is a SDK version mismatch.
        }
      }
    }
  }

  public void setCameraListener(Camera2WrapperListener camera2WrapperListener) {
    this.listener = camera2WrapperListener;
  }

  private String getCameraId() throws CameraAccessException {
    getCameraManager();
    return manager.getCameraIdList()[this.cameraIdSelected];
  }

  VideonaCameraCharacteristics getCurrentCameraCharacteristics() throws CameraAccessException {
    return manager.getCameraCharacteristics(getCameraId());
  }

  private void setupSensorParams() {
    try {
      sensorActiveArray = getCurrentCameraCharacteristics().
              get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
      sensorArrayWidth = sensorActiveArray.width();
      sensorArrayHeight = sensorActiveArray.height();

      Log.i(LOG_TAG, "Sensor active array size width: "
              + sensorArrayWidth + " height: " + sensorArrayHeight );
    } catch (CameraAccessException e) {
      logExceptionAccessingCameraCharacteristics(e);
    }
  }

  private void logExceptionAccessingCameraCharacteristics(CameraAccessException e) {
    Log.e(LOG_TAG, "failed to get camera characteristics");
    Log.e(LOG_TAG, "reason: " + e.getReason());
    Log.e(LOG_TAG, "message: " + e.getMessage());
  }

  public VideonaCaptureRequest.Builder getPreviewBuilder() {
    return previewBuilder;
  }

  public VideonaCameraCaptureSession getPreviewSession() {
    return previewSession;
  }

  public Handler getBackgroundHandler() {
    return backgroundHandler;
  }

  public void onResume() {
    startBackgroundThread();
    checkTextureViewToOpenCamera();
  }

  private void getCameraManager() {
    if (manager != null) {
      return;
    }
    if (contextWeakReference.get() != null) {
      final Activity activity = (Activity) contextWeakReference.get();
      if (sCameraManager != null) {
        manager = new VideonaCameraManager(sCameraManager);
      } else {
        manager = new VideonaCameraManager
                ((CameraManager) activity.getSystemService(activity.CAMERA_SERVICE));
      }
    }
  }

  public void onPause() {
    if (isRecordingVideo) {
      stopRecordVideo();
    }
    closeCamera();
    // TODO(jliarte): 16/10/17 consider including in closCamera method
    manager = null;
    stopBackgroundThread();
  }

  private void checkTextureViewToOpenCamera() {
    if (textureView.isAvailable()) {
      openCamera(textureView.getWidth(), textureView.getHeight());
    } else {
      textureView.setSurfaceTextureListener(this);
    }
  }

  public void reStartPreview() {
    closeCamera();
    reStartBackgroundThread();
    checkTextureViewToOpenCamera();
  }

  private void reStartBackgroundThread() {
    stopBackgroundThread();
    startBackgroundThread();
  }

  public void stopRecordVideo() {
    try {
      try {
        // (jliarte): 26/07/17 fix for some Android 5.x devices as seen in
        // https://stackoverflow.com/a/35739021
        previewSession.stopRepeating();
        previewSession.abortCaptures();
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
      mediaRecorder.stop();
      isRecordingVideo = false;
    } catch (RuntimeException runtimeException) {
      Log.d(LOG_TAG, runtimeException.toString() + " - Caugth error stopping record");
      throw runtimeException;
    }
  }

  /**
   * Starts a background thread and its {@link Handler}.
   */
  private void startBackgroundThread() {
    backgroundThread = new HandlerThread("CameraBackground");
    backgroundThread.start();
    backgroundHandler = new Handler(backgroundThread.getLooper());
  }

  /**
   * Stops the background thread and its {@link Handler}.
   */
  private void stopBackgroundThread() {
    backgroundThread.quitSafely();
    try {
      backgroundThread.join();
      backgroundThread = null;
      backgroundThread = null;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    openCamera(width, height);
    Log.d(LOG_TAG, "onSurfaceTextureAvailable " + width + " x " + height);
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    configureTransform(width, height);
    Log.d(LOG_TAG, "onSurfaceTextureSizeChanged " + width + " x " + height);
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {

  }

  /**
   * Tries to open a {@link VideonaCameraDevice}. The result is listened by `stateCallback`.
   */
  private void openCamera(int width, int height) {
    final Activity activity = (Activity) contextWeakReference.get();
    if (activity == null || activity.isFinishing()) {
      return;
    }
    try {
      Log.d(LOG_TAG, "tryAcquire");
      if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
        throw new RuntimeException("Time out waiting to lock camera opening.");
      }

      String cameraId = getCameraId();

      // Choose the sizes for camera preview and video isRecording
      characteristics = getCurrentCameraCharacteristics();
      listener.setFlashSupport();
      StreamConfigurationMap map = characteristics
              .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

      videoSize = Camera2Utils.chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
      previewSize = Camera2Utils.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
              width, height, videoSize);

      Log.d(LOG_TAG, "VideoSize " + videoSize.getWidth() + " x " + videoSize.getHeight());
      Log.d(LOG_TAG, "PreviewSize " + previewSize.getWidth() + " x " + previewSize.getHeight());

      int orientation = getActivityOrientation(activity);
      Log.d(LOG_TAG, "orientation " + orientation);

      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
      } else {
        textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
      }
      configureTransform(width, height);
      Log.d(LOG_TAG, "Rotation " + rotation + " cameraId " + cameraIdSelected +
              " sensorOrientation " + sensorOrientation);

      mediaRecorder = new MediaRecorderWrapper(new MediaRecorder(), cameraIdSelected,
              sensorOrientation, rotation, createVideoFilePath(), videoCameraFormat);
      manager.openCamera(cameraId, stateCallback, null, contextWeakReference);
    } catch (CameraAccessException e) {
      Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
      activity.finish();
    } catch (NullPointerException npeOpeningCamera) {
      Crashlytics.log("Error opening camera!");
      Crashlytics.logException(npeOpeningCamera);
      npeOpeningCamera.printStackTrace();
      throw npeOpeningCamera;
      // Currently an NPE is thrown when the Camera2API is used but not supported on the
      // device this code runs.
      // TODO:(alvaro.martinez) 17/01/17 Manage this NPE
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera opening.");
    }
  }

  private int getActivityOrientation(Activity activity) {
    return activity.getResources().getConfiguration().orientation;
  }

  private void closeCamera() {
    try {
      cameraOpenCloseLock.acquire();
      closePreviewSession();
      if (null != cameraDevice) {
        cameraDevice.close();
        cameraDevice = null;
      }
      if (null != mediaRecorder) {
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
      }
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera closing.");
    } finally {
      cameraOpenCloseLock.release();
    }
  }


  /**
   * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
   * This method should not to be called until the camera preview size is determined in
   * openCamera, or until the size of `mTextureView` is fixed.
   *
   * @param viewWidth  The width of `mTextureView`
   * @param viewHeight The height of `mTextureView`
   */
  private void configureTransform(int viewWidth, int viewHeight) {
    Activity activity = (Activity) contextWeakReference.get();
    if (null == textureView || null == previewSize || null == activity) {
      Log.d(LOG_TAG, "configureTransform, null textureView, previewSize, activity");
      return;
    }
    rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    Matrix matrix = new Matrix();
    RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
    RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
    float centerX = viewRect.centerX();
    float centerY = viewRect.centerY();
    if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
      bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
      matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
      float scale = Math.max(
              (float) viewHeight / previewSize.getHeight(),
              (float) viewWidth / previewSize.getWidth());
      matrix.postScale(scale, scale, centerX, centerY);
      matrix.postRotate(90 * (rotation - 2), centerX, centerY);
    }
    textureView.setTransform(matrix);
  }

  /**
   * Start the camera preview.
   */
  private void startPreview() {
    if (cameraDevice == null || !textureView.isAvailable() || previewSize == null) {
      return;
    }
    try {
      closePreviewSession();
      SurfaceTexture texture = textureView.getSurfaceTexture();
      assert texture != null;
      texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
      previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

      Surface previewSurface = new Surface(texture);
      previewBuilder.addTarget(previewSurface);

      cameraDevice.createCaptureSession(Arrays.asList(previewSurface),
              new VideonaCameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(VideonaCameraCaptureSession cameraCaptureSession) {
                  setupPreviewSession(cameraCaptureSession);
                }

                @Override
                public void onConfigureFailed(VideonaCameraCaptureSession cameraCaptureSession) {
                  Activity activity = (Activity) contextWeakReference.get();
                  if (activity != null) {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                  }
                }
              }, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private void setupPreviewSession(VideonaCameraCaptureSession cameraCaptureSession) {
    previewSession = cameraCaptureSession;
    camera2WhiteBalanceHelper.setCurrentWhiteBalanceMode();
    camera2MeteringModeHelper.setCurrentMeteringMode();
    camera2ISOHelper.setCurrentISOValue();
    try {
      camera2ZoomHelper.setCurrentZoom();
    } catch (CameraAccessException e) {
      logExceptionAccessingCameraCharacteristics(e);
    }
    setCurrentFlashSettings();
    camera2FocusHelper.setCurrentFocusSelectionMode();
    updatePreview();
  }

  /**
   * Update the camera preview. {@link #startPreview()} needs to be called in advance.
   */
  public void updatePreview() {
    if (cameraDevice == null || previewSession == null) {
      return;
    }
    try {

      // setUpCaptureRequestBuilderAutoMode(previewBuilder);
      // TODO(jliarte): 28/06/17 check if we can change frame rate with this.
      //                Tested on M5 and working at 25FPS.
      //                Seems not to crash if camera doesnt support FPS, it aproximates to next available FPS setting
      //getPreviewBuilder().set(CaptureRequest.SENSOR_FRAME_DURATION, Long.valueOf(40000000));
      Range<Integer> fpsRange = new Range<>(videoCameraFormat.getFrameRate(),
          videoCameraFormat.getFrameRate());
      this.getPreviewBuilder().set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange);
      previewSession.setRepeatingRequest(previewBuilder.build(), previewCaptureCallback,
              backgroundHandler);
      Log.d(LOG_TAG, "Updated preview");
    } catch (CameraAccessException e) {
      e.printStackTrace();
    } catch (IllegalStateException illegalStateError) {
      Log.e(LOG_TAG, "Illegal state updating preview", illegalStateError);
      Crashlytics.log("Illegal state updating preview");
      Crashlytics.logException(illegalStateError);
    } catch (NullPointerException nullPreviewSession) {
      Log.e(LOG_TAG, "Preview session becomes null!!", nullPreviewSession);
    }
  }

  void setUpCaptureRequestBuilderAutoMode(VideonaCaptureRequest.Builder builder) {
    builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
  }

  private void closePreviewSession() {
    Log.d(LOG_TAG, "---------------------- close preview session -----------------");
    if (previewSession != null) {
      try {
        previewSession.close();
        previewSession = null;
      } catch (Exception errorCLosingPreview) {
        Log.e(LOG_TAG, "failed to close preview");
        Log.e(LOG_TAG, "message: " + errorCLosingPreview.getMessage());
      }
    }
  }

  public void startRecordingVideo(final RecordStartedCallback callback) {
    Log.d(LOG_TAG, "startRecordingVideo");
    if (initializingRecorder) {
      // (jliarte): 16/06/17 workarround to prevent startRecording been called consecutively
      return;
    }
    initializingRecorder = true;
    if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
      return;
    }
    try {
      closePreviewSession();
      mediaRecorder.setUpMediaRecorder();
      //mediaRecorder.setUpCameraProfileMediaRecoder();

      SurfaceTexture texture = textureView.getSurfaceTexture();
      // TODO(jliarte): 14/06/17 this will crash the app if texture is null???
      assert texture != null;
      texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
      previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
      List<Surface> surfaces = new ArrayList<>();

      // Set up Surface for the camera preview
      Surface previewSurface = new Surface(texture);
      surfaces.add(previewSurface);
      previewBuilder.addTarget(previewSurface);

      // TODO(jliarte): 20/06/17 trying new approach to use the same surface and capture session that we already have
//      mediaRecorder.setSurface(previewSurface);
//      setupPreviewSession(previewSession);
//      startRecordingSession(callback);

      // Set up Surface for the MediaRecorder
      recorderSurface = mediaRecorder.getSurface();
      surfaces.add(recorderSurface);
      previewBuilder.addTarget(recorderSurface);

      // Start a capture session
      // Once the session starts, we can update the UI and start recording
      cameraDevice.createCaptureSession(surfaces, new VideonaCameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull VideonaCameraCaptureSession cameraCaptureSession) {
          initializingRecorder = false;
          setupPreviewSession(cameraCaptureSession);
          startRecordingSession(callback);
        }

        @Override
        public void onConfigureFailed(@NonNull VideonaCameraCaptureSession cameraCaptureSession) {
          Activity activity = (Activity) contextWeakReference.get();
          if (activity != null) {
            Toast.makeText(activity, "Failed to start recording.", Toast.LENGTH_SHORT).show();
          }
          initializingRecorder = false;
        }
      }, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      initializingRecorder = false;
    }
  }

  public int getMaxAmplitudeRecording() {
    if (isMediaRecorderPrepared()) {
      return mediaRecorder.getMaxAmplitude();
    } else {
      return 0;
    }
  }

  public boolean isMediaRecorderPrepared() {
    return mediaRecorder != null;
  }

  private void startRecordingSession(final RecordStartedCallback callback) {
    if (contextWeakReference.get() != null) {
      ((Activity) contextWeakReference.get()).runOnUiThread(new Runnable() {
        @Override
        public void run() {
          // Start recording
          try {
            mediaRecorder.start();
            isRecordingVideo = true;
            callback.onRecordStarted();
          } catch (IllegalStateException illegalStateError) {
            Log.d(LOG_TAG, "IllegalStateException - Caught error starting record");
          }
        }
      });
    }
  }

  private void setCurrentFlashSettings() {
    if (isFlashActivated) {
      setFlashOn();
    }
  }

  public boolean isFlashSupported() {
    return characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
  }

  public void setFlashOff() {
    if (previewSession == null) {
      reStartPreview();
      return;
    }
    isFlashActivated = false;
    previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
    updatePreview();
  }

  public void setFlashOn() {
    isFlashActivated = true;
    previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
    updatePreview();
  }

  private String createVideoFilePath() {
    // TODO:(alvaro.martinez) 19/01/17 Get pattern VID_ from where?
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "VID_" + timeStamp + ".mp4";
    videoPath = directorySaveVideos + File.separator + fileName;

    return videoPath;
  }

  public String getVideoPath() {
    return videoPath;
  }

  public boolean isRecordingVideo() {
    return isRecordingVideo;
  }

  /********* Zoom component ********/
  public void onTouchZoom(float current_finger_spacing) {
    try {
      float zoomValue = camera2ZoomHelper.onTouchZoom(current_finger_spacing);
      listener.setZoom(zoomValue);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "Error zooming - camera access", e);
    }
  }

  public void seekBarZoom(float zoomValue) {
    try {
      camera2ZoomHelper.seekBarZoom(zoomValue);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "Error zooming - camera access", e);
    }
  }

  /********* end of Zoom component ********/

  public int getRotation() {
    if (rotation == Surface.ROTATION_270) {
      if (sensorOrientation == 90) {
        return getInverseRotation();
      } else {
        return getNormalRotation();
      }
    } else {
      if (sensorOrientation == 90) {
        return getNormalRotation();
      } else {
        return getInverseRotation();
      }
    }
  }

  private int getInverseRotation() {
    if (cameraIdSelected == CAMERA_ID_REAR) {
      return 180;
    } else {
      return 0;
    }
  }

  private int getNormalRotation() {
    if (cameraIdSelected == CAMERA_ID_REAR) {
      return 0;
    } else {
      return 180;
    }
  }

  public void switchCamera(boolean isFrontCameraSelected) {
    if (isFrontCameraSelected) {
      cameraIdSelected = CAMERA_ID_FRONT;
    } else {
      cameraIdSelected = CAMERA_ID_REAR;
    }
    closeCamera();
    setupCamera();
    checkTextureViewToOpenCamera();
  }

  public void resetZoom() {
    try {
      camera2ZoomHelper.resetZoom();
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "Error reset zooming - camera access", e);
    }
  }

  public boolean ISOSelectionSupported() {
    return camera2ISOHelper.ISOSelectionSupported();
  }

  public Integer getMaximumSensitivity() {
    return camera2ISOHelper.getMaximumSensitivity();
  }

  public Range<Integer> getSupportedISORange() {
    return camera2ISOHelper.getSupportedISORange();
  }

  public void setISO(Integer isoValue) {
    camera2ISOHelper.setISO(isoValue);
  }

  public boolean whiteBalanceSelectionSupported() {
    return camera2WhiteBalanceHelper.whiteBalanceSelectionSupported();
  }

  public CameraFeatures.SupportedValues getSupportedWhiteBalanceModes() {
    return camera2WhiteBalanceHelper.getSupportedWhiteBalanceModes();
  }

  public boolean metteringModeSelectionSupported() {
    return camera2MeteringModeHelper.metteringModeSelectionSupported();
  }

  public void setWhiteBalanceMode(String whiteBalanceMode) {
    camera2WhiteBalanceHelper.setWhiteBalanceMode(whiteBalanceMode);
  }

  public void resetWhiteBalanceMode() {
    camera2WhiteBalanceHelper.resetWhiteBalanceMode();
  }

  public int getMinimumExposureCompensation() {
    return camera2MeteringModeHelper.getMinimumExposureCompensation();
  }

  public int getMaximumExposureCompensation() {
    return camera2MeteringModeHelper.getMaximumExposureCompensation();
  }

  public float getExposureCompensationStep() {
    return camera2MeteringModeHelper.getExposureCompensationStep();
  }

  public int getCurrentExposureCompensation() {
    return camera2MeteringModeHelper.getCurrentExposureCompensation();
  }

  public void setExposureCompensation(int exposureCompensation) {
    camera2MeteringModeHelper.setExposureCompensation(exposureCompensation);
  }

  public void setFocusSelectionMode(String focusSelectionMode) {
    camera2FocusHelper.setFocusSelectionMode(focusSelectionMode);
  }

  public void resetFocusSelectionMode() {
    camera2FocusHelper.resetFocusSelectionMode();
  }

  public boolean focusSelectionSupported() {
    return camera2FocusHelper.isFocusSelectionSupported();
  }

  public CameraFeatures.SupportedValues getSupportedFocusSelectionModes() {
    return camera2FocusHelper.getSupportedFocusSelectionModes();
  }

  public CameraFeatures.SupportedValues getSupportedMeteringModes() {
    return camera2MeteringModeHelper.getSupportedMeteringModes();
  }

  public void resetMeteringMode() {
    camera2MeteringModeHelper.resetMeteringMode();
  }

  public void setMeteringPoint(int touchEventX, int touchEventY, int viewWidth, int viewHeight) {
    camera2MeteringModeHelper.setMeteringPoint(touchEventX, touchEventY, viewWidth, viewHeight);
  }

  public void setFocusModeSelective(int touchEventX, int touchEventY,
                                    int viewWidth, int viewHeight) {
    camera2FocusHelper.setFocusModeRegion(touchEventX, touchEventY, viewWidth, viewHeight);
  }


  private void logRect(Rect rect, String message) {
    Log.e(LOG_TAG, message + " Rect coords l: " + rect.left + " r: " + rect.right
            + " t: " + rect.top
            + " b: " + rect.bottom);
  }

  public MeteringRectangle[] getMeteringRectangles(int touchEventX, int touchEventY,
                                                   int viewWidth, int viewHeight, int areaSize) {
    int ll = ((touchEventX * sensorArrayWidth) - areaSize) / viewWidth;
    int rr = ((touchEventY * sensorArrayHeight) - areaSize) / viewHeight;
    int focusAreaLeft = clamp(ll, 1, sensorArrayWidth - 1);
    int focusAreaBottom = clamp(rr, 1, sensorArrayHeight - 2);
    Rect newRect = new Rect(focusAreaLeft + 1, focusAreaBottom, focusAreaLeft + areaSize + 1,
            focusAreaBottom + areaSize - 2);
    logRect(newRect, "metering");
    MeteringRectangle meteringRectangle = new MeteringRectangle(newRect, 500);
    MeteringRectangle[] meteringRectangleArr = {meteringRectangle};
    return meteringRectangleArr;
  }

  public MeteringRectangle[] getFullSensorAreaMeteringRectangle() {
    Rect newRect = new Rect(sensorActiveArray.left + 1, sensorActiveArray.top + 1,
            sensorActiveArray.right - 2, sensorActiveArray.bottom - 2);
    MeteringRectangle meteringRectangle = new MeteringRectangle(newRect, 500);
    MeteringRectangle[] meteringRectangleArr = {meteringRectangle};
    return meteringRectangleArr;
  }

  private int clamp(int x, int min, int max) {
    if (x < min) {
      return min;
    } else if (x > max) {
      return max;
    } else {
      return x;
    }
  }

  public void setFocusModeManual(int seekbarProgress) {
    camera2FocusHelper.setFocusModeManual(seekbarProgress);
  }

  public interface RecordStartedCallback {
    void onRecordStarted();
  }

  public class CaptureResultSettings {
    boolean captureResultHasIso;
    Integer captureResultIso;
    boolean captureResultHasExposureTime;
    Long captureResultExposureTime;
    private boolean captureResultHasFrameDuration;
    private Long captureResultFrameDuration;
    private boolean captureResultHasLensAperture;
    private Float captureResultLensAperture;
    private boolean captureResultHasFocalLength;
    private Float captureResultFocalLength;
    public boolean captureResultHasFocusDistance;
    public Float captureResultFocusDistance;
  }

  private CaptureResultSettings captureResultSettings = new CaptureResultSettings();

  public CaptureResultSettings getCaptureResultSettings() {
    return captureResultSettings;
  }

  private final VideonaCameraCaptureSession.CaptureCallback previewCaptureCallback =
          new VideonaCameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(@NonNull VideonaCameraCaptureSession session,
                                           @NonNull VideonaCaptureRequest request,
                                           @NonNull VideonaCaptureResult result) {
              processCompleted(request, result);
              // TODO(jliarte): 14/11/17 check comment this is safe
//              super.onCaptureCompleted(session, request, result); // API docs say this does nothing, but call it just to be safe (as with Google Camera)
            }

            /** Processes a total result.
             */
            private void processCompleted(VideonaCaptureRequest request, VideonaCaptureResult result) {
              if (result.get(CaptureResult.SENSOR_SENSITIVITY) != null) {
                captureResultSettings.captureResultHasIso = true;
                captureResultSettings.captureResultIso = result.get(CaptureResult.SENSOR_SENSITIVITY);
//        Log.d(LOG_TAG, "Capture result iso: " + captureResultSettings.captureResultIso);
              } else {
                captureResultSettings.captureResultHasIso = false;
              }
//      Log.d (LOG_TAG, "ae state: " + result.get(CaptureResult.CONTROL_AE_STATE));
              if (result.get(CaptureResult.SENSOR_EXPOSURE_TIME) != null) {
                captureResultSettings.captureResultHasExposureTime = true;
                captureResultSettings.captureResultExposureTime =
                        result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
//        Log.d(LOG_TAG, "Capture result exposure time: "
//                + captureResultSettings.captureResultExposureTime);
              } else {
                captureResultSettings.captureResultHasExposureTime = false;
              }
              if (result.get(CaptureResult.SENSOR_FRAME_DURATION) != null) {
                captureResultSettings.captureResultHasFrameDuration = true;
                captureResultSettings.captureResultFrameDuration =
                        result.get(CaptureResult.SENSOR_FRAME_DURATION);
//        Log.d(LOG_TAG, "Capture result frame duration: "
//                + captureResultSettings.captureResultFrameDuration);
              } else {
                captureResultSettings.captureResultHasFrameDuration = false;
              }

              if (result.get(CaptureResult.LENS_APERTURE) != null) {
                captureResultSettings.captureResultHasLensAperture = true;
                captureResultSettings.captureResultLensAperture =
                        result.get(CaptureResult.LENS_APERTURE);
//        Log.d(LOG_TAG, "Capture result lens aperture: "
//                + captureResultSettings.captureResultLensAperture);
              } else {
                captureResultSettings.captureResultHasLensAperture = false;
              }
              if (result.get(CaptureResult.LENS_FOCAL_LENGTH) != null) {
                captureResultSettings.captureResultHasFocalLength = true;
                captureResultSettings.captureResultFocalLength =
                        result.get(CaptureResult.LENS_FOCAL_LENGTH);
//        Log.d(LOG_TAG, "Capture result focal lenght: "
//                + captureResultSettings.captureResultFocalLength);
              } else {
                captureResultSettings.captureResultHasFocalLength = false;
              }
              if (result.get(CaptureResult.LENS_FOCUS_DISTANCE) != null) {
                captureResultSettings.captureResultHasFocusDistance = true;
                captureResultSettings.captureResultFocusDistance =
                        result.get(CaptureResult.LENS_FOCUS_DISTANCE);
                Log.d(LOG_TAG, "Capture result focus distance: "
                        + captureResultSettings.captureResultFocusDistance);
              } else {
                captureResultSettings.captureResultHasFocusDistance = false;
              }
            }
          };

}
