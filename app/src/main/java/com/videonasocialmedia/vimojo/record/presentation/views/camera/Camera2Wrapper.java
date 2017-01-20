package com.videonasocialmedia.vimojo.record.presentation.views.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.record.presentation.views.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.record.presentation.views.recorder.MediaRecorderWrapper;
import com.videonasocialmedia.vimojo.record.presentation.views.util.RecordCamera2Utils;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * Created by alvaro on 18/01/17.
 */

public class Camera2Wrapper implements TextureView.SurfaceTextureListener {

  private final String LOG_TAG = getClass().getSimpleName();


  private final Context context;
  private final Camera2WrapperListener listener;

  /**
   * A refernce to the opened {@link android.hardware.camera2.CameraDevice}.
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

  public Camera2Wrapper(Context context, Camera2WrapperListener listener, int cameraIdSelected,
                        AutoFitTextureView textureView){
    this.context = context;
    this.cameraIdSelected = cameraIdSelected;
    this.textureView = textureView;
    this.listener = listener;
  }

  public void onResume() {
    startBackgroundThread();
    if (textureView.isAvailable()) {
      openCamera(textureView.getWidth(), textureView.getHeight());
    } else {
      textureView.setSurfaceTextureListener(this);
    }
  }

  public void onPause() {
    if(isRecordingVideo)
      stopRecordingVideo();
    closeCamera();
    stopBackgroundThread();
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
    if (null == activity || activity.isFinishing()) {
      return;
    }
    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try {
      Log.d(LOG_TAG, "tryAcquire");
      if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
        throw new RuntimeException("Time out waiting to lock camera opening.");
      }

      String cameraId = manager.getCameraIdList()[cameraIdSelected];

      // Choose the sizes for camera preview and video isRecording
      characteristics = manager.getCameraCharacteristics(cameraId);
      listener.setFlashSupport();
      StreamConfigurationMap map = characteristics
          .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

      videoSize = RecordCamera2Utils.chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
      previewSize = RecordCamera2Utils.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
          width, height, videoSize);

      Log.d(LOG_TAG, "VideoSize " + videoSize.getWidth() + " x " + videoSize.getHeight());
      Log.d(LOG_TAG, "PreviewSize " + previewSize.getWidth() + " x " + previewSize.getHeight());


      int orientation = context.getResources().getConfiguration().orientation;
      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
      } else {
        textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
      }
      configureTransform(width, height);
      int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
      
      mediaRecorder = new MediaRecorderWrapper(new MediaRecorder(), sensorOrientation, rotation,
          videoPath);
      if (ActivityCompat.checkSelfPermission(VimojoApplication.getAppContext(),
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
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera opening.");
    }
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
      return;
    }
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
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
              previewSession = cameraCaptureSession;
              updatePreview();
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


  /**
   * Update the camera preview. {@link #startPreview()} needs to be called in advance.
   */
  private void updatePreview() {
    if (null == cameraDevice) {
      return;
    }
    try {
      setUpCaptureRequestBuilder(previewBuilder);
      HandlerThread thread = new HandlerThread("CameraPreview");
      thread.start();
      previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
    builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
  }

  private void closePreviewSession() {
    if (previewSession != null) {
      previewSession.close();
      previewSession = null;
    }
  }

  public void startRecordingVideo() {
    Log.d(LOG_TAG, "startRecordingVideo");
    if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
      return;
    }
    try {
      closePreviewSession();
      mediaRecorder.setUpMediaRecorder();
      SurfaceTexture texture = textureView.getSurfaceTexture();
      assert texture != null;
      texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
      previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
      List<Surface> surfaces = new ArrayList<>();

      // Set up Surface for the camera preview
      Surface previewSurface = new Surface(texture);
      surfaces.add(previewSurface);
      previewBuilder.addTarget(previewSurface);

      // Set up Surface for the MediaRecorder
      recorderSurface = mediaRecorder.getSurface();
      surfaces.add(recorderSurface);
      previewBuilder.addTarget(recorderSurface);

      // Start a capture session
      // Once the session starts, we can update the UI and start recording
      cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
          previewSession = cameraCaptureSession;
          updatePreview();
          ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
              isRecordingVideo = true;
              // Start recording
              mediaRecorder.start();
            }
          });
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
          Activity activity = (Activity) context;
          if (null != activity) {
            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
          }
        }
      }, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void stopRecordingVideo() {
    Log.d(LOG_TAG, "stopRecordingVideo");
    isRecordingVideo = false;

    // Stop recording
    mediaRecorder.stop();
    mediaRecorder.reset();

    listener.videoRecorded(videoPath);
   // startPreview();
  }

  public boolean isFlashSupported() {
    return characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
  }

  public void setFlashOff() {
    previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
    try {
      previewSession.setRepeatingRequest(previewBuilder.build(),null,null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  public void setFlashOn() {
    previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
    try {
      previewSession.setRepeatingRequest(previewBuilder.build(),null,null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private String getVideoFilePath() {
    // TODO:(alvaro.martinez) 19/01/17 Get this path from Project ¿?
    videoPath = Constants.PATH_APP_MASTERS + "/"
        + System.currentTimeMillis() + ".mp4";

    return videoPath;
  }
}
