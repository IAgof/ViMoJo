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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.Log;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.result.Credentials;
import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth0.accountmanager.GetAccount;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateCompositionWatermark;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.upload.UploadDataSource;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.OnRelaunchTemporalFileListener;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.store.billing.PlayStoreBillingDelegate;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter extends VimojoPresenter
    implements SharedPreferences.OnSharedPreferenceChangeListener,
    OnRelaunchTemporalFileListener, PlayStoreBillingDelegate.BillingDelegateView {
  private static final String LOG_TAG = PreferencesPresenter.class.getSimpleName();
  private final BillingManager billingManager;
  private UserAuth0Helper userAuth0Helper;
  private final UploadDataSource uploadRepository;
  private final ProjectInstanceCache projectInstanceCache;
  private PlayStoreBillingDelegate playStoreBillingDelegate;
  private Context context;
  private UserEventTracker userEventTracker;
  private SharedPreferences sharedPreferences;
  private PreferencesView preferencesView;
  private Preference transitionVideoPref;
  private Preference transitionAudioPref;
  private Preference watermarkPref;
  private Preference themeApp;
  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
  private UpdateAudioTransitionPreferenceToProjectUseCase
      updateAudioTransitionPreferenceToProjectUseCase;
  private UpdateVideoTransitionPreferenceToProjectUseCase
      updateVideoTransitionPreferenceToProjectUseCase;
  private UpdateIntermediateTemporalFilesTransitionsUseCase
      updateIntermediateTemporalFilesTransitionsUseCase;
  private UpdateCompositionWatermark updateCompositionWatermark;
  private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;
  private GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
  private GetAccount getAccount;
  private Project currentProject;
  private UpdateComposition updateComposition;

  /**
   * Constructor
   *  @param preferencesView
   * @param context
   * @param sharedPreferences
   * @param userAuth0Helper
   * @param updateComposition
   */
  public PreferencesPresenter(
          PreferencesView preferencesView, Context context, SharedPreferences sharedPreferences,
          Preference transitionVideoPref, Preference themeApp, Preference transitionAudioPref,
          Preference watermarkPref, GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          UpdateAudioTransitionPreferenceToProjectUseCase
                  updateAudioTransitionPreferenceToProjectUseCase,
          UpdateVideoTransitionPreferenceToProjectUseCase
                  updateVideoTransitionPreferenceToProjectUseCase,
          UpdateIntermediateTemporalFilesTransitionsUseCase
                  updateIntermediateTemporalFilesTransitionsUseCase,
          UpdateCompositionWatermark updateCompositionWatermark,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          BillingManager billingManager, UserAuth0Helper userAuth0Helper,
          UploadDataSource uploadRepository, ProjectInstanceCache projectInstanceCache,
          GetAccount getAccount, UpdateComposition updateComposition) {
    this.preferencesView = preferencesView;
    this.context = context;
    this.sharedPreferences = sharedPreferences;
    this.transitionVideoPref = transitionVideoPref;
    this.transitionAudioPref = transitionAudioPref;
    this.watermarkPref = watermarkPref;
    this.themeApp = themeApp;
    this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
    this.getPreferencesTransitionFromProjectUseCase =
        getPreferencesTransitionFromProjectUseCase;
    this.updateAudioTransitionPreferenceToProjectUseCase =
        updateAudioTransitionPreferenceToProjectUseCase;
    this.updateVideoTransitionPreferenceToProjectUseCase =
        updateVideoTransitionPreferenceToProjectUseCase;
    this.updateIntermediateTemporalFilesTransitionsUseCase =
        updateIntermediateTemporalFilesTransitionsUseCase;
    this.updateCompositionWatermark = updateCompositionWatermark;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.getVideoFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    userEventTracker = UserEventTracker.getInstance(MixpanelAPI
        .getInstance(context.getApplicationContext(), BuildConfig.MIXPANEL_TOKEN));
    this.billingManager = billingManager;
    this.playStoreBillingDelegate = new PlayStoreBillingDelegate(billingManager, this);
    this.projectInstanceCache = projectInstanceCache;
    this.uploadRepository = uploadRepository;
    this.userAuth0Helper = userAuth0Helper;
    this.getAccount = getAccount;
    this.updateComposition = updateComposition;
  }

  public void updatePresenter(Activity activity) {
    this.currentProject = projectInstanceCache.getCurrentProject();
    checkAvailablePreferences();
    checkVimojoStore(activity);
    setupUserAuthPreference();
    setupMoreApps();
  }

  private void setupMoreApps() {
    if (BuildConfig.FEATURE_SHOW_MORE_APPS) {
      preferencesView.showMoreAppsSection();
    } else {
      preferencesView.hideMoreAppsSection();
    }
  }

  public void pausePresenter() {
    if (BuildConfig.VIMOJO_STORE_AVAILABLE) {
      billingManager.destroy();
    }
  }

  /**
   * Checks the available preferences on the device
   */

  public void checkAvailablePreferences() {
    if (BuildConfig.FEATURE_FTP) {
      checkUserFTP1Data();
      // TODO:(alvaro.martinez) 12/01/18 Now we only use one FTP, not two. Implement feature, I want to add more FTPs
      //checkUserFTP2Data();
    } else {
      // Visibility FTP gone
      preferencesView.hideFtpsViews();
    }
    checkTransitions();
    checkWatermark();
    checkThemeApp(ConfigPreferences.THEME_APP_DARK);
  }

  private void checkThemeApp(String key) {
    preferencesView.setThemeDarkAppPref(key, sharedPreferences.getBoolean(key, false));
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

  private void checkWatermark() {
    if (BuildConfig.FEATURE_WATERMARK_SWITCH && !BuildConfig.FEATURE_FORCE_WATERMARK) {
      boolean data = currentProject.hasWatermark();
      preferencesView.setWatermarkSwitchPref(data);
    } else {
      preferencesView.hideWatermarkView();
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
      case ConfigPreferences.WATERMARK:
        boolean data = sharedPreferences.getBoolean(key, false);
        if (data && !(new File(Constants.PATH_WATERMARK).exists())) {
          Utils.copyWatermarkResourceToDevice();
        }
        updateCompositionWatermark.updateCompositionWatermark(currentProject,
            data);
        preferencesView.setWatermarkSwitchPref(data);
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
        if (video.getUuid().compareTo(videoId) == 0) {
          return (Video) video;
        }
      }
    }
    return null;
  }

  public void trackThemeApp(boolean isDarkTheme) {
    userEventTracker.trackThemeAppSettingsChanged(isDarkTheme);
  }

  public void initBilling(Activity activity) {
    playStoreBillingDelegate.initBilling(activity);
  }

  @Override
  public void itemDarkThemePurchased(boolean purchased) {
    if (purchased) {
      preferencesView.itemDarkThemePurchased();
    } else {
      deactivateDarkThemePreference();
      preferencesView.deactivateDarkTheme();
    }
  }

  private void deactivateDarkThemePreference() {
    sharedPreferences.edit().putBoolean(ConfigPreferences.THEME_APP_DARK, false).commit();
  }

  @Override
  public void itemWatermarkPurchased(boolean purchased) {
    if (purchased) {
      preferencesView.itemWatermarkPurchased();
    } else {
      activateWatermarkPreference();
      preferencesView.activateWatermark();
    }
  }

  private void activateWatermarkPreference() {
    sharedPreferences.edit().putBoolean(ConfigPreferences.WATERMARK, true).commit();
  }

  public void checkVimojoStore(Activity activity) {
    if (BuildConfig.VIMOJO_STORE_AVAILABLE) {
      initBilling(activity);
      preferencesView.vimojoStoreSupported();
    }
  }

  public void setupUserAuthPreference() {
    if (!BuildConfig.FEATURE_VIMOJO_PLATFORM) {
      preferencesView.hideRegisterLoginView();
      return;
    }

    if (userAuth0Helper.isLogged()) {
      preferencesView.setupUserAuthentication(true);
    } else {
      preferencesView.setupUserAuthentication(false);
    }
  }

  public Context getContext() {
    return context;
  }

  public void signOutConfirmed() {
    deleteAccount();
    preferencesView.setupUserAuthentication(false);
  }

  private void deleteAccount() {
    userAuth0Helper.signOut();
    deletePendingVideosToUpload();
    ListenableFuture<Account> accountFuture =
            executeUseCaseCall(() -> getAccount.getCurrentAccount(context));
    Futures.addCallback(accountFuture, new FutureCallback<Account>() {
      @Override
      public void onSuccess(Account account) {
        AccountManager am = AccountManager.get(context);
        if (am != null && account != null) {
          Log.d(LOG_TAG, "removeAccount");
          am.removeAccount(account, null, null);
        }
      }
      @Override
      public void onFailure(Throwable t) {
        // (jliarte): 22/01/18 no account present? do nothing
      }
    });
  }

  private void deletePendingVideosToUpload() {
    if(uploadRepository.getAllVideosToUpload().size() > 0) {
      uploadRepository.removeAllVideosToUpload();
    }
  }

  public void performLoginAndSaveAccount(Activity activity) {
    userAuth0Helper.performLogin(activity, new AuthCallback() {
          @Override
          public void onFailure(@NonNull Dialog dialog) {
            Log.d(LOG_TAG, "Error performLogin onFailure ");
            preferencesView.showError(R.string.auth0_error_login_failure);
          }

          @Override
          public void onFailure(AuthenticationException exception) {
            Log.d(LOG_TAG, "Error performLogin AuthenticationException "
                + exception.getMessage());
            Crashlytics.log("Error performLogin AuthenticationException: " + exception);
            preferencesView.showError(R.string.auth0_error_authentication);
          }

          @Override
          public void onSuccess(@NonNull Credentials credentials) {
            Log.d(LOG_TAG, "Logged in: " + credentials.getAccessToken());
            userAuth0Helper.saveCredentials(credentials);
            preferencesView.setupUserAuthentication(true);
          }
        });
  }
}
