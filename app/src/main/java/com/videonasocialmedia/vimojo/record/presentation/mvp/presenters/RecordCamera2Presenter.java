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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.os.BatteryManager;
import android.util.Log;
import android.view.MotionEvent;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.BuildConfig;
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
import com.videonasocialmedia.vimojo.utils.Constants;
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

  // TODO:(alvaro.martinez) 26/01/17  ADD TRACKING TO RECORD ACTIVITY. Update from RecordActivity
  private final String TAG = RecordCamera2Presenter.class.getCanonicalName();
  private final Context context;
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

  public RecordCamera2Presenter(Context context, RecordCamera2View recordView,
                                AutoFitTextureView textureView,
                                String directorySaveVideos,
                                UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase,
                                LaunchTranscoderAddAVTransitionsUseCase
                                    launchTranscoderAddAVTransitionUseCase,
                                GetVideoFormatFromCurrentProjectUseCase
                                    getVideoFormatFromCurrentProjectUseCase,
                                AddVideoToProjectUseCase addVideoToProjectUseCase,
                                AdaptVideoRecordedToVideoFormatUseCase
                                    adaptVideoRecordedToVideoFormatUseCase) {
    this.context = context;
    this.recordView = recordView;
    this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
    this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionUseCase;
    this.getVideonaFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    this.addVideoToProjectUseCase = addVideoToProjectUseCase;
    initCameraWrapper(context, isFrontCameraSelected, textureView, directorySaveVideos,
        getVideoFormatFromCurrentProjectUseCase);
    this.adaptVideoRecordedToVideoFormatUseCase = adaptVideoRecordedToVideoFormatUseCase;
    this.currentProject = loadProject();
  }

  private void initCameraWrapper(
          Context context, boolean isFrontCameraSelected, AutoFitTextureView textureView,
          String directorySaveVideos,
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase) {
    int cameraId = 0;
    if (isFrontCameraSelected) {
      cameraId = 1;
    }
    // TODO:(alvaro.martinez) 25/01/17 Support camera1, api <21 or combine both. Make Camera1Wrapper
    camera = new Camera2Wrapper(context, this, cameraId, textureView, directorySaveVideos,
        getVideoFormatFromCurrentProjectUseCase.getVideoRecordedFormatFromCurrentProjectUseCase());
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
    // TODO(jliarte): 26/05/17 temporal workarround to force showing buttons
    if (BuildConfig.FEATURE_FORCE_PRO_CONTROLS_SHOW) {
      return;
    }
    if (!camera.ISOSelectionSupported()) {
      recordView.hideISOSelection();
    }
    if (!camera.advancedFocusSupported()) {
      recordView.hideAdvancedAFSelection();
    }
    if (!camera.whiteBalanceSelectionSupported()) {
      recordView.hideWhiteBalanceSelection();
    } else {
      recordView.setupWhiteBalanceSupportedModesButtons(camera.getSupportedWhiteBalanceModes().values);
    }
    if (!camera.metteringModeSelectionSupported()) {
      recordView.hideMetteringModeSelection();
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
  }

  public void onPause() {
    camera.onPause();
    recordView.stopMonitoringRotation();
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
        }
      });
    } catch (IllegalStateException illegalState) {
      // do nothing as it should be already managed in camera wrapper
    }
  }

  public void stopRecord() {
    try {
      camera.stopRecordVideo();
      updateStopVideoUI();
      onVideoRecorded(camera.getVideoPath());
      restartPreview();
    } catch (RuntimeException runtimeException) {
      // do nothing as it's already managed in camera wrapper
    }
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

    final Video videoToAdapt = new Video(origPath);
    videoListToAdaptAndPosition.add(new VideoToAdapt(videoToAdapt,recordedVideosNumber));

    // FIXME: 23/05/17 if rotation == 0, should be use getVideonaFormatToAdaptVideoRecordedAudio, more efficient.
    // Fix problems with profile MotoG, LG_pablo, ...
    VideonaFormat videonaFormat = getVideonaFormatFromCurrentProjectUseCase
        .getVideonaFormatToAdaptVideoRecordedAudioAndVideo();
    // FIXME: 24/05/17 AdaptVideo not need fadeTransition or isTransitionActivated, refactor SDK
    Drawable fadeTransition = context.getDrawable(R.drawable.alpha_transition_white);
    try {
      adaptVideoRecordedToVideoFormatUseCase.adaptVideo(videoToAdapt, videonaFormat,
          destVideoRecorded, camera.getRotation(),fadeTransition, false,this);
    } catch (IOException e) {
      e.printStackTrace();
      onErrorTranscoding(videoToAdapt, "adaptVideoRecordedToVideoFormatUseCase");
      recordView.hideProgressAdaptingVideo();
    }
  }

  private void videoRecordedAdapted(final String origVideoRecorded, String destVideoRecorded,
                                    int position) {
    addVideoToProjectUseCase.addVideoToProjectAtPosition(new Video(destVideoRecorded), position,
        new OnAddMediaFinishedListener() {
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

  public void onTouchFocus(MotionEvent event) {
    int x = Math.round(event.getX());
    int y = Math.round(event.getY());
    //camera.setFocus(calculateBounds(x, y), 100);
    try {
      camera.setFocus(x, y);
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.e(TAG, "Error focusing", e);
    }
    recordView.setFocus(event);
  }

  private Rect calculateBounds(int x, int y) {
    Rect focusIconBounds = new Rect();
    // TODO:(alvaro.martinez) 24/01/17 Define area to calculate autofocus
    int halfHeight = 100; // focusIcon.getIntrinsicHeight();
    int halfWidth = 100; //focusIcon.getIntrinsicWidth();
    focusIconBounds.set(x - halfWidth, y - halfHeight, x + halfWidth, y + halfHeight);
    return focusIconBounds;
  }

  public void navigateToEditOrGallery() {

    if(areTherePendingTranscodingTask()){
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
        Log.d(TAG, "onSuccessTranscoding position " + position);
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
    camera.switchCamera(isFrontCameraSelected);
  }

  private void resetViewSwitchCamera() {
    recordView.setZoom(0f);
    recordView.setFlash(false);   
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

  public void setWhiteBalanceMode(String whiteBalanceMode) {
    camera.setWhiteBalanceMode(whiteBalanceMode);
  }

  public void resetWhiteBalanceMode() {
    camera.resetWhiteBalanceMode();
  }

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