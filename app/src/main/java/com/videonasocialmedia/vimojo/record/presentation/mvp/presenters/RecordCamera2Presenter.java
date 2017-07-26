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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnLaunchAVTransitionTempFileListener;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToVideoFormatUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.PicometerAmplitudeDbListener;
import com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.PicometerSamplingLoopThread;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  Created by alvaro on 16/01/17.
 */

public class RecordCamera2Presenter implements Camera2WrapperListener,
    OnLaunchAVTransitionTempFileListener, TranscoderHelperListener {
  public static final int DEFAULT_CAMERA_ID = 0;
  public static final int NORMALIZE_PICOMETER_VALUE = 108;
  public static final double MAX_AMPLITUDE_VALUE_PICOMETER = 32768;
  public static final int SLEEP_TIME_MILLIS_WAITING_FOR_NEXT_VALUE = 100;
  public static final int PREVIEW_RECORD_PICOMETER_SCALE_CORRECTION_RATIO = 2;
  // TODO:(alvaro.martinez) 26/01/17  ADD TRACKING TO RECORD ACTIVITY. Update from RecordActivity
  private final String TAG = RecordCamera2Presenter.class.getCanonicalName();
  private final Context context;
  private SharedPreferences sharedPreferences;
  protected UserEventTracker userEventTracker;
  private RecordCamera2View recordView;
  private AddVideoToProjectUseCase addVideoToProjectUseCase;
  private AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase;
  private UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;
  private LaunchTranscoderAddAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;
  private GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase;
  private int recordedVideosNumber = 0;
  protected Project currentProject;
  private Camera2Wrapper camera;
  private List<VideoToAdapt> videoListToAdaptAndPosition = new ArrayList<>();

  private Drawable drawableFadeTransitionVideo;
  private VideonaFormat videoFormat;
  private int numTriesAdaptingVideo = 0;
  private final int maxNumTriesAdaptingVideo = 3;
  private boolean isClickedNavigateToEditOrGallery = false;
  private boolean isFrontCameraSelected = false;

  public long ONE_KB = 1 *1024;
  public long ONE_MB = ONE_KB*1024;
  public long ONE_GB = ONE_MB*1024;
  private PicometerSamplingLoopThread picometerSamplingLoopThread;
  private int audioGain = 100;
  private Handler picometerRecordingUpdaterHandler = new Handler();
  private Runnable updatePicometerRecordingTask = new Runnable() {
    @Override
    public void run() {
      updatePicometerRecording();
    }
  };

  public RecordCamera2Presenter(Context context, RecordCamera2View recordView,
                                UserEventTracker userEventTracker,
                                SharedPreferences sharedPreferences,
                                UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase,
                                LaunchTranscoderAddAVTransitionsUseCase
                                    launchTranscoderAddAVTransitionUseCase,
                                GetVideoFormatFromCurrentProjectUseCase
                                    getVideoFormatFromCurrentProjectUseCase,
                                AddVideoToProjectUseCase addVideoToProjectUseCase,
                                AdaptVideoRecordedToVideoFormatUseCase
                                    adaptVideoRecordedToVideoFormatUseCase,
                                Camera2Wrapper camera) {
    this.context = context;
    this.recordView = recordView;
    this.userEventTracker = userEventTracker;
    this.sharedPreferences = sharedPreferences;
    this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
    this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionUseCase;
    this.getVideonaFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    this.addVideoToProjectUseCase = addVideoToProjectUseCase;
    this.adaptVideoRecordedToVideoFormatUseCase = adaptVideoRecordedToVideoFormatUseCase;
    this.currentProject = loadProject();
    // TODO:(alvaro.martinez) 25/01/17 Support camera1, api <21 or combine both. Make Camera1Wrapper
//    camera = new Camera2Wrapper(context, DEFAULT_CAMERA_ID, textureView, directorySaveVideos,
//        getVideoFormatFromCurrentProjectUseCase.getVideoRecordedFormatFromCurrentProjectUseCase());
    this.camera = camera;
    camera.setCameraListener(this);
  }

  private Project loadProject() {
    return Project.getInstance(null,null,null);
  }

  public void initViews() {
    recordView.setResolutionSelected(getResolutionHeight(currentProject));
    recordView.hideChronometer();
    recordView.showPrincipalViews();
    recordView.showRightControlsView();
    recordView.showSettingsCameraView();
    setupAdvancedCameraControls();
  }

  private void setupAdvancedCameraControls() {
    if (!camera.ISOSelectionSupported()) {
      recordView.hideISOSelection();
    } else {
      recordView.showISOSelection();
      recordView.setupISOSupportedModesButtons(camera.getSupportedISORange());
    }
    if (!camera.focusSelectionSupported()) {
      recordView.hideAdvancedAFSelection();
    } else {
      recordView.showAdvancedAFSelection();
      recordView.setupFocusSelectionSupportedModesButtons(
              camera.getSupportedFocusSelectionModes().values);
    }
    if (!camera.whiteBalanceSelectionSupported()) {
      recordView.hideWhiteBalanceSelection();
    } else {
      recordView.showWhiteBalanceSelection();
      recordView.setupWhiteBalanceSupportedModesButtons(
              camera.getSupportedWhiteBalanceModes().values);
    }
    if (!camera.metteringModeSelectionSupported()) {
      recordView.hideMetteringModeSelection();
    } else {
      recordView.showMetteringModeSelection();
      recordView.setupMeteringModeSupportedModesButtons(
              camera.getSupportedMeteringModes().values);
    }
  }

  private int getResolutionHeight(Project currentProject) {
    VideoResolution.Resolution resolution = currentProject.getProfile().getResolution();
    int height;
    switch (resolution) {
      case HD1080:
        height = 1080;
        break;
      case HD4K:
        height = 2160;
        break;
      case HD720:
      default:
        height = 720;
    }
    return height;
  }

  public void onResume() {
    showThumbAndNumber();
    Log.d(TAG, "resume presenter");
    camera.onResume();
    startSamplingPicometerPreview();
  }

  private void startSamplingPicometerPreview() {
    // Stop previous sampler if any.
    stopCurrentPicometerSamplingLoopThread();
    // Start sampling
    picometerSamplingLoopThread = new PicometerSamplingLoopThread(
        new PicometerAmplitudeDbListener() {
      @Override
      public void setMaxAmplituedDb(double maxAmplituedDb) {
        //Log.d(TAG, "maxAmplitudePreview Dbs " + maxAmplituedDb);
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
    color = Color.GREEN;
    if (progress > 80) {
      color = Color.YELLOW;
    }
    if (progress > 98) {
      color = Color.RED;
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
      this.recordedVideosNumber = mediaInProject.size();
      recordView.showVideosRecordedNumber(recordedVideosNumber);
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
          recordView.showChronometer();
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

  // TODO:(alvaro.martinez) 25/07/17 Update/Get this data with RealmProject, not SharedPreferences
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
    recordView.hideChronometer();
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
    recordView.showVideosRecordedNumber(++recordedVideosNumber);
    moveAndAdaptRecordedVideo(path);
  }

  private void moveAndAdaptRecordedVideo(String origPath) {
    File tempPath = new File(origPath);
    String destVideoRecorded = Constants.PATH_APP_MASTERS + File.separator + tempPath.getName();

    final Video videoToAdapt = new Video(origPath, (float) audioGain / 100f);
    videoListToAdaptAndPosition.add(new VideoToAdapt(videoToAdapt,recordedVideosNumber));

    // FIXME: 23/05/17 if rotation == 0, should be use getVideonaFormatToAdaptVideoRecordedAudio, more efficient.
    // Fix problems with profile MotoG, LG_pablo, ...
    VideonaFormat videonaFormat = getVideonaFormatFromCurrentProjectUseCase
        .getVideonaFormatToAdaptVideoRecordedAudioAndVideo();
    // FIXME: 24/05/17 AdaptVideo not need fadeTransition or isTransitionActivated, refactor SDK
    Drawable fadeTransition = context.getDrawable(R.drawable.alpha_transition_white);
    try {
      adaptVideoRecordedToVideoFormatUseCase.adaptVideo(videoToAdapt, videonaFormat,
          destVideoRecorded, camera.getRotation(),fadeTransition, false, this);
    } catch (IOException e) {
      e.printStackTrace();
      onErrorTranscoding(videoToAdapt, "adaptVideoRecordedToVideoFormatUseCase");
      recordView.hideProgressAdaptingVideo();
    }
  }

  private void videoRecordedAdapted(final String origVideoRecorded, String destVideoRecorded,
                                    int position) {
    addVideoToProjectUseCase.addVideoToProjectAtPosition(new Video(destVideoRecorded,
            Video.DEFAULT_VOLUME), position, new OnAddMediaFinishedListener() {
      @Override
      public void onAddMediaItemToTrackError() {
        recordView.hideProgressAdaptingVideo();
        recordView.showError(context.getString(R.string.addMediaItemToTrackError));
      }

      @Override
      public void onAddMediaItemToTrackSuccess(Media media) {
        Utils.removeVideo(origVideoRecorded);
        if (!areTherePendingTranscodingTask() || videoListToAdaptAndPosition.size() == 0) {
          recordView.hideProgressAdaptingVideo();
          if(isClickedNavigateToEditOrGallery){
            navigateToEditOrGallery();
          }
        }
        checkIfVideoAddedNeedLaunchAVTransitionJob((Video) media);
      }
    });
  }

  private void checkIfVideoAddedNeedLaunchAVTransitionJob(Video video) {
    if(currentProject.isAudioFadeTransitionActivated()
        || currentProject.isVideoFadeTransitionActivated()){
      videoToLaunchAVTransitionTempFile(video,
          currentProject.getProjectPathIntermediateFileAudioFade());
    }
  }

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

  public void restartPreview(){
    if(!camera.isRecordingVideo()) {
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
    if(isSelected) {
      recordView.hideSettingsCameraView();
    } else {
      recordView.showSettingsCameraView();
    }
  }

  public void navigateToEditOrGallery() {
    if (areTherePendingTranscodingTask()) {
      recordView.showProgressAdaptingVideo();
      isClickedNavigateToEditOrGallery = true;
      Log.d(TAG, "showProgressAdaptingVideo");
    } else {
      if(areThereVideosInProject()){
        recordView.navigateTo(EditActivity.class);
      } else {
        recordView.navigateTo(GalleryActivity.class);
      }
    }
  }

  private boolean areThereVideosInProject() {
    return currentProject.getVMComposition().hasVideos();
  }

  private boolean areTherePendingTranscodingTask() {
    for (VideoToAdapt video : videoListToAdaptAndPosition) {
      if ((video.getVideo().getTranscodingTask() == null)
              || (!video.getVideo().getTranscodingTask().isDone())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onSuccessTranscoding(Video video) {
    if(isAVideoAdaptedToFormat(video)) {
      Log.d(TAG, "onSuccessTranscoding adapting video " + video.getMediaPath());
      addVideoRecordedToProject(video);
    } else {
      Log.d(TAG, "onSuccessTranscoding " + video.getTempPath());
      updateVideoRepositoryUseCase.succesTranscodingVideo(video);
    }
  }

  private void addVideoRecordedToProject(Video video) {
    String destVideoRecorded = Constants.PATH_APP_MASTERS +
        File.separator + new File(video.getMediaPath()).getName();
    int position = recordedVideosNumber;
    // (jliarte): 16/06/17 using iterator to avoid ConcurrentModificationException
    Iterator<VideoToAdapt> iter = videoListToAdaptAndPosition.iterator();
    while (iter.hasNext()) {
      VideoToAdapt videoToAdapt = iter.next();
      if (videoToAdapt.getVideo().getUuid().compareTo(video.getUuid()) == 0) {
        videoListToAdaptAndPosition.remove(videoToAdapt);
        position = videoToAdapt.getPosition() - 1;
      }
    }
    videoRecordedAdapted(video.getMediaPath(), destVideoRecorded, position);
  }

  private boolean isAVideoAdaptedToFormat(Video video) {
    String pathVideo = new File(video.getMediaPath()).getParent();
    if(pathVideo.compareTo(Constants.PATH_APP_TEMP) == 0){
      return true;
    }
    return false;
  }

  @Override
  public void onErrorTranscoding(Video video, String message) {
    if (isAVideoAdaptedToFormat(video)) {
      Log.d(TAG, "onErrorTranscoding adapting video " + video.getMediaPath() + " - " + message);
      if(numTriesAdaptingVideo < maxNumTriesAdaptingVideo) {
        moveAndAdaptRecordedVideo(video.getMediaPath());
        numTriesAdaptingVideo++;
      } else {
        // TODO:(alvaro.martinez) 24/05/17 How to manage this error adapting video ¿?
        addVideoRecordedToProject(video);
      }
    } else {
      Log.d(TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
      if(video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO){
        video.increaseNumTriesToExportVideo();
        Project currentProject = Project.getInstance(null, null, null);
        launchTranscoderAddAVTransitionUseCase.launchExportTempFile(context
                .getDrawable(R.drawable.alpha_transition_white), video,
            getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject(),
            currentProject.getProjectPathIntermediateFileAudioFade(), this);
      } else {
        updateVideoRepositoryUseCase.errorTranscodingVideo(video,
            Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
      }
    }
  }

  @Override
  public void videoToLaunchAVTransitionTempFile(Video video,
                                                String intermediatesTempAudioFadeDirectory) {
    video.setTempPath(currentProject.getProjectPathIntermediateFiles());

    videoFormat = currentProject.getVMComposition().getVideoFormat();
    drawableFadeTransitionVideo = context.getDrawable(R.drawable.alpha_transition_white);

    launchTranscoderAddAVTransitionUseCase.launchExportTempFile(drawableFadeTransitionVideo, video,
        videoFormat, intermediatesTempAudioFadeDirectory, this);
  }

  public void switchCamera() {
    if (!isFrontCameraSelected) {
      isFrontCameraSelected = true;
    } else {
      isFrontCameraSelected = false;
    }
    resetViewSwitchCamera();
    recordView.setCameraDefaultSettings();
    camera.switchCamera(isFrontCameraSelected);
    setupAdvancedCameraControls();
    userEventTracker.trackChangeCamera(isFrontCameraSelected);
  }

  private void resetViewSwitchCamera() {
    recordView.setZoom(0f);
    recordView.setFlash(false);
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

  private class VideoToAdapt {
    private final int position;
    private final Video video;

    public VideoToAdapt(Video video, int position) {
      this.video = video;
      this.position = position;
    }

    public int getPosition() {
      return position;
    }

    public Video getVideo() {
      return video;
    }
  }

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
    Constants.BATTERY_STATUS status=
        Constants.BATTERY_STATUS.UNKNOW;
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
    recordView.showFreeStorageSpace(memoryStatus, memoryFreePercent, freeMemoryInBytes, totalMemoryInBytes);
  }

  public int getPercentFreeBattery(long totalMemory, long freeMemory) {
    return Math.round(freeMemory / (float) totalMemory *100);
  }

  public Constants.MEMORY_STATUS getMemoryStatus(int freeMemoryPercent) {
    Constants.MEMORY_STATUS memoryStatus= Constants.MEMORY_STATUS.OKAY;
    if (freeMemoryPercent<25)
      memoryStatus= Constants.MEMORY_STATUS.CRITICAL;
    else if (freeMemoryPercent>=25 && freeMemoryPercent<75)
      memoryStatus= Constants.MEMORY_STATUS.MEDIUM;
    else  memoryStatus= Constants.MEMORY_STATUS.OKAY;
    return memoryStatus;
  }


  public String toFormattedMemorySpaceWithBytes(long memorySpace) {
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
