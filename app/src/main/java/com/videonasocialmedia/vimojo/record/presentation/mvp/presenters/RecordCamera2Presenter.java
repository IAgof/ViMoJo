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

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToTranscoderUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *  Created by alvaro on 16/01/17.
 */

public class RecordCamera2Presenter implements Camera2WrapperListener{

  // TODO:(alvaro.martinez) 26/01/17  ADD TRACKING TO RECORD ACTIVITY. Update from RecordActivity
  private static final String LOG_TAG = "RecordPresenter";
  private final boolean isRightControlsViewSelected;
  private final boolean isPrincipalViewSelected;
  private final GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
  private RecordCamera2View recordView;
  private AddVideoToProjectUseCase addVideoToProjectUseCase;
  private AdaptVideoRecordedToTranscoderUseCase adaptVideoRecordedToTranscoderUseCase;
  private int recordedVideosNumber = 0;
  protected Project currentProject;
  private int height = 720;
  private Camera2Wrapper camera;
  private String origVideoRecorded;
  private String destVideoRecorded;
  private int numVideoAdapting = 0;


  public RecordCamera2Presenter(Context context, RecordCamera2View recordView,
                                boolean isFrontCameraSelected, boolean isPrincipalViewSelected,
                                boolean isRightControlsViewSelected, AutoFitTextureView textureView,
                                String directorySaveVideos,
                                AddVideoToProjectUseCase addVideoToProjectUseCase) {

    this.recordView = recordView;
    this.isPrincipalViewSelected = isPrincipalViewSelected;
    this.isRightControlsViewSelected = isRightControlsViewSelected;
    getVideoFormatFromCurrentProjectUseCase = new GetVideoFormatFromCurrentProjectUseCase();
    int cameraId = 0;
    if(isFrontCameraSelected)
      cameraId = 1;
    // TODO:(alvaro.martinez) 25/01/17 Support camera1, api <21 or combine both. Make Camera1Wrapper
    camera = new Camera2Wrapper(context, this, cameraId, textureView, directorySaveVideos,
        getVideoFormatFromCurrentProjectUseCase.getVideoRecordedFormatFromCurrentProjectUseCase());

    this.addVideoToProjectUseCase = addVideoToProjectUseCase;
    adaptVideoRecordedToTranscoderUseCase = new AdaptVideoRecordedToTranscoderUseCase();

  }

  public void initViews() {
    recordView.setResolutionSelected(height);
    recordView.hideChronometer();
    if(isPrincipalViewSelected) {
      recordView.showPrincipalViews();
    }else {
      recordView.hidePrincipalViews();
    }
    if(isRightControlsViewSelected) {
      recordView.showRightControlsView();
    }else {
      recordView.hideRightControlsView();
    }
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
  public void videoRecorded(String path) {
    recordView.showRecordButton();
    recordView.showNavigateToSettingsActivity();
    recordView.stopChronometer();
    recordView.hideChronometer();
    recordView.showChangeCamera();
    recordView.showRecordedVideoThumb(path);
    recordView.showVideosRecordedNumber(++recordedVideosNumber);
    moveAndAdaptVideoRecorded(path);
  }

  private void moveAndAdaptVideoRecorded(String origPath) {

    File tempPath = new File(origPath);
    String finalPath = Constants.PATH_APP_MASTERS + File.separator + tempPath.getName();

    origVideoRecorded = origPath;
    destVideoRecorded = finalPath;

    numVideoAdapting++;
    Log.d(LOG_TAG, "adaptVideo " + numVideoAdapting);

    ListenableFuture transcodingJob = null;
    try {
      transcodingJob = adaptVideoRecordedToTranscoderUseCase.adaptVideo(origVideoRecorded, destVideoRecorded);
    } catch (IOException e) {
      e.printStackTrace();
    }

    waitTranscodingJobToFinish(transcodingJob);
    videosRecordedAdapted();

  }

  private void videosRecordedAdapted() {
    addVideoToProjectUseCase.addVideoToTrackAtPosition(destVideoRecorded, recordedVideosNumber);
    Utils.removeVideo(origVideoRecorded);
    recordView.hideProgressAdaptingVideo();
    numVideoAdapting--;
  }

  private void waitTranscodingJobToFinish(ListenableFuture future) {
    while(!future.isDone()){
      try {
        Thread.sleep(1000);
      } catch (InterruptedException exception) {
        exception.printStackTrace();
      }
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
    recordView.setFlash(false);
  }

  public void toggleFlash(boolean isSelected) {

    if(isSelected){
      camera.setFlashOff();
    } else {
      camera.setFlashOn();
    }
    recordView.setFlash(!isSelected);
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
    setFlashOff();
    if(recordedVideosNumber > 0){
        if(numVideoAdapting > 0){
          recordView.showProgressAdaptingVideo();
        } else {
          recordView.navigateTo(EditActivity.class);
        }

    } else {
      recordView.navigateTo(GalleryActivity.class);
    }
  }
}

