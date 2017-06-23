package com.videonasocialmedia.camera.camera2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.camera.recorder.MediaRecorderWrapper;
import com.videonasocialmedia.camera.utils.Camera2Utils;
import com.videonasocialmedia.camera.utils.VideoCameraFormat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


/**
 * Created by alvaro on 18/01/17.
 */

public class Camera2Wrapper implements TextureView.SurfaceTextureListener {
  public static final int CAMERA_ID_REAR = 0;
  public static final int CAMERA_ID_FRONT = 1;
  private final String LOG_TAG = getClass().getSimpleName();

  private final Camera2WrapperListener listener;
  private final Context context;
  private final String directorySaveVideos;
  private final Camera2ZoomHelper camera2ZoomHelper;
  private final Camera2FocusHelper camera2FocusHelper;
  private final Camera2ISOHelper camera2ISOHelper;
  private final Camera2WhiteBalanceHelper camera2WhiteBalanceHelper;
  private final Camera2MeteringModeHelper camera2MeteringModeHelper;

  /**
   * A reference to the opened {@link android.hardware.camera2.CameraDevice}.
   */
  private CameraDevice cameraDevice;
  /**
   * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for
   * preview.
   */
  private CameraCaptureSession previewSession;
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
  private CaptureRequest.Builder previewBuilder;
  private Surface recorderSurface;

  AutoFitTextureView textureView;

  private VideoCameraFormat videoCameraFormat;

  private int rotation;

  /**
   * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
   */
  private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

    @Override
    public void onOpened(CameraDevice cameraDevice) {
      Camera2Wrapper.this.cameraDevice = cameraDevice;
      startPreview();
      cameraOpenCloseLock.release();
      if (null != textureView) {
        configureTransform(textureView.getWidth(), textureView.getHeight());
      }
    }

    @Override
    public void onDisconnected(CameraDevice cameraDevice) {
      cameraOpenCloseLock.release();
      cameraDevice.close();
      Camera2Wrapper.this.cameraDevice = null;
    }

    @Override
    public void onError(CameraDevice cameraDevice, int error) {
      cameraOpenCloseLock.release();
      cameraDevice.close();
      Camera2Wrapper.this.cameraDevice = null;
      Activity activity = (Activity) context;
      if (null != activity) {
        activity.finish();
      }
    }
  };
  private int cameraIdSelected;
  private CameraCharacteristics characteristics;
  private String videoPath;
  private CameraManager manager;
  private boolean isFlashActivated;
  private boolean initializingRecorder = false;

  private int sensorArrayRight = 0;
  private int sensorArrayBottom = 0;

  public Camera2Wrapper(Context context, Camera2WrapperListener listener, int cameraIdSelected,
                        AutoFitTextureView textureView, String directorySaveVideos,
                        VideoCameraFormat videoCameraFormat) {
    this.context = context;
    this.listener = listener;
    this.cameraIdSelected = cameraIdSelected;
    this.textureView = textureView;
    this.directorySaveVideos = directorySaveVideos;
    this.videoCameraFormat = videoCameraFormat;
    getCameraManager(); // (jliarte): 19/06/17 manager is needed to ask for camera characteristics used in helpers
    setupSensorParams();
    // TODO(jliarte): 26/05/17 inject the components
    camera2ZoomHelper = new Camera2ZoomHelper(this);
    camera2FocusHelper = new Camera2FocusHelper(this);
    camera2ISOHelper = new Camera2ISOHelper(this);
    camera2WhiteBalanceHelper = new Camera2WhiteBalanceHelper(this);
    camera2MeteringModeHelper = new Camera2MeteringModeHelper(this);
  }

  private String getCameraId() throws CameraAccessException {
    return manager.getCameraIdList()[this.cameraIdSelected];
  }

  CameraCharacteristics getCurrentCameraCharacteristics() throws CameraAccessException {
    return manager.getCameraCharacteristics(getCameraId());
  }

  private void setupSensorParams() {
    try {
      Rect sensorActiveArray = getCurrentCameraCharacteristics().
              get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
      sensorArrayRight = sensorActiveArray.right;
      sensorArrayBottom = sensorActiveArray.bottom;

      Size pixelSize = getCurrentCameraCharacteristics()
              .get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
      Log.i(TAG, "cameraCharacteristics,,,,pixelSize.getWidth()--->" + pixelSize.getWidth()
              + ",,,pixelSize.getHeight()--->" + pixelSize.getHeight());
    } catch (CameraAccessException e) {
      Log.e(TAG, "failed to get camera characteristics");
      Log.e(TAG, "reason: " + e.getReason());
      Log.e(TAG, "message: " + e.getMessage());
    }
  }

  public CaptureRequest.Builder getPreviewBuilder() {
    return previewBuilder;
  }

  public CameraCaptureSession getPreviewSession() {
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
    final Activity activity = (Activity) context;
    manager = (CameraManager) activity.getSystemService(context.CAMERA_SERVICE);
  }

  public void onPause() {
    if(isRecordingVideo) {
      stopRecordVideo();
    }
    closeCamera();
    stopBackgroundThread();
  }

  private void checkTextureViewToOpenCamera() {
    if (textureView.isAvailable()) {
      openCamera(textureView.getWidth(), textureView.getHeight());
    } else {
      textureView.setSurfaceTextureListener(this);
    }
  }

  public void reStartPreview(){
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
      mediaRecorder.stop();
      isRecordingVideo = false;
    } catch (RuntimeException runtimeException) {
      Log.d(TAG, runtimeException.toString()+" - Caugth error stopping record");
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
    Log.d(LOG_TAG, "onSurfaceTextureAvailable " + width + " x " + height );
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    configureTransform(width, height);
    Log.d(LOG_TAG, "onSurfaceTextureSizeChanged " + width + " x " + height );
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {

  }

  /**
   * Tries to open a {@link CameraDevice}. The result is listened by `stateCallback`.
   */
  private void openCamera(int width, int height) {
    final Activity activity = (Activity) context;
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
     if (ActivityCompat.checkSelfPermission(context,
          android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
      }
      manager.openCamera(cameraId, stateCallback, null);
    } catch (CameraAccessException e) {
      Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
      activity.finish();
    } catch (NullPointerException e) {
      // Currently an NPE is thrown when the Camera2API is used but not supported on the
      // device this code runs.
      // TODO:(alvaro.martinez) 17/01/17 Manage this NPE
      // ErrorDialog.newInstance("error dialog")
      //   .show(getChildFragmentManager(), FRAGMENT_DIALOG);
      //listener.setError("Camera2API is used but not supported on the device");
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
    Activity activity = (Activity) context;
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
    if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
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
          new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession cameraCaptureSession) {
              setupPreviewSession(cameraCaptureSession);
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
              Activity activity = (Activity) context;
              if (null != activity) {
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
              }
            }
          }, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private void setupPreviewSession(CameraCaptureSession cameraCaptureSession) {
    previewSession = cameraCaptureSession;
    updatePreview();
    camera2WhiteBalanceHelper.setCurrentWhiteBalanceMode();
    camera2MeteringModeHelper.setCurrentMeteringMode();
    setCurrentFlashSettings();
  }

  /**
   * Update the camera preview. {@link #startPreview()} needs to be called in advance.
   */
  public void updatePreview() {
    if (cameraDevice == null) {
      return;
    }
    try {
      setUpCaptureRequestBuilder(previewBuilder);
      previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    } catch (NullPointerException nullPreviewSession) {
      Log.e(TAG, "Preview session becomes null!!", nullPreviewSession);
    }
  }

  private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
    builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
  }

  private void closePreviewSession() {
    Log.d(TAG, "---------------------- close preview session -----------------");
    if (previewSession != null) {
      previewSession.close();
      previewSession = null;
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
      cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
          setupPreviewSession(cameraCaptureSession);
          startRecordingSession(callback);
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
          Activity activity = (Activity) context;
          if (null != activity) {
            Toast.makeText(activity, "Failed to start recording.", Toast.LENGTH_SHORT).show();
          }
        }
      }, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void startRecordingSession(final RecordStartedCallback callback) {
    ((Activity) context).runOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Start recording
        try {
          mediaRecorder.start();
          isRecordingVideo = true;
          callback.onRecordStarted();
        } catch (IllegalStateException illegalStateError) {
          Log.d(TAG, "IllegalStateException - Caught error starting record");
        }
      }
    });
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
      Log.e(TAG, "-----------------------------------------------------------------------------");
