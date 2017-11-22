/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.record.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraPreferencesUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.PicometerAmplitudeDbListener;
import com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.PicometerSamplingLoopThread;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.text.DecimalFormat;
import java.util.List;

/**
 *  Created by alvaro on 16/01/17.
 */

public class RecordCamera2Presenter implements Camera2WrapperListener {
  public static final int DEFAULT_CAMERA_ID = 0;
  private static final int NORMALIZE_PICOMETER_VALUE = 108;
  private static final double MAX_AMPLITUDE_VALUE_PICOMETER = 32768;
  private static final int SLEEP_TIME_MILLIS_WAITING_FOR_NEXT_VALUE = 100;
  public static final int PREVIEW_RECORD_PICOMETER_SCALE_CORRECTION_RATIO = 2;
  // TODO:(alvaro.martinez) 26/01/17  ADD TRACKING TO RECORD ACTIVITY. Update from RecordActivity
  private static final String TAG = RecordCamera2Presenter.class.getCanonicalName();
  private final Context context;
  private GetCameraPreferencesUseCase getCameraPreferencesUseCase;
  private SharedPreferences sharedPreferences;
  protected UserEventTracker userEventTracker;
  private final NewClipImporter newClipImporter;
  private RecordCamera2View recordView;
  private AddVideoToProjectUseCase addVideoToProjectUseCase;
  private int videosRecorded = 0;
  private Project currentProject;
  private Camera2Wrapper camera;

  private VideonaFormat videoFormat;
  private boolean isFrontCameraSelected = false;

  private long ONE_KB = 1024;
  private long ONE_MB = ONE_KB * 1024;
  private long ONE_GB = ONE_MB * 1024;
  private PicometerSamplingLoopThread picometerSamplingLoopThread;
  private int audioGain = 100;
  private Handler picometerRecordingUpdaterHandler = new Handler();
  private Runnable updatePicometerRecordingTask = new Runnable() {
    @Override
    public void run() {
      updatePicometerRecording();
    }
  };

  // TODO:(alvaro.martinez) 14/11/17 get data from realm camera repository
  private boolean cameraProSelected = false;

  public RecordCamera2Presenter(
      Context context, RecordCamera2View recordView,
      UserEventTracker userEventTracker,
      SharedPreferences sharedPreferences,
      AddVideoToProjectUseCase addVideoToProjectUseCase, NewClipImporter newClipImporter,
      Camera2Wrapper camera, GetCameraPreferencesUseCase getCameraPreferencesUseCase) {
    this.context = context;
    this.recordView = recordView;
    this.userEventTracker = userEventTracker;
    this.sharedPreferences = sharedPreferences;
    this.addVideoToProjectUseCase = addVideoToProjectUseCase;
    this.getCameraPreferencesUseCase = getCameraPreferencesUseCase;
    this.currentProject = loadProject();
    // TODO:(alvaro.martinez) 25/01/17 Support camera1, api <21 or combine both. Make Camera1Wrapper
//    camera = new Camera2Wrapper(context, DEFAULT_CAMERA_ID, textureView, directorySaveVideos,
//        getVideoFormatFromCurrentProjectUseCase.getVideoRecordedFormatFromCurrentProjectUseCase());
    this.camera = camera;
    camera.setCameraListener(this);

    this.newClipImporter = newClipImporter;
  }

  private Project loadProject() {
    return Project.getInstance(null, null, null, null);
  }

  public void initViews() {
    checkCameraPreferences();
    recordView.setCameraSettingSelected(getResolution(currentProject.getProfile().getResolution()),
        getQuality(currentProject.getProfile().getQuality()),
        getFrameRate(currentProject.getProfile().getFrameRate()));
    recordView.showPrincipalViews();
    recordView.showRightControlsView();
    recordView.showSettingsCameraView();
    recordView.hideRecordPointIndicator();
    setupAdvancedCameraControls();
  }

  private void checkCameraPreferences() {
    if(getCameraPreferencesUseCase.isInterfaceProSelected()) {
      cameraProSelected = true;
    }
  }

  private String getResolution(VideoResolution.Resolution resolution) {
    String resolutionName;
    switch (resolution) {
      case HD1080:
        resolutionName = "1080p";
        break;
      case HD4K:
        resolutionName = "4k";
        break;
      case HD720:
      default:
        resolutionName = "720p";
    }
    return resolutionName;
  }


