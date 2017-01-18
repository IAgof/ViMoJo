package com.videonasocialmedia.vimojo.record.presentation.views.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.record.presentation.views.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.record.presentation.views.util.RecordCamera2Utils;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by alvaro on 16/01/17.
 */

public class RecordCamera2Activity extends VimojoActivity implements TextureView.SurfaceTextureListener {

  private final String LOG_TAG = getClass().getSimpleName();

  @Bind(R.id.button_settings_camera)
  ImageButton settingsCameraButton;
  @Bind(R.id.button_toggle_flash)
  ImageButton flashButton;
  @Bind(R.id.button_change_camera)
  ImageButton changeCameraButton;
  @Bind(R.id.button_to_show_controls)
  ImageButton showControlsButton;
  @Bind(R.id.button_to_hide_controls)
  ImageButton hideControlsViewButton;
  @Bind(R.id.button_navigate_settings)
  ImageButton navigateSettingsButtons;
  @Bind(R.id.chronometer_record)
  Chronometer chronometer;
  @Bind(R.id.text_view_num_videos)
  TextView numVideosRecordedTextView;
  @Bind(R.id.button_navigate_edit_or_gallery)
  CircleImageView thumbClipRecordedButton;

  /**
   * An {@link AutoFitTextureView} for camera preview.
   */
  @Bind(R.id.textureView)
  AutoFitTextureView textureView;

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
   * The {@link android.util.Size} of video recording.
   */
  private Size videoSize;

  /**
   * MediaRecorder
   */
  private MediaRecorder mediaRecorder;

  private Integer sensorOrientation;
  private CaptureRequest.Builder previewBuilder;

  /**
   * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
   */
  private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

    @Override
    public void onOpened(CameraDevice cameraDevice) {
      RecordCamera2Activity.this.cameraDevice = cameraDevice;
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
      RecordCamera2Activity.this.cameraDevice = null;
    }

    @Override
    public void onError(CameraDevice cameraDevice, int error) {
      cameraOpenCloseLock.release();
      cameraDevice.close();
      RecordCamera2Activity.this.cameraDevice = null;
      Activity activity = RecordCamera2Activity.this;
      if (null != activity) {
        activity.finish();
      }
    }

  };

  /**
   * if for result
   **/
  private String resultVideoPath;
  private boolean externalIntent = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   /* setContentView(R.layout.activity_record_camera2);
    if (null == savedInstanceState) {
      getFragmentManager().beginTransaction()
          .replace(R.id.container, RecordCamera2Fragment.newInstance())
          .commit();
    } */

    Log.d(LOG_TAG, "onCreate");
    setContentView(R.layout.activity_record_camera2);
    keepScreenOn();
    ButterKnife.bind(this);
    setupActivityButtons();
    checkAction();
    configChronometer();
    configShowThumbAndNumberClips();

  }

  private void keepScreenOn() {
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  public void setupActivityButtons() {
    // TODO:(alvaro.martinez) 7/11/16 implement this functionality
    settingsCameraButton.setEnabled(false);
    tintRecordButtons(R.color.button_color_record_activity);
  }

  private void tintRecordButtons(int button_color) {
    tintButton(flashButton, button_color);
    tintButton(changeCameraButton, button_color);
    tintButton(showControlsButton, button_color);
    tintButton(hideControlsViewButton, button_color);
    tintButton(navigateSettingsButtons, button_color);
    tintButton(settingsCameraButton, button_color);
  }

  private void checkAction() {
    if (getIntent().getAction() != null) {
      if (getIntent().getAction().equals(MediaStore.ACTION_VIDEO_CAPTURE)) {
        if (getIntent().getClipData() != null) {
          resultVideoPath = getIntent().getClipData().getItemAt(0).getUri().toString();
          if (resultVideoPath.startsWith("file://"))
            resultVideoPath = resultVideoPath.replace("file://", "");
        }
        externalIntent = true;
      }
    }
  }

  private void configChronometer() {
    chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
      @Override
      public void onChronometerTick(Chronometer chronometer) {
        long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();

        int h = (int) (elapsedTime / 3600000);
        int m = (int) (elapsedTime - h * 3600000) / 60000;
        int s = (int) (elapsedTime - h * 3600000 - m * 60000) / 1000;
        // String hh = h < 10 ? "0"+h: h+"";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
        String time = mm + ":" + ss;
        chronometer.setText(time);
      }
    });
  }

  private void configShowThumbAndNumberClips() {
    thumbClipRecordedButton.setBorderWidth(5);
    thumbClipRecordedButton.setBorderColor(Color.WHITE);
    numVideosRecordedTextView.setVisibility(View.GONE);
  }

  @Override
  public void onResume() {
    super.onResume();
    startBackgroundThread();
    if (textureView.isAvailable()) {
      openCamera(textureView.getWidth(), textureView.getHeight());
    } else {
      textureView.setSurfaceTextureListener(this);
    }
  }

  @Override
  public void onPause() {
    closeCamera();
    stopBackgroundThread();
    super.onPause();
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
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    configureTransform(width, height);
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

    final Activity activity = this;
    if (null == activity || activity.isFinishing()) {
      return;
    }
//    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try {
      Log.d(LOG_TAG, "tryAcquire");
      if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
        throw new RuntimeException("Time out waiting to lock camera opening.");
      }
      String cameraId = manager.getCameraIdList()[0];

      // Choose the sizes for camera preview and video recording
      CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
      StreamConfigurationMap map = characteristics
          .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
      videoSize = RecordCamera2Utils.chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
      previewSize = RecordCamera2Utils.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
          width, height, videoSize);

      int orientation = getResources().getConfiguration().orientation;
      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
      } else {
        textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
      }
      configureTransform(width, height);
      mediaRecorder = new MediaRecorder();
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
    Activity activity = this;
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
              Activity activity = RecordCamera2Activity.this;
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

}