//      checkTextureViewToOpenCamera();
      reStartPreview();
      return;
    }
    isFlashActivated = false;
    previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
    try {
      previewSession.setRepeatingRequest(previewBuilder.build(),null,null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  public void setFlashOn() {
    isFlashActivated = true;
    previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
    try {
      previewSession.setRepeatingRequest(previewBuilder.build(),null,null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
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
      Log.e(TAG, "Error zooming - camera access", e);
    }
  }

  public void seekBarZoom(float zoomValue) {
    try {
      camera2ZoomHelper.seekBarZoom(zoomValue);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(TAG, "Error zooming - camera access", e);
    }
  }
  /********* end of Zoom component ********/

  /********* Focus component ********/
  public void setFocus(int x, int y) throws CameraAccessException {
    camera2FocusHelper.setFocus(x, y);
  }

  public boolean advancedFocusSupported() {
    return camera2FocusHelper.advancedFocusSupported();
  }
  /********* end of Focus component ********/

  public int getRotation() {
    if(rotation == Surface.ROTATION_270){
      if(sensorOrientation == 90) {
        return getInverseRotation();
      }else {
        return getNormalRotation();
      }
    } else {
      if(sensorOrientation == 90) {
        return getNormalRotation();
      } else {
        return getInverseRotation();
      }
    }
  }

  private int getInverseRotation() {
    if(cameraIdSelected == CAMERA_ID_REAR) {
      return 180;
    } else {
      return 0;
    }
  }

  private int getNormalRotation() {
    if(cameraIdSelected == CAMERA_ID_REAR) {
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
    checkTextureViewToOpenCamera();
  }

  public boolean ISOSelectionSupported() {
    return camera2ISOHelper.ISOSelectionSupported();
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

  public int getCurrentExposureCompensation() {
    return camera2MeteringModeHelper.getCurrentExposureCompensation();
  }

  public void setExposureCompensation(int exposureCompensation) {
    camera2MeteringModeHelper.setExposureCompensation(exposureCompensation);
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

  public MeteringRectangle[] getMeteringRectangles(int touchEventX, int touchEventY,
                                                   int viewWidth, int viewHeight, int areaSize) {
    int ll = ((touchEventX * sensorArrayRight) - areaSize) / viewWidth;
    int rr = ((touchEventY * sensorArrayBottom) - areaSize) / viewHeight;
    int focusAreaLeft = clamp(ll, 0, sensorArrayRight);
    int focusAreaBottom = clamp(rr, 0, sensorArrayBottom);
    Rect newRect = new Rect(focusAreaLeft, focusAreaBottom, focusAreaLeft + areaSize, focusAreaBottom + areaSize);
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


  public interface RecordStartedCallback {
    void onRecordStarted();
  }
}
