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
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.camera.utils.VideoFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 *  Created by alvaro on 16/01/17.
 */

public class RecordCamera2Presenter implements Camera2WrapperListener {

  // TODO:(alvaro.martinez) 26/01/17  ADD TRACKING TO RECORD ACTIVITY. Update from RecordActivity
  private static final String LOG_TAG = "RecordPresenter";
  private final Context context;
  private RecordCamera2View recordView;
  private AddVideoToProjectUseCase addVideoToProjectUseCase;
  private int recordedVideosNumber;
  protected Project currentProject;
  private int height = 720;
  private boolean externalIntent;
  private Camera2Wrapper camera;

  @Inject
  public RecordCamera2Presenter(Context context, RecordCamera2View recordView,
                                boolean isFrontCameraSelected, boolean isPrincipalViewSelected,
                                boolean isRightControlsViewSelected, AutoFitTextureView textureView,
                                boolean externalIntent, GetVideoFormatFromCurrentProjectUseCase
                                getVideoFormatFromCurrentProjectUseCase, AddVideoToProjectUseCase
                                addVideoToProjectUseCase) {

    this.recordView = recordView;
    this.context = context;
    this.externalIntent = externalIntent;
    int cameraId = 0;
    if(isFrontCameraSelected)
      cameraId = 1;
    // TODO:(alvaro.martinez) 25/01/17 Support camera1, api <21 or combine both. Make Camera1Wrapper
    camera = new Camera2Wrapper(context, this, cameraId, textureView, Constants.PATH_APP_MASTERS,
        getVideoFormatFromCurrentProjectUseCase.getVideoRecordedFormatFromCurrentProjectUseCase());

    this.addVideoToProjectUseCase = addVideoToProjectUseCase;
    initViews(recordView, isPrincipalViewSelected, isRightControlsViewSelected);
  }

  private void initViews(RecordCamera2View recordView, boolean isPrincipalViewSelected, boolean
      isRightControlsViewSelected) {
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
    if (!externalIntent)
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
  public void videoRecorded(String path){
    if (externalIntent) {
      recordView.finishActivityForResult(path);
    } else {
      addVideoToProjectUseCase.addVideoToTrack(path);
      recordView.showRecordButton();
      recordView.showNavigateToSettingsActivity();
      recordView.stopChronometer();
      recordView.hideChronometer();
      recordView.showChangeCamera();
      recordView.showRecordedVideoThumb(path);
      recordView.showVideosRecordedNumber(++recordedVideosNumber);
    }
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

  public void onTouch(MotionEvent event) {
    camera.onTouch(event);
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
  }

  private Rect calculateBounds(int x, int y) {
    Rect focusIconBounds = new Rect();
    // TODO:(alvaro.martinez) 24/01/17 Define area to calculate autofocus
    int halfHeight = 100; // focusIcon.getIntrinsicHeight();
    int halfWidth = 100; //focusIcon.getIntrinsicWidth();
    focusIconBounds.set(x - halfWidth, y - halfHeight, x + halfWidth, y + halfHeight);
    return focusIconBounds;
  }
}

