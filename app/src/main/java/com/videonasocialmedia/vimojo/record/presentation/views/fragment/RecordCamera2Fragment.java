package com.videonasocialmedia.vimojo.record.presentation.views.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.record.presentation.views.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by alvaro on 16/01/17.
 */

public class RecordCamera2Fragment extends Fragment
    implements View.OnClickListener {

  private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
  private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
  private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
  private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

  private static final String TAG = "Camera2VideoFragment";
  private static final String FRAGMENT_DIALOG = "dialog";


  static {
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
    DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
  }

  static {
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
    INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
  }

  /**
   * An {@link AutoFitTextureView} for camera preview.
   */
  private AutoFitTextureView mTextureView;

  /**
   * Button to record video
   */
  private Button mButtonVideo;

  /**
   * A refernce to the opened {@link android.hardware.camera2.CameraDevice}.
   */
  private CameraDevice mCameraDevice;

  /**
   * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for
   * preview.
   */
  private CameraCaptureSession mPreviewSession;

  /**
   * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
   * {@link TextureView}.
   */
  private TextureView.SurfaceTextureListener mSurfaceTextureListener
      = new TextureView.SurfaceTextureListener() {

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                          int width, int height) {
      openCamera(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                            int width, int height) {
      configureTransform(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
      return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

  };

  /**
   * The {@link android.util.Size} of camera preview.
   */
  private Size mPreviewSize;

  /**
   * The {@link android.util.Size} of video recording.
   */
  private Size mVideoSize;

  /**
   * MediaRecorder
   */
  private MediaRecorder mMediaRecorder;

  /**
   * Whether the app is recording video now
   */
  private boolean mIsRecordingVideo;

  /**
   * An additional thread for running tasks that shouldn't block the UI.
   */
  private HandlerThread mBackgroundThread;

  /**
   * A {@link Handler} for running tasks in the background.
   */
  private Handler mBackgroundHandler;

  /**
   * A {@link Semaphore} to prevent the app from exiting before closing the camera.
   */
  private Semaphore mCameraOpenCloseLock = new Semaphore(1);

  /**
   * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
   */
  private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

    @Override
    public void onOpened(CameraDevice cameraDevice) {
      mCameraDevice = cameraDevice;
      startPreview();
      mCameraOpenCloseLock.release();
      if (null != mTextureView) {
        configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
      }
    }

    @Override
    public void onDisconnected(CameraDevice cameraDevice) {
      mCameraOpenCloseLock.release();
      cameraDevice.close();
      mCameraDevice = null;
    }

    @Override
    public void onError(CameraDevice cameraDevice, int error) {
      mCameraOpenCloseLock.release();
      cameraDevice.close();
      mCameraDevice = null;
      Activity activity = getActivity();
      if (null != activity) {
        activity.finish();
      }
    }

  };
  private Integer mSensorOrientation;
  private String mNextVideoAbsolutePath;
  private CaptureRequest.Builder mPreviewBuilder;
  private Surface mRecorderSurface;

  public static RecordCamera2Fragment newInstance() {
    return new RecordCamera2Fragment();
  }

  /**
   * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
   * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
   *
   * @param choices The list of available sizes
   * @return The video size
   */
  private static Size chooseVideoSize(Size[] choices) {
    for (Size size : choices) {
      if (size.getWidth() == size.getHeight() * 16 / 9 && size.getWidth() <= 1080) {
        return size;
      }
    }
    Log.e(TAG, "Couldn't find any suitable video size");
    return choices[choices.length - 1];
  }

  /**
   * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
   * width and height are at least as large as the respective requested values, and whose aspect
   * ratio matches with the specified value.
   *
   * @param choices     The list of sizes that the camera supports for the intended output class
   * @param width       The minimum desired width
   * @param height      The minimum desired height
   * @param aspectRatio The aspect ratio
   * @return The optimal {@code Size}, or an arbitrary one if none were big enough
   */
  private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
    // Collect the supported resolutions that are at least as big as the preview Surface
    List<Size> bigEnough = new ArrayList<Size>();
    int w = aspectRatio.getWidth();
    int h = aspectRatio.getHeight();
    for (Size option : choices) {
      if (option.getHeight() == option.getWidth() * h / w &&
          option.getWidth() >= width && option.getHeight() >= height) {
        bigEnough.add(option);
      }
    }

    // Pick the smallest of those, assuming we found any
    if (bigEnough.size() > 0) {
      return Collections.min(bigEnough, new CompareSizesByArea());
    } else {
      Log.e(TAG, "Couldn't find any suitable preview size");
      return choices[0];
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_record_camera2, container, false);
  }

  @Override
  public void onViewCreated(final View view, Bundle savedInstanceState) {
    mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
    mButtonVideo = (Button) view.findViewById(R.id.video);
    mButtonVideo.setOnClickListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    startBackgroundThread();
    if (mTextureView.isAvailable()) {
      openCamera(mTextureView.getWidth(), mTextureView.getHeight());
    } else {
      mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }
  }

  @Override
  public void onPause() {
    closeCamera();
    stopBackgroundThread();
    super.onPause();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.video: {
        if (mIsRecordingVideo) {
          stopRecordingVideo();
        } else {
          startRecordingVideo();
        }
        break;
      }
    }
  }

  /**
   * Starts a background thread and its {@link Handler}.
   */
  private void startBackgroundThread() {
    mBackgroundThread = new HandlerThread("CameraBackground");
    mBackgroundThread.start();
    mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
  }

  /**
   * Stops the background thread and its {@link Handler}.
   */
  private void stopBackgroundThread() {
    mBackgroundThread.quitSafely();
    try {
      mBackgroundThread.join();
      mBackgroundThread = null;
      mBackgroundHandler = null;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  /**
   * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
   */
  private void openCamera(int width, int height) {

    final Activity activity = getActivity();
    if (null == activity || activity.isFinishing()) {
      return;
    }
//    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try {
      Log.d(TAG, "tryAcquire");
      if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
        throw new RuntimeException("Time out waiting to lock camera opening.");
      }
      String cameraId = manager.getCameraIdList()[0];

      // Choose the sizes for camera preview and video recording
      CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
      StreamConfigurationMap map = characteristics
          .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
      mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
      mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
          width, height, mVideoSize);

      int orientation = getResources().getConfiguration().orientation;
      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
      } else {
        mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
      }
      configureTransform(width, height);
      mMediaRecorder = new MediaRecorder();
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
      manager.openCamera(cameraId, mStateCallback, null);
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
      mCameraOpenCloseLock.acquire();
      closePreviewSession();
      if (null != mCameraDevice) {
        mCameraDevice.close();
        mCameraDevice = null;
      }
      if (null != mMediaRecorder) {
        mMediaRecorder.release();
        mMediaRecorder = null;
      }
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while trying to lock camera closing.");
    } finally {
      mCameraOpenCloseLock.release();
    }
  }

  /**
   * Start the camera preview.
   */
  private void startPreview() {
    if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
      return;
    }
    try {
      closePreviewSession();
      SurfaceTexture texture = mTextureView.getSurfaceTexture();
      assert texture != null;
      texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
      mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

      Surface previewSurface = new Surface(texture);
      mPreviewBuilder.addTarget(previewSurface);

      mCameraDevice.createCaptureSession(Arrays.asList(previewSurface),
          new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
          mPreviewSession = cameraCaptureSession;
          updatePreview();
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
          Activity activity = getActivity();
          if (null != activity) {
            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
          }
        }
      }, mBackgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the camera preview. {@link #startPreview()} needs to be called in advance.
   */
  private void updatePreview() {
    if (null == mCameraDevice) {
      return;
    }
    try {
      setUpCaptureRequestBuilder(mPreviewBuilder);
      HandlerThread thread = new HandlerThread("CameraPreview");
      thread.start();
      mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
    builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
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
    Activity activity = getActivity();
    if (null == mTextureView || null == mPreviewSize || null == activity) {
      return;
    }
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    Matrix matrix = new Matrix();
    RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
    RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
    float centerX = viewRect.centerX();
    float centerY = viewRect.centerY();
    if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
      bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
      matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
      float scale = Math.max(
          (float) viewHeight / mPreviewSize.getHeight(),
          (float) viewWidth / mPreviewSize.getWidth());
      matrix.postScale(scale, scale, centerX, centerY);
      matrix.postRotate(90 * (rotation - 2), centerX, centerY);
    }
    mTextureView.setTransform(matrix);
  }

  private void setUpMediaRecorder() throws IOException {
    final Activity activity = getActivity();
    if (null == activity) {
      return;
    }
    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
      mNextVideoAbsolutePath = getVideoFilePath(getActivity());
    }
    mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
    mMediaRecorder.setVideoEncodingBitRate(10000000);
    mMediaRecorder.setVideoFrameRate(30);
    mMediaRecorder.setVideoSize(1280, 720);
    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    mMediaRecorder.setAudioChannels(1);
    mMediaRecorder.setAudioSamplingRate(48000);
    mMediaRecorder.setAudioEncodingBitRate(192000);
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    switch (mSensorOrientation) {
      case SENSOR_ORIENTATION_DEFAULT_DEGREES:
        mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
        break;
      case SENSOR_ORIENTATION_INVERSE_DEGREES:
        mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
        break;
    }
    mMediaRecorder.prepare();
  }

  private String getVideoFilePath(Context context) {
    return Constants.PATH_APP + "/"
        + System.currentTimeMillis() + ".mp4";
  }

  private void startRecordingVideo() {
    if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
      return;
    }
    try {
      closePreviewSession();
      setUpMediaRecorder();
      SurfaceTexture texture = mTextureView.getSurfaceTexture();
      assert texture != null;
      texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
      mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
      List<Surface> surfaces = new ArrayList<>();

      // Set up Surface for the camera preview
      Surface previewSurface = new Surface(texture);
      surfaces.add(previewSurface);
      mPreviewBuilder.addTarget(previewSurface);

      // Set up Surface for the MediaRecorder
      mRecorderSurface = mMediaRecorder.getSurface();
      surfaces.add(mRecorderSurface);
      mPreviewBuilder.addTarget(mRecorderSurface);

      // Start a capture session
      // Once the session starts, we can update the UI and start recording
      mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
          mPreviewSession = cameraCaptureSession;
          updatePreview();
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              // UI
              mButtonVideo.setText("Stop");
              mIsRecordingVideo = true;

              // Start recording
              mMediaRecorder.start();
            }
          });
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
          Activity activity = getActivity();
          if (null != activity) {
            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
          }
        }
      }, mBackgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void closePreviewSession() {
    if (mPreviewSession != null) {
      mPreviewSession.close();
      mPreviewSession = null;
    }
  }

  private void stopRecordingVideo() {
    // UI
    mIsRecordingVideo = false;
    //mButtonVideo.setText(R.string.record);
    mButtonVideo.setText("Record");
    // Stop recording
    mMediaRecorder.stop();
    mMediaRecorder.reset();

    Activity activity = getActivity();
    if (null != activity) {
      Toast.makeText(activity, "Video saved: " + mNextVideoAbsolutePath,
          Toast.LENGTH_SHORT).show();
      Log.d(TAG, "Video saved: " + mNextVideoAbsolutePath);
    }
    mNextVideoAbsolutePath = null;
    startPreview();
  }

  /**
   * Compares two {@code Size}s based on their areas.
   */
  static class CompareSizesByArea implements Comparator<Size> {

    @Override
    public int compare(Size lhs, Size rhs) {
      // We cast here to ensure the multiplications won't overflow
      return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
          (long) rhs.getWidth() * rhs.getHeight());
    }

  }

}
