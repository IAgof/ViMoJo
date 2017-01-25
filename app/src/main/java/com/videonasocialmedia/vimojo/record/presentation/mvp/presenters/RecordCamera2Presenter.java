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
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
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

/**
 *  Created by alvaro on 16/01/17.
 */

public class RecordCamera2Presenter implements Camera2WrapperListener {

  /**
   * LOG_TAG
   */
  private static final String LOG_TAG = "RecordPresenter";
  private RecordCamera2View recordView;
  private AddVideoToProjectUseCase addVideoToProjectUseCase;
  private int recordedVideosNumber;
  private MixpanelAPI mixpanel;
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor preferencesEditor;
  private String resolution;
  private Context context;
  protected Project currentProject;
  private int height = 720;

  private boolean externalIntent;

  private Camera2Wrapper camera;

 /* @Inject
  public RecordCamera2Presenter(Context context, RecordCamera2View recordView,
                                SharedPreferences sharedPreferences,
                                boolean externalIntent,
                                AddVideoToProjectUseCase addVideoToProjectUseCase) {

    this.context = context;
    this.recordView = recordView;
    this.sharedPreferences = sharedPreferences;
    this.externalIntent = externalIntent;
    this.addVideoToProjectUseCase = addVideoToProjectUseCase;

    this.currentProject = loadCurrentProject();
    preferencesEditor = sharedPreferences.edit();
    recordedVideosNumber = 0;
    mixpanel = MixpanelAPI.getInstance(context, BuildConfig.MIXPANEL_TOKEN);
  }*/

  public RecordCamera2Presenter(RecordCamera2View recordView, Context context,
                                boolean isFrontCameraSelected, boolean isPrincipalViewSelected,
                                boolean isRightControlsViewSelected, AutoFitTextureView textureView,
                                boolean externalIntent) {

    this.recordView = recordView;
    this.context = context;
    int cameraId = 0;
    if(isFrontCameraSelected)
      cameraId = 1;
    // TODO:(alvaro.martinez) 25/01/17 Support camera1, api <21 or combine both. Make Camera1Wrapper
    camera = new Camera2Wrapper(context, this, cameraId, textureView, Constants.PATH_APP_MASTERS);
    this.externalIntent = externalIntent;

    addVideoToProjectUseCase = new AddVideoToProjectUseCase(new ProjectRealmRepository());
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

  public Project loadCurrentProject() {
    // TODO(jliarte): this should make use of a repository or use case to load the Project
    return Project.getInstance(null, null, null);
  }

  // Save file if user go to home without stop video
  // Move to master last video recorded temp.
  // Video temp has to be bigger than 1MB to consider is video file
  // TODO:(alvaro.martinez) 18/01/17 Check user go to home while recording and verify if this method has sense
  private void checkLastTempFileRecordVideo() {

    String tempFileName = Constants.PATH_APP_TEMP + File.separator + Constants.VIDEO_TEMP_RECORD_FILENAME;
    File vTemp = new File(tempFileName);

    if (vTemp.exists() && vTemp.length() > 1024 * 1024) {

      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String fileName = "VID_" + timeStamp + ".mp4";
      String destinationFile = Constants.PATH_APP_MASTERS + File.separator + fileName;
      try {
        Utils.moveFile(tempFileName, destinationFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
      Utils.addFileToVideoGallery(destinationFile);
    }
  }

  private void hideInitialsButtons() {
    recordView.hideChronometer();
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
    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
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


  /**
   * Sends button clicks to Mixpanel Analytics
   *
   * @param interaction
   * @param result
   */
  // TODO:(alvaro.martinez) 18/01/17 Review tracking RecordActivity
  private void trackUserInteracted(String interaction, String result) {
      /*  JSONObject userInteractionsProperties = new JSONObject();
        try {
            userInteractionsProperties.put(AnalyticsConstants.ACTIVITY, context.getClass().getSimpleName());
            userInteractionsProperties.put(AnalyticsConstants.RECORDING, recorder.isRecording());
            userInteractionsProperties.put(AnalyticsConstants.INTERACTION, interaction);
            userInteractionsProperties.put(AnalyticsConstants.RESULT, result);
            mixpanel.track(AnalyticsConstants.USER_INTERACTED, userInteractionsProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        } */
  }


  public void startRecord() {

    camera.startRecordingVideo();

    //mixpanel.timeEvent(AnalyticsConstants.VIDEO_RECORDED);
   // trackUserInteracted(AnalyticsConstants.RECORD, AnalyticsConstants.START);

    recordView.showStopButton();
    recordView.startChronometer();
    recordView.showChronometer();
    recordView.hideNavigateToSettingsActivity();
    recordView.hideVideosRecordedNumber();
    recordView.hideRecordedVideoThumb();
    recordView.hideChangeCamera();
  }

  public void stopRecord() {
    camera.stopRecordingVideo();
  }


  // TODO:(alvaro.martinez) 18/01/17 Check if it is necessary
  private String moveVideoToMastersFolder() {

    String originalFile = " "; //config.getOutputPath();
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "VID_" + timeStamp + ".mp4";
    String destinationFile = Constants.PATH_APP_MASTERS + File.separator + fileName;
    try {
      Utils.moveFile(originalFile, destinationFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Utils.addFileToVideoGallery(destinationFile);

    int numTotalVideosRecorded = sharedPreferences
        .getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
    preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED,
        ++numTotalVideosRecorded);
    preferencesEditor.commit();
    trackTotalVideosRecordedSuperProperty();
    double clipDuration = 0.0;
    try {
      clipDuration = Utils.getFileDuration(destinationFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    trackVideoRecorded(clipDuration);
    return destinationFile;
  }

  private void trackTotalVideosRecordedSuperProperty() {
    JSONObject totalVideoRecordedSuperProperty = new JSONObject();
    int numPreviousVideosRecorded;
    try {
      numPreviousVideosRecorded =
          mixpanel.getSuperProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_RECORDED);
    } catch (JSONException e) {
      numPreviousVideosRecorded = 0;
    }
    try {
      totalVideoRecordedSuperProperty.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
          ++numPreviousVideosRecorded);
      mixpanel.registerSuperProperties(totalVideoRecordedSuperProperty);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void trackVideoRecorded(Double clipDuration) {
    JSONObject videoRecordedProperties = new JSONObject();
    //resolution = config.getVideoWidth() + "x" + config.getVideoHeight();
    int totalVideosRecorded = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
    try {
      videoRecordedProperties.put(AnalyticsConstants.VIDEO_LENGTH, clipDuration);
      videoRecordedProperties.put(AnalyticsConstants.RESOLUTION, resolution);
      videoRecordedProperties.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
          totalVideosRecorded);
      mixpanel.track(AnalyticsConstants.VIDEO_RECORDED, videoRecordedProperties);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    trackVideoRecordedUserTraits();
  }

  private void trackVideoRecordedUserTraits() {
        /* TODO: why do we update quality and resolution on video recorded?? This should be only updated in settings
        JSONObject userProfileProperties = new JSONObject();
        try {
            userProfileProperties.put(AnalyticsConstants.RESOLUTION, sharedPreferences.getString(
                    AnalyticsConstants.RESOLUTION, resolution));
            userProfileProperties.put(AnalyticsConstants.QUALITY,
                    sharedPreferences.getInt(AnalyticsConstants.QUALITY, config.getVideoBitRate()));
            mixpanel.getPeople().set(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_VIDEOS_RECORDED, 1);
    mixpanel.getPeople().set(AnalyticsConstants.LAST_VIDEO_RECORDED,
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
  }


  public int getProjectDuration() {
    return Project.getInstance(null, null, null).getDuration();
  }

  public int getNumVideosOnProject() {
    return recordedVideosNumber;
  }

  // TODO:(alvaro.martinez) 18/01/17 Check flash support, hardwareCameraRepository?
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
      camera.onPause();
      camera.onResume();
    }
  }

  public void setFlashOff() {
    camera.setFlashOff();
    recordView.setFlash(false);
  }

  public void toggleFlash(boolean isSelected) {
   /* if(!isFlashActivated) {
      camera.setFlashOn();
      isFlashActivated = true;
    } else {
      camera.setFlashOff();
      isFlashActivated = false;
    }
    recordView.setFlash(isFlashActivated);*/
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
    //camera.setFocus(calculateBounds(x, y));
    camera.setFocus(calculateBounds(x, y), 100);
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