  private String getQuality(VideoQuality.Quality quality) {
    String qualityName;
    switch (quality) {
      case HIGH:
        qualityName = "50 Mbps";
        break;
      case GOOD:
        qualityName = "32 Mbps";
        break;
      case LOW:
      default:
        qualityName = "16 Mbps";
    }
    return qualityName;
  }

  private String getFrameRate(VideoFrameRate.FrameRate frameRate) {
    String frameRateName;
    switch(frameRate) {
      case FPS24:
        frameRateName = "24 fps";
        break;
      case FPS25:
        frameRateName = "25 fps";
        break;
      case FPS30:
      default:
        frameRateName = "30 fps";
    }
    return frameRateName;
  }

  private void setupAdvancedCameraControls() {
    if (!camera.ISOSelectionSupported() || !cameraProSelected) {
      recordView.hideISOSelection();
    } else {
      recordView.showISOSelection();
      recordView.setupISOSupportedModesButtons(camera.getSupportedISORange());
    }
    if (!camera.focusSelectionSupported() || !cameraProSelected) {
      recordView.hideAdvancedAFSelection();
    } else {
      recordView.showAdvancedAFSelection();
      recordView.setupFocusSelectionSupportedModesButtons(
              camera.getSupportedFocusSelectionModes().values);
    }
    if (!camera.whiteBalanceSelectionSupported() || !cameraProSelected) {
      recordView.hideWhiteBalanceSelection();
    } else {
      recordView.showWhiteBalanceSelection();
      recordView.setupWhiteBalanceSupportedModesButtons(
              camera.getSupportedWhiteBalanceModes().values);
    }
    if (!camera.metteringModeSelectionSupported() || !cameraProSelected) {
      recordView.hideMetteringModeSelection();
    } else {
      recordView.showMetteringModeSelection();
      recordView.setupMeteringModeSupportedModesButtons(
              camera.getSupportedMeteringModes().values);
    }

    if (!cameraProSelected) {
      recordView.hideDefaultButton();
      recordView.hideAudioGainButton();
    } else {
      recordView.showDefaultButton();
      recordView.showAudioGainButton();
    }
  }

  public void onResume() {
    showThumbAndNumber();
    Log.d(TAG, "resume presenter");
    try {
      camera.onResume();
      startSamplingPicometerPreview();
    } catch (RuntimeException cameraError) {
      // TODO(jliarte): 18/10/17 move to strings
      recordView.showError("Error opening camera, please restart the app");
    }
  }

  private void startSamplingPicometerPreview() {
    // Stop previous sampler if any.
    stopCurrentPicometerSamplingLoopThread();
    // Start sampling
    picometerSamplingLoopThread = new PicometerSamplingLoopThread(
        new PicometerAmplitudeDbListener() {
      @Override
      public void setMaxAmplituedDb(double maxAmplituedDb) {
//        Log.d(TAG, "maxAmplitudePreview Dbs " + maxAmplituedDb);
        setPicometerProgressAndColor(getProgressPicometerPreview(maxAmplituedDb));
      }
    });
    picometerSamplingLoopThread.start();
  }

  private void stopCurrentPicometerSamplingLoopThread() {
    if (picometerSamplingLoopThread != null) {
      picometerSamplingLoopThread.finish();
      try {
        picometerSamplingLoopThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      picometerSamplingLoopThread = null;
    }
  }

  private int getProgressPicometerPreview(double maxAmplituedDb) {
    int progress = 100 - (int) ((maxAmplituedDb / NORMALIZE_PICOMETER_VALUE) * 100 * -1);
    progress = (progress < 100) ? progress : 0;
    return progress;
  }

  private void stopSamplingPicometerPreview(){
    if (picometerSamplingLoopThread != null) {
      picometerSamplingLoopThread.finish();
    }
  }

  private void startSamplingPicometerRecording() {
    picometerRecordingUpdaterHandler.postDelayed(updatePicometerRecordingTask,
        SLEEP_TIME_MILLIS_WAITING_FOR_NEXT_VALUE);
  }

  private void updatePicometerRecording() {
    int maxAmplitude = camera.getMaxAmplitudeRecording();
    double dBs = getAmplitudePicometerFromRecorderDbs(maxAmplitude);
    //Log.d(TAG, "maxAmplitudeRecording " + maxAmplitude + " dBs " + dBs);
    int progress = getProgressPicometerRecording(dBs);
    if (maxAmplitude > 0) {
      setPicometerProgressAndColor(progress);
    }

    if (camera.isRecordingVideo()) {
      picometerRecordingUpdaterHandler.postDelayed(updatePicometerRecordingTask,
          SLEEP_TIME_MILLIS_WAITING_FOR_NEXT_VALUE);
    }
  }

  private float getAmplitudePicometerFromRecorderDbs(int maxAmplitude) {
    return (float) (20 * Math.log10(maxAmplitude / MAX_AMPLITUDE_VALUE_PICOMETER));
  }

  private int getProgressPicometerRecording(double dBs) {
    return (int) (100 - ((dBs / NORMALIZE_PICOMETER_VALUE) * 100 * -1
            * PREVIEW_RECORD_PICOMETER_SCALE_CORRECTION_RATIO));
  }

  private void setPicometerProgressAndColor(int progress) {
    int color;
    // TODO(jliarte): 13/07/17 should we check limits here?
    progress = progress * audioGain / 100;
    color = R.color.recordActivityInfoGreen;
    if (progress > 80) {
      color = R.color.recordActivityInfoYellow;
    }
    if (progress > 98) {
      color = R.color.recordActivityInfoRed;
    }
    recordView.showProgressPicometer(progress, color);
//    Log.d(TAG, "Picometer progress " + progress + " isRecording " + camera.isRecordingVideo());
  }

  public void onPause() {
    if(camera.isRecordingVideo()){
      stopVideoRecording();
    }
    camera.onPause();
    recordView.stopMonitoringRotation();
    stopSamplingPicometerPreview();
    picometerRecordingUpdaterHandler.removeCallbacksAndMessages(null);
  }

  private void showThumbAndNumber() {
    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase =
        new GetMediaListFromProjectUseCase();
    final List mediaInProject = getMediaListFromProjectUseCase.getMediaListFromProject();
    if (mediaInProject != null && mediaInProject.size() > 0) {
      int lastItemIndex = mediaInProject.size() - 1;
      final Video lastItem = (Video) mediaInProject.get(lastItemIndex);
      this.videosRecorded = mediaInProject.size();
      recordView.showVideosRecordedNumber(videosRecorded);
      recordView.showRecordedVideoThumbWithText(lastItem.getMediaPath());
    } else {
      recordView.hideVideosRecordedNumber();
    }
  }

  public void startRecord() {
    stopSamplingPicometerPreview();
    try {
      camera.startRecordingVideo(new Camera2Wrapper.RecordStartedCallback() {
        @Override
        public void onRecordStarted() {
          recordView.showStopButton();
          recordView.startChronometer();
          recordView.showRecordPointIndicator();
          recordView.hideNavigateToSettingsActivity();
          recordView.hideVideosRecordedNumber();
          recordView.hideRecordedVideoThumbWithText();
          recordView.hideChangeCamera();
          recordView.updateAudioGainSeekbarDisability();
          userEventTracker.trackVideoStartRecording();
          startSamplingPicometerRecording();
        }
      });
    } catch (IllegalStateException illegalState) {
      // do nothing as it should be already managed in camera wrapper
      startSamplingPicometerPreview();
    }
  }

  public void stopRecord() {
    try {
      stopVideoRecording();
      picometerRecordingUpdaterHandler.removeCallbacksAndMessages(null);
      startSamplingPicometerPreview();
      restartPreview();
      recordView.updateAudioGainSeekbarDisability();
    } catch (RuntimeException runtimeException) {
      // do nothing as it's already managed in camera wrapper
    }
    incrementSharedPreferencesTotalVideosRecorded();
    trackVideoRecorded();
  }

  private void trackVideoRecorded() {
    userEventTracker.trackVideoStopRecording();
    userEventTracker.trackTotalVideosRecordedSuperProperty();
    userEventTracker.trackVideoRecorded(currentProject, getSharedPreferencesTotalVideosRecorded());
    userEventTracker.trackVideoRecordedUserTraits();
  }

  private void incrementSharedPreferencesTotalVideosRecorded() {
    int numTotalVideosRecorded = getSharedPreferencesTotalVideosRecorded();
    SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
    preferenceEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED,
        ++numTotalVideosRecorded);
    preferenceEditor.commit();
  }

  private int getSharedPreferencesTotalVideosRecorded() {
    return sharedPreferences
          .getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
  }

  protected void stopVideoRecording() {
    camera.stopRecordVideo();
    updateStopVideoUI();
    onVideoRecorded(camera.getVideoPath());
  }

  private void updateStopVideoUI() {
    recordView.showRecordButton();
    recordView.showNavigateToSettingsActivity();
    recordView.stopChronometer();
    recordView.hideRecordPointIndicator();
    recordView.resetChronometer();
    recordView.showChangeCamera();
//    setFlashOff();
  }

  @Override
  public void setFlashSupport() {
    if (camera.isFlashSupported()) {
      recordView.setFlashSupported(true);
      Log.d(TAG, "checkSupportFlash flash Supported camera");
    } else {
       recordView.setFlashSupported(false);
      Log.d(TAG, "checkSupportFlash flash NOT Supported camera");
    }
  }

  private void onVideoRecorded(String path) {
    recordView.showRecordedVideoThumbWithText(path);
    recordView.showVideosRecordedNumber(++videosRecorded);
    Video recordedVideo = new Video(path, (float) audioGain / 100f);
    addVideoToProject(recordedVideo);
    Log.d("Importer", "Adding video to project " + recordedVideo.getMediaPath());
    newClipImporter.adaptVideoToVideonaFormat(currentProject,
            recordedVideo, videosRecorded, camera.getRotation(), 0);
  }

  private void addVideoToProject(Video recordedVideo) {
    addVideoToProjectUseCase.addVideoToProjectAtPosition(recordedVideo,
            currentProject.numberOfClips(), new OnAddMediaFinishedListener() {
              @Override
              public void onAddMediaItemToTrackError() {
                recordView.showError(context.getString(R.string.addMediaItemToTrackError));
              }

              @Override
              public void onAddMediaItemToTrackSuccess(Media media) {
//                checkIfVideoAddedNeedLaunchAVTransitionJob((Video) media);
                // TODO(jliarte): 31/08/17 should we do anything here?
              }
            });
  }

//  private void checkIfVideoAddedNeedLaunchAVTransitionJob(Video video) {
//    if (currentProject.getVMComposition().isAudioFadeTransitionActivated()
//            || currentProject.getVMComposition().isVideoFadeTransitionActivated()) {
//      videoToLaunchAVTransitionTempFile(video,
//          currentProject.getProjectPathIntermediateFileAudioFade());
//    }
//  }

  @Override
  public void setZoom(float zoomValue) {
    recordView.setZoom(zoomValue);
  }

  @Override
  public void setError(String message) {
    //recordView.showError(message);
  }

  public void resetZoom(){
    camera.resetZoom();
  }

  public void restartPreview() {
    if (!camera.isRecordingVideo()) {
      camera.reStartPreview();
    }
  }

  public void setFlashOff() {
    camera.setFlashOff();
    recordView.setFlash(false);
  }

  public void isFlashEnabled(boolean isSelected) {
    if (isSelected) {
      camera.setFlashOff();
      recordView.setFlash(false);
    } else {
      camera.setFlashOn();
      recordView.setFlash(true);
    }
    userEventTracker.trackChangeFlashMode(isSelected);
  }

  public void onTouchZoom(MotionEvent event) {
      camera.onTouchZoom(getFingerSpacing(event));
  }

  //Determine the space between the first two fingers
  @SuppressWarnings("deprecation")
  private float getFingerSpacing(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return (float) Math.sqrt(x * x + y * y);
  }

  public void showRightControls() {
    recordView.showRightControlsView();
  }

  public void hideRightControls() {
    recordView.hideRightControlsView();
  }

  public void buttonSettingsCamera(boolean isSelected) {
    if (isSelected) {
      recordView.hideSettingsCameraView();
    } else {
      recordView.showSettingsCameraView();
    }
  }

  public void navigateToEdit() {
      if (areThereVideosInProject()) {
        recordView.navigateTo(EditActivity.class);
      } else {
        recordView.navigateTo(GalleryActivity.class);
      }
  }

  private boolean areThereVideosInProject() {
    return currentProject.getVMComposition().hasVideos();
  }

//  @Override
//  public void videoToLaunchAVTransitionTempFile(Video video,
//                                                String intermediatesTempAudioFadeDirectory) {
//    video.setTempPath(currentProject.getProjectPathIntermediateFiles());
//
//    videoFormat = currentProject.getVMComposition().getVideoFormat();
//    Drawable drawableFadeTransitionVideo = currentProject.getVMComposition()
//            .getDrawableFadeTransitionVideo();
//    launchTranscoderAddAVTransitionUseCase.applyAVTransitions(drawableFadeTransitionVideo, video,
//            videoFormat, intermediatesTempAudioFadeDirectory, new TranscoderHelperListener() {
//              // TODO(jliarte): 5/07/17 check these two listener, code is the else {} part
//              @Override
//              public void onSuccessTranscoding(Video video) {
//                Log.d(TAG, "onSuccessTranscoding " + video.getTempPath());
//                videoRepository.setSuccessTranscodingVideo(video);
//              }
//
//              @Override
//              public void onErrorTranscoding(Video video, String message) {
//                Log.d(TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
//                if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
//                  video.increaseNumTriesToExportVideo();
//                  Project currentProject = Project.getInstance(null, null, null, null);
//                  launchTranscoderAddAVTransitionUseCase.applyAVTransitions(
//                          context.getDrawable(R.drawable.alpha_transition_white), video,
//                          videoFormat, currentProject.getProjectPathIntermediateFileAudioFade(),
//                          this);
//                } else {
//                  videoRepository.setErrorTranscodingVideo(video,
//                          Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
//                }
//              }
//            });
//  }

  public void switchCamera() {
    isFrontCameraSelected = !isFrontCameraSelected;
    resetViewSwitchCamera();
    recordView.setCameraDefaultSettings();
    camera.switchCamera(isFrontCameraSelected);
    setupAdvancedCameraControls();
    userEventTracker.trackChangeCamera(isFrontCameraSelected);
  }

  private void resetViewSwitchCamera() {
    recordView.setZoom(0f);
    recordView.resetSpotMeteringSelector();
  }

  public void onSeekBarZoom(float zoomValue) {
    camera.seekBarZoom(zoomValue);
  }

  public void storageDialog(long totalStorage, long freeStorage) {
    updateFreeStorageSpace(totalStorage, freeStorage);
    recordView.showAlertDialogStorage();
  }

  public void batteryDialog(int level, int status, int scale) {
    updateBatteryStatus(status, level, scale);
    recordView.showAlertDialogBattery();
  }

  // ------------------ white balance settings --------------------

  public void resetWhiteBalanceMode() {
    camera.resetWhiteBalanceMode();
  }

  public void setWhiteBalanceMode(String whiteBalanceMode) {
    camera.setWhiteBalanceMode(whiteBalanceMode);
  }

  // ------------------ metering-exposure settings --------------------

  public void resetMeteringMode() {
    camera.resetMeteringMode();
  }

  public void setFocusSelectionMode(String focusSelectionMode){
    camera.setFocusSelectionMode(focusSelectionMode);
  }

  public void resetFocusSelectionMode(){
    camera.resetFocusSelectionMode();
  }

  public void setExposureCompensation(int exposureCompensation) {
    camera.setExposureCompensation(exposureCompensation);
  }

  public int getMinimumExposureCompensation() {
    return camera.getMinimumExposureCompensation();
  }

  public int getMaximumExposureCompensation() {
    return camera.getMaximumExposureCompensation();
  }

  public float getExposureCompensationStep() {
    return camera.getExposureCompensationStep();
  }

  public int getCurrentExposureCompensation() {
    return camera.getCurrentExposureCompensation();
  }

  public void setMeteringPoint(int touchEventX, int touchEventY, int viewWidth, int viewHeight) {
    camera.setMeteringPoint(touchEventX, touchEventY, viewWidth, viewHeight);
  }

  public void setFocusSelectionModeSelective(int touchEventX, int touchEventY, int viewWidth,
                                             int viewHeight, MotionEvent event) {
    camera.setFocusModeSelective(touchEventX, touchEventY, viewWidth, viewHeight);
    // TODO(jliarte): 10/07/17 what tries to do this invocation?
    recordView.setFocusModeManual(event);
  }

  public void setFocusSelectionModeManual(int seekbarProgress) {
    camera.setFocusModeManual(seekbarProgress);
  }
  public Integer getMaximumSensitivity() {
    return camera.getMaximumSensitivity();
  }

  public void setISO(Integer isoValue) {
    camera.setISO(isoValue);
  }

  public void setMicrophoneStatus(int state, int microphone) {
    if(isAJackMicrophoneConnected(state, microphone)){
      recordView.showExternalMicrophoneConnected();
    } else {
      recordView.showSmartphoneMicrophoneWorking();
    }
  }

  private boolean isAJackMicrophoneConnected(int state, int microphone) {
    return state == 1 && microphone == 1;
  }

  public void setAudioGain(int audioGain) {
    this.audioGain = audioGain;
  }

  // --------------------------------------------------------------

  public void updateBatteryStatus(int batteryStatus, int batteryLevel, int batteryScale) {
    int batteryPercent= getPercentLevel(batteryLevel, batteryScale);
    recordView.showBatteryStatus(getBatteryStatus(batteryStatus, batteryPercent),batteryPercent);
  }


  public int getPercentLevel(int batteryLevel, int batteryScale) {
    float level = batteryLevel / (float) batteryScale *100;
    return Math.round(level);
  }

  public Constants.BATTERY_STATUS getBatteryStatus(int batteryStatus, int batteryPercent) {
    Constants.BATTERY_STATUS status;
    if(batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING)
      status = Constants.BATTERY_STATUS.CHARGING;
    else
      status = getStatusNotCharging(batteryPercent);
    return status;
  }

  public Constants.BATTERY_STATUS getStatusNotCharging(int batteryPercent) {
    Constants.BATTERY_STATUS status;
    if (batteryPercent < 15)
      status = Constants.BATTERY_STATUS.CRITICAL;
    else if (batteryPercent>=15 && batteryPercent<25)
      status = Constants.BATTERY_STATUS.LOW;
    else if (batteryPercent>=25 && batteryPercent<75)
      status = Constants.BATTERY_STATUS.MEDIUM;
    else status= Constants.BATTERY_STATUS.FULL;
    return status;
  }

  public void updateFreeStorageSpace(long totalMemory, long freeMemory) {
    int memoryFreePercent= getPercentFreeBattery(totalMemory, freeMemory);
    Constants.MEMORY_STATUS memoryStatus= getMemoryStatus(memoryFreePercent);
    String freeMemoryInBytes= toFormattedMemorySpaceWithBytes(freeMemory);
    String totalMemoryInBytes=toFormattedMemorySpaceWithBytes(totalMemory);
    recordView.showFreeStorageSpace(memoryStatus, memoryFreePercent, freeMemoryInBytes,
            totalMemoryInBytes);
  }

  private int getPercentFreeBattery(long totalMemory, long freeMemory) {
    return Math.round(freeMemory / (float) totalMemory * 100);
  }

  private Constants.MEMORY_STATUS getMemoryStatus(int freeMemoryPercent) {
    Constants.MEMORY_STATUS memoryStatus;
    if (freeMemoryPercent < 25) {
      memoryStatus = Constants.MEMORY_STATUS.CRITICAL;
    } else if (freeMemoryPercent >= 25 && freeMemoryPercent < 75) {
      memoryStatus = Constants.MEMORY_STATUS.MEDIUM;
    } else {
      memoryStatus = Constants.MEMORY_STATUS.OKAY;
    }
    return memoryStatus;
  }

  private String toFormattedMemorySpaceWithBytes(long memorySpace) {
    double memorySpaceInBytes;
    if (memorySpace<ONE_KB) {
      memorySpaceInBytes = memorySpace;
      return new DecimalFormat("#.#").format(memorySpaceInBytes)+ " bytes";
    }
    if (memorySpace>=ONE_KB && memorySpace<ONE_MB) {
      memorySpaceInBytes = (double) memorySpace / ONE_KB;
      return new DecimalFormat("#.#").format(memorySpaceInBytes)+ " Kb";
    }
    if (memorySpace>=ONE_MB && memorySpace<ONE_GB) {
      memorySpaceInBytes = (double) memorySpace / ONE_MB;
      return new DecimalFormat("#.#").format(memorySpaceInBytes)+ " Mb";
    }
    if (memorySpace>=ONE_GB) {
      memorySpaceInBytes = (double) memorySpace / ONE_GB;
      return new DecimalFormat("#.#").format(memorySpaceInBytes)+ " Gb";
    }
    return "";
  }

}
