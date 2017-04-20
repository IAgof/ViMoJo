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
import android.util.Log;
import android.view.MotionEvent;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
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

public class RecordCamera2Presenter implements Camera2WrapperListener, TranscoderHelperListener {

  // TODO:(alvaro.martinez) 26/01/17  ADD TRACKING TO RECORD ACTIVITY. Update from RecordActivity
  private static final String LOG_TAG = "RecordPresenter";
  private final boolean isRightControlsViewSelected;
  private final boolean isPrincipalViewSelected;
  private RecordCamera2View recordView;
  private AddVideoToProjectUseCase addVideoToProjectUseCase;
  private AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase;
  private int recordedVideosNumber = 0;
  protected Project currentProject;
  private Camera2Wrapper camera;
  private List<VideoToAdapt> videoListToAdaptAndPosition = new ArrayList<>();
  private List<Video> videoList = new ArrayList<>();

  public RecordCamera2Presenter(Context context, RecordCamera2View recordView,
                                boolean isFrontCameraSelected, boolean isPrincipalViewSelected,
                                boolean isRightControlsViewSelected, AutoFitTextureView textureView,
                                String directorySaveVideos,
                                GetVideoFormatFromCurrentProjectUseCase
                                    getVideoFormatFromCurrentProjectUseCase,
                                AddVideoToProjectUseCase addVideoToProjectUseCase,
                                AdaptVideoRecordedToVideoFormatUseCase
                                    adaptVideoRecordedToVideoFormatUseCase) {
    this.recordView = recordView;
    this.isPrincipalViewSelected = isPrincipalViewSelected;
    this.isRightControlsViewSelected = isRightControlsViewSelected;
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
    restartPreview();
  }

  @Override
  public void setFlash(boolean state) {
    recordView.setFlash(state);
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

  @Override
  public void stopVideo(String path) {
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

    VideonaFormat videoFormat = new VideonaFormat(Constants.DEFAULT_VIMOJO_AUDIO_BITRATE,
        Constants.DEFAULT_VIMOJO_AUDIO_CHANNELS);

    try {
      adaptVideoRecordedToVideoFormatUseCase.adaptVideo(videoToAdapt, videoFormat,
              destVideoRecorded, this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void videoRecordedAdapted(String origVideoRecorded, String destVideoRecorded,
                                    int position) {
    addVideoToProjectUseCase.addVideoToTrackAtPosition(destVideoRecorded, position);
    Utils.removeVideo(origVideoRecorded);
    if (!areTherePendingTranscodingTask()) {
      recordView.hideProgressAdaptingVideo();
    }
  }

  @Override
  public void setZoom(Rect rectValue) {
    // TODO:(alvaro.martinez) 27/01/17 Convert zoom from 0 to 1 and show on RecordView
    //recordView.setZoom(0.5f);
  }

  public void restartPreview(){
    camera.onPause();
    camera.onResume();
  }

  public void setFlashOff() {
    camera.setFlashOff();
  }

  public void isFlashEnabled(boolean isSelected) {
    if (isSelected) {
      camera.setFlashOff();
    } else {
      camera.setFlashOn();
    }
  }

  public void onTouchZoom(MotionEvent event) {
    camera.onTouchZoom(getFingerSpacing(event));
    // RecordView show slide zoom, from 0 to 1
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
    camera.setFocus(calculateBounds(x, y), 100);
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
    if (areThereVideosInProject()) {
      if (areTherePendingTranscodingTask()) {
        recordView.showProgressAdaptingVideo();
      } else {
         recordView.navigateTo(EditActivity.class);
      }
    } else {
      recordView.navigateTo(GalleryActivity.class);
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
    String destVideoRecorded = Constants.PATH_APP_MASTERS +
        File.separator + new File(video.getMediaPath()).getName();
    int position = recordedVideosNumber;
    for(VideoToAdapt videoToAdapt: videoListToAdaptAndPosition){
      if(videoToAdapt.getVideo().getUuid().compareTo(video.getUuid()) == 0){
        position = videoToAdapt.getPosition() - 1;
        Log.d(LOG_TAG, "onSuccessTranscoding position " + position);
      }
    }
    videoRecordedAdapted(video.getMediaPath(), destVideoRecorded, position);
  }

  @Override
  public void onErrorTranscoding(Video video, String message) {
    // TODO(jliarte): 12/04/17 what should we do on error?
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