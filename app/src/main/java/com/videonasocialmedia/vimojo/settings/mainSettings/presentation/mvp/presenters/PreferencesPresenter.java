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
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.text.TextUtils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.AccountConstants;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAccount;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetWatermarkPreferenceFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateWatermarkPreferenceToProjectUseCase;
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
import java.util.concurrent.Callable;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter extends VimojoPresenter
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        OnRelaunchTemporalFileListener, PlayStoreBillingDelegate.BillingDelegateView {
    private static final String LOG_TAG = PreferencesPresenter.class.getSimpleName();
    private final BillingManager billingManager;
    private final GetAuthToken getAuthToken;
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
    private boolean isPreferenceAvailable = false;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private UpdateAudioTransitionPreferenceToProjectUseCase
        updateAudioTransitionPreferenceToProjectUseCase;
    private UpdateVideoTransitionPreferenceToProjectUseCase
        updateVideoTransitionPreferenceToProjectUseCase;
    private UpdateIntermediateTemporalFilesTransitionsUseCase
        updateIntermediateTemporalFilesTransitionsUseCase;
    private GetWatermarkPreferenceFromProjectUseCase getWatermarkPreferenceFromProjectUseCase;
    private UpdateWatermarkPreferenceToProjectUseCase updateWatermarkPreferenceToProjectUseCase;
    private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;
    private GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
    private GetAccount getAccount;

    /**
     * Constructor
     *  @param preferencesView
     * @param context
     * @param sharedPreferences
     * @param getAuthToken
     * @param getAccount
     */
    public PreferencesPresenter(PreferencesView preferencesView,
            Context context, SharedPreferences sharedPreferences,
            Preference transitionVideoPref, Preference themeApp,
            Preference transitionAudioPref, Preference watermarkPref,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
            UpdateAudioTransitionPreferenceToProjectUseCase
                                        updateAudioTransitionPreferenceToProjectUseCase,
                                UpdateVideoTransitionPreferenceToProjectUseCase
                                        updateVideoTransitionPreferenceToProjectUseCase,
                                UpdateIntermediateTemporalFilesTransitionsUseCase
                                        updateIntermediateTemporalFilesTransitionsUseCase,
                                GetWatermarkPreferenceFromProjectUseCase getWatermarkPreferenceFromProjectUseCase,
                                UpdateWatermarkPreferenceToProjectUseCase updateWatermarkPreferenceToProjectUseCase,
                                RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
                                GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
                                BillingManager billingManager, GetAuthToken getAuthToken, GetAccount getAccount) {
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
        this.getWatermarkPreferenceFromProjectUseCase = getWatermarkPreferenceFromProjectUseCase;
        this.updateWatermarkPreferenceToProjectUseCase = updateWatermarkPreferenceToProjectUseCase;
        this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
        this.getVideoFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
        userEventTracker = UserEventTracker.getInstance(MixpanelAPI
                .getInstance(context.getApplicationContext(), BuildConfig.MIXPANEL_TOKEN));
        this.billingManager = billingManager;
        this.getAuthToken = getAuthToken;
        this.playStoreBillingDelegate = new PlayStoreBillingDelegate(billingManager, this);
        this.getAccount = getAccount;
    }

    public void onPause() {
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
            data = getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated();
            preferencesView.setTransitionsPref(key, data);
        } else {
            if (key.compareTo(ConfigPreferences.TRANSITION_VIDEO) == 0) {
                data = getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();
                preferencesView.setTransitionsPref(key, data);
            }
        }
    }

    private void checkWatermark() {
        if (BuildConfig.FEATURE_WATERMARK_SWITCH && !BuildConfig.FEATURE_FORCE_WATERMARK) {
            boolean data = getWatermarkPreferenceFromProjectUseCase.isWatermarkActivated();
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
     * @param key the key of the shared preference
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
                boolean dataTransitionAudio = sharedPreferences.getBoolean(key,false);
                updateAudioTransitionPreferenceToProjectUseCase
                        .setAudioFadeTransitionActivated(dataTransitionAudio);
                updateIntermediateTemporalFilesTransitionsUseCase.execute(this);
                break;
            case ConfigPreferences.TRANSITION_VIDEO:
                boolean dataTransitionVideo = sharedPreferences.getBoolean(key, false);
                updateVideoTransitionPreferenceToProjectUseCase
                        .setVideoFadeTransitionActivated(dataTransitionVideo);
                updateIntermediateTemporalFilesTransitionsUseCase.execute(this);
                break;
            case ConfigPreferences.WATERMARK:
                boolean data = sharedPreferences.getBoolean(key, false);
                if (data && !(new File(Constants.PATH_WATERMARK).exists())) {
                    Utils.copyWatermarkResourceToDevice();
                }
                updateWatermarkPreferenceToProjectUseCase.setWatermarkActivated(data);
                preferencesView.setWatermarkSwitchPref(data);
            default:
        }
    }

    @Override
    public void videoToRelaunch(String videoUuid, String intermediatesTempAudioFadeDirectory) {
        final Video video = getVideo(videoUuid);
        Project currentProject = loadCurrentProject();
        relaunchTranscoderTempBackgroundUseCase.relaunchExport(video, currentProject);
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null, null);
    }

    private Video getVideo(String videoId) {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase =
                new GetMediaListFromProjectUseCase();
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            for (Media video : videoList) {
                if (((Video) video).getUuid().compareTo(videoId) == 0) {
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
        } else  {
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
        ListenableFuture<String> authTokenFuture = executeUseCaseCall(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getAuthToken.getAuthToken(getContext()).getToken();
            }
        });
        Futures.addCallback(authTokenFuture, new FutureCallback<String>() {
            @Override
            public void onSuccess(String authToken) {
                preferencesView.setupUserAuthentication(!TextUtils.isEmpty(authToken));
            }

            @Override
            public void onFailure(Throwable errorGettingToken) {
                preferencesView.setupUserAuthentication(false);
            }
        });
    }

    public Context getContext() {
        return context;
    }

    public void signOutConfirmed() {
        deleteAccount();
        preferencesView.setupUserAuthentication(false);
    }

    private void deleteAccount() {
        ListenableFuture<Account> accountFuture = executeUseCaseCall(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                return getAccount.getCurrentAccount(getContext());
            }
        });
        Futures.addCallback(accountFuture, new FutureCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                AccountManager am = AccountManager.get(getContext());
                am.removeAccount(account, null, null);
            }

            @Override
            public void onFailure(Throwable t) {
                // (jliarte): 22/01/18 no account present? do nothing
            }
        });
    }
}
