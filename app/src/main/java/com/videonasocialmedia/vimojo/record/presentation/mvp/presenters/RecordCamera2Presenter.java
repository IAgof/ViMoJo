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
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by alvaro on 16/01/17.
 */

public class RecordCamera2Presenter implements Camera2WrapperListener,
    OnLaunchAVTransitionTempFileListener, TranscoderHelperListener {

  // TODO:(alvaro.martinez) 26/01/17  ADD TRACKING TO RECORD ACTIVITY. Update from RecordActivity
  private static final String LOG_TAG = "RecordCamera2Presenter";
  private final boolean isRightControlsViewSelected;
  private final boolean isPrincipalViewSelected;
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

  public RecordCamera2Presenter(Context context, RecordCamera2View recordView,
                                boolean isFrontCameraSelected, boolean isPrincipalViewSelected,
                                boolean isRightControlsViewSelected, AutoFitTextureView textureView,
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
    this.isPrincipalViewSelected = isPrincipalViewSelected;
    this.isRightControlsViewSelected = isRightControlsViewSelected;
    this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
    this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionUseCase;
    this.getVideonaFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    this.addVideoToProjectUseCase = addVideoToProjectUseCase;
    initCameraWrapper(context, isFrontCameraSelected, textureView, directorySaveVideos, getVideoFormatFromCurrentProjectUseCase);
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
    if (isPrincipalViewSelected) {
      recordView.showPrincipalViews();
    } else {
      recordView.hidePrincipalViews();
    }
    if (isRightControlsViewSelected) {
      recordView.showRightControlsView();
    } else {
      recordView.hideRightControlsView();
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
    Log.d(LOG_TAG, "resume presenter");
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
      recordView.showRecordedVideoThumb(lastItem.getMediaPath());
    } else {
      recordView.hideVideosRecordedNumber();
    }
  }

  public void startRecord() {
    camera.startRecordingVideo();
    recordView.showStopButton();
    recordView.startChronometer();
    recordView.showChronometer();
    recordView.hideNavigateToSettingsActivity();
    recordView.hideVideosRecordedNumber();
    recordView.hideRecordedVideoThumb();
    recordView.hideChangeCamera();
  }

  public void stopRecord() {
    camera.stopRecordVideo();
    stopVideo(camera.getVideoPath());
    restartPreview();
  }

  @Override
  public void setFlashSupport() {
    if (camera.isFlashSupported()) {
      recordView.setFlashSupported(true);
      Log.d(LOG_TAG, "checkSupportFlash flash Supported camera");
    } else {
       recordView.setFlashSupported(false);
      Log.d(LOG_TAG, "checkSupportFlash flash NOT Supported camera");
    }
  }


  private void stopVideo(String path) {
    recordView.showRecordButton();
    recordView.showNavigateToSettingsActivity();
    recordView.stopChronometer();
    recordView.hideChronometer();
    recordView.showChangeCamera();
    recordView.showRecordedVideoThumb(path);
    recordView.showVideosRecordedNumber(++recordedVideosNumber);
    moveAndAdaptRecordedVideo(path);
  }

  private void moveAndAdaptRecordedVideo(String origPath) {
    File tempPath = new File(origPath);
    String destVideoRecorded = Constants.PATH_APP_MASTERS + File.separator + tempPath.getName();

    final Video videoToAdapt = new Video(origPath);
    videoListToAdaptAndPosition.add(new VideoToAdapt(videoToAdapt,recordedVideosNumber));

    VideonaFormat videonaFormat = getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatToAdaptVideo();

    try {
      adaptVideoRecordedToVideoFormatUseCase.adaptVideo(videoToAdapt, videonaFormat,
          destVideoRecorded, this);
    } catch (IOException e) {
      e.printStackTrace();
      onErrorTranscoding(videoToAdapt, "adaptVideoRecordedToVideoFormatUseCase");
    }
  }

  private void videoRecordedAdapted(final String origVideoRecorded, String destVideoRecorded,
                                    int position) {
    addVideoToProjectUseCase.addVideoToProjectAtPosition(new Video(destVideoRecorded), position,
        new OnAddMediaFinishedListener() {
      @Override
      public void onAddMediaItemToTrackError() {
        recordView.hideProgressAdaptingVideo();
        recordView.showError(R.string.addMediaItemToTrackError);
      }

      @Override
      public void onAddMediaItemToTrackSuccess(Media media) {
        Utils.removeVideo(origVideoRecorded);
        if (!areTherePendingTranscodingTask() || videoListToAdaptAndPosition.size() == 0) {
          recordView.hideProgressAdaptingVideo();
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
  public void setZoom(Rect rectValue) {
    // TODO:(alvaro.martinez) 27/01/17 Convert zoom from 0 to 1 and show on RecordView
    //recordView.setZoom(0.5f);
  }

  public void restartPreview(){
    if(!camera.isRecordingVideo()) {
      camera.onPause();
      camera.onResume();
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

  public void bottomSettingsCamera(boolean isSelected) {
    if(isSelected) {
      recordView.hideBottomControlsView();
    } else {
      recordView.showBottomControlsView();
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
      Log.d(LOG_TAG, "showProgressAdaptingVideo");
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
      if (!video.getVideo().getTranscodingTask().isDone()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onSuccessTranscoding(Video video) {
    if(haveJustBeenVideoAdapted(video)) {
      String destVideoRecorded = Constants.PATH_APP_MASTERS +
          File.separator + new File(video.getMediaPath()).getName();
      int position = recordedVideosNumber;
      for (VideoToAdapt videoToAdapt : videoListToAdaptAndPosition) {
        if (videoToAdapt.getVideo().getUuid().compareTo(video.getUuid()) == 0) {
          videoListToAdaptAndPosition.remove(videoToAdapt);
          position = videoToAdapt.getPosition() - 1;
          Log.d(LOG_TAG, "onSuccessTranscoding position " + position);
        }
      }
      videoRecordedAdapted(video.getMediaPath(), destVideoRecorded, position);

    } else {
      Log.d(LOG_TAG, "onSuccessTranscoding adapting video to format" + video.getTempPath());
      updateVideoRepositoryUseCase.succesTranscodingVideo(video);
    }
  }

  private boolean haveJustBeenVideoAdapted(Video video) {
    String pathVideo = new File(video.getMediaPath()).getParent();
    if(pathVideo.compareTo(Constants.PATH_APP_TEMP) == 0){
      return true;
    }
    return false;
  }

  @Override
  public void onErrorTranscoding(Video video, String message) {

    if(haveJustBeenVideoAdapted(video)) {
      Log.d(LOG_TAG, "onErrorTranscoding adapting video " + video.getMediaPath() + " - " + message);
    } else {
      Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
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

    launchTranscoderAddAVTransitionUseCase.launchExportTempFile(drawableFadeTransitionVideo, video, videoFormat,
        intermediatesTempAudioFadeDirectory, this);

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

}