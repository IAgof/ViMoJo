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
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
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

public class RecordCamera2Presenter {

  /**
   * LOG_TAG
   */
  private static final String LOG_TAG = "RecordPresenter";
  private boolean firstTimeRecording;
  private RecordCamera2View recordView;
  private AddVideoToProjectUseCase addVideoToProjectUseCase;
  private int recordedVideosNumber;
  private MixpanelAPI mixpanel;
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor preferencesEditor;
  private String resolution;
  private Context context;
  protected Project currentProject;
  private int height;

  private boolean externalIntent;

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

  public RecordCamera2Presenter(RecordCamera2View recordView){

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
    mixpanel.timeEvent(AnalyticsConstants.VIDEO_RECORDED);
    trackUserInteracted(AnalyticsConstants.RECORD, AnalyticsConstants.START);

    recordView.showStopButton();
    recordView.startChronometer();
    recordView.showChronometer();
    recordView.hideSettingsOptions();
    recordView.hideVideosRecordedNumber();
    recordView.hideRecordedVideoThumb();
    firstTimeRecording = false;
  }

  public void stopRecord() {
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

  public void changeCamera(int cameraId) {
    if (cameraId == 0) {
      recordView.showBackCameraSelected();

    } else {

      if (cameraId == 1) {
        recordView.showFrontCameraSelected();
      }
    }
    checkFlashSupport();
  }

  // TODO:(alvaro.martinez) 18/01/17 Check flash support, hardwareCameraRepository?
  public void checkFlashSupport() {

    // Check flash support
    int flashSupport = 0; //recorder.checkSupportFlash(); // 0 true, 1 false, 2 ignoring, not prepared

    Log.d(LOG_TAG, "checkSupportFlash flashSupport " + flashSupport);

    if (flashSupport == 0) {
      recordView.showFlashSupported(true);
      Log.d(LOG_TAG, "checkSupportFlash flash Supported camera");
    } else {
      if (flashSupport == 1) {
        recordView.showFlashSupported(false);
        Log.d(LOG_TAG, "checkSupportFlash flash NOT Supported camera");
      }
    }
  }

  public void setFlashOff() {
    boolean on = false; //recorder.setFlashOff();
    recordView.showFlashOn(on);
  }

  public void toggleFlash() {
    boolean on =  false; //recorder.toggleFlash();
    recordView.showFlashOn(on);
  }

}

