/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.repository.upload.UploadDataSource;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.OnRelaunchTemporalFileListener;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter extends VimojoPresenter
    implements SharedPreferences.OnSharedPreferenceChangeListener, OnRelaunchTemporalFileListener {
  private static final String LOG_TAG = PreferencesPresenter.class.getSimpleName();
  private final UploadDataSource uploadRepository;
  private final ProjectInstanceCache projectInstanceCache;
  private Context context;
  private UserEventTracker userEventTracker;
  private SharedPreferences sharedPreferences;
  private PreferencesView preferencesView;
  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
  private UpdateAudioTransitionPreferenceToProjectUseCase
      updateAudioTransitionPreferenceToProjectUseCase;
  private UpdateVideoTransitionPreferenceToProjectUseCase
      updateVideoTransitionPreferenceToProjectUseCase;
  private UpdateIntermediateTemporalFilesTransitionsUseCase
      updateIntermediateTemporalFilesTransitionsUseCase;
  private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;
  private Project currentProject;
  private UpdateComposition updateComposition;
  private boolean ftpPublishingAvailable;
  private boolean hideTransitionPreference;
  private boolean showMoreAppsPreference;

  /**
   * Constructor
   *
   * @param preferencesView
   * @param context
   * @param sharedPreferences
   * @param updateComposition
   * @param ftpPublishingAvailable
   * @param hideTransitionPreference
   */
  public PreferencesPresenter(
      PreferencesView preferencesView, Context context, SharedPreferences sharedPreferences,
      GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
      UpdateAudioTransitionPreferenceToProjectUseCase
          updateAudioTransitionPreferenceToProjectUseCase,
      UpdateVideoTransitionPreferenceToProjectUseCase
          updateVideoTransitionPreferenceToProjectUseCase,
      UpdateIntermediateTemporalFilesTransitionsUseCase
          updateIntermediateTemporalFilesTransitionsUseCase,
      RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
      UploadDataSource uploadRepository, ProjectInstanceCache projectInstanceCache,
      UserEventTracker userEventTracker, UpdateComposition updateComposition,
      @Named("ftpPublishingAvailable") boolean ftpPublishingAvailable,
      @Named("hideTransitionPreference") boolean hideTransitionPreference,
      @Named("showMoreAppsPreference") boolean showMoreAppsPreference,
      BackgroundExecutor backgroundExecutor) {
    super(backgroundExecutor, userEventTracker);
    this.preferencesView = preferencesView;
    this.context = context;
    this.sharedPreferences = sharedPreferences;
    this.getPreferencesTransitionFromProjectUseCase =
        getPreferencesTransitionFromProjectUseCase;
    this.updateAudioTransitionPreferenceToProjectUseCase =
        updateAudioTransitionPreferenceToProjectUseCase;
    this.updateVideoTransitionPreferenceToProjectUseCase =
        updateVideoTransitionPreferenceToProjectUseCase;
    this.updateIntermediateTemporalFilesTransitionsUseCase =
        updateIntermediateTemporalFilesTransitionsUseCase;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.userEventTracker = userEventTracker;
    this.projectInstanceCache = projectInstanceCache;
    this.uploadRepository = uploadRepository;
    this.updateComposition = updateComposition;
    this.ftpPublishingAvailable = ftpPublishingAvailable;
    this.hideTransitionPreference = hideTransitionPreference;
    this.showMoreAppsPreference = showMoreAppsPreference;
  }

  public void updatePresenter(Activity activity) {
    this.currentProject = projectInstanceCache.getCurrentProject();
    setupTransitions();
    checkAvailablePreferences();
    setupMoreApps();
  }

  private void setupTransitions() {
    if (hideTransitionPreference) {
      preferencesView.hideTransitions();
    }
  }

  private void setupMoreApps() {
    if (showMoreAppsPreference) {
      preferencesView.showMoreAppsSection();
    } else {
      preferencesView.hideMoreAppsSection();
    }
  }

  /**
   * Checks the available preferences on the device
   */

  public void checkAvailablePreferences() {
    setupFtpPreferences();
    checkTransitions();
  }

  private void setupFtpPreferences() {
    if (ftpPublishingAvailable) {
      checkUserFTP1Data();
      // TODO:(alvaro.martinez) 12/01/18 Now we only use one FTP, not two. Implement feature, I want to add more FTPs
      //checkUserFTP2Data();
    } else {
      // Visibility FTP gone
      preferencesView.hideFtpsViews();
    }
  }

  private void checkTransitions() {
    checkTransitionPreference(ConfigPreferences.TRANSITION_VIDEO);
    checkTransitionPreference(ConfigPreferences.TRANSITION_AUDIO);
  }

  private void checkTransitionPreference(String key) {
    boolean data = false;
    if (key.compareTo(ConfigPreferences.TRANSITION_AUDIO) == 0) {
      data = getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated(currentProject);
      preferencesView.setTransitionsPref(key, data);
    } else {
      if (key.compareTo(ConfigPreferences.TRANSITION_VIDEO) == 0) {
        data = getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated(currentProject);
        preferencesView.setTransitionsPref(key, data);
      }
    }
  }

  private void checkUserFTP1Data() {
    checkUserFTPPreference(ConfigPreferences.HOST);
    checkUserFTPPreference(ConfigPreferences.USERNAME_FTP);
    checkUserFTPPreference(ConfigPreferences.EDITED_VIDEO_DESTINATION);
    checkUserFTPPreference(ConfigPreferences.UNEDITED_VIDEO_DESTINATION);
  }

  private void checkUserFTP2Data() {
    checkUserFTPPreference(ConfigPreferences.HOST_FTP2);
    checkUserFTPPreference(ConfigPreferences.USERNAME_FTP2);
    checkUserFTPPreference(ConfigPreferences.EDITED_VIDEO_DESTINATION_FTP2);
    checkUserFTPPreference(ConfigPreferences.UNEDITED_VIDEO_DESTINATION_FTP2);
  }

  private void checkUserFTPPreference(String key) {
    String data = sharedPreferences.getString(key, null);
    if (data != null && !data.isEmpty()) {
      preferencesView.setSummary(key, data);
    }
  }

  /**
   * Checks if the actual default value in shared preferences is supported by the device
   *
   * @param key    the key of the shared preference
   * @param values the supported values for this preference
   * @return return true if the default value is not supported by the device, so update it
   */
  private boolean updateDefaultPreference(String key, ArrayList<String> values) {
    boolean result = false;
    String actualDefaultValue = sharedPreferences.getString(key, "");
    if (!values.contains(actualDefaultValue)) {
      result = true;
    }
    return result;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    switch (key) {
      case ConfigPreferences.TRANSITION_AUDIO:
        boolean dataTransitionAudio = sharedPreferences.getBoolean(key, false);
        updateAudioTransitionPreferenceToProjectUseCase
            .setAudioFadeTransitionActivated(currentProject, dataTransitionAudio);
        executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
        updateIntermediateTemporalFilesTransitionsUseCase.execute(currentProject, this);
        break;
      case ConfigPreferences.TRANSITION_VIDEO:
        boolean dataTransitionVideo = sharedPreferences.getBoolean(key, false);
        updateVideoTransitionPreferenceToProjectUseCase
            .setVideoFadeTransitionActivated(currentProject, dataTransitionVideo);
        executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
        updateIntermediateTemporalFilesTransitionsUseCase.execute(currentProject, this);
        break;
      default:
    }
  }

  @Override
  public void videoToRelaunch(String videoUuid, String intermediatesTempAudioFadeDirectory) {
    final Video video = getVideo(videoUuid);
    executeUseCaseCall(() -> relaunchTranscoderTempBackgroundUseCase
            .relaunchExport(video, currentProject));
  }

  private Video getVideo(String videoId) {
    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase =
        new GetMediaListFromProjectUseCase();
    List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject(currentProject);
    if (videoList != null) {
      for (Media video : videoList) {
        if (((Video) video).getUuid().compareTo(videoId) == 0) {
          return (Video) video;
        }
      }
    }
    return null;
  }

  private void deletePendingVideosToUpload() {
    if (uploadRepository.getAllVideosToUpload().size() > 0) {
      uploadRepository.removeAllVideosToUpload();
    }
  }

  public void trackQualityAndResolutionAndFrameRateUserTraits(String key, String value) {
    switch (key) {
      case ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION:
        userEventTracker.trackResolutionUserTraits(value);
        break;
      case ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY:
        userEventTracker.trackQualityUserTraits(value);
        break;
      case ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE:
        userEventTracker.trackFrameRateUserTraits(value);
        break;
    }
  }

}
