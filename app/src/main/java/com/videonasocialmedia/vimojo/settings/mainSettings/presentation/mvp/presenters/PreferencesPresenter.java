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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetWatermarkPreferenceFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateWatermarkPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.OnRelaunchTemporalFileListener;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.vimojo.store.billing.BillingConnectionListener;
import com.videonasocialmedia.vimojo.store.billing.BillingHistoryPurchaseListener;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter implements SharedPreferences.OnSharedPreferenceChangeListener,
    OnRelaunchTemporalFileListener, BillingHistoryPurchaseListener, BillingConnectionListener {

    private static final String LOG_TAG = PreferencesPresenter.class.getSimpleName();
    private Context context;
    private UserEventTracker userEventTracker;
    private SharedPreferences sharedPreferences;
    private PreferencesView preferencesView;
    private PreferenceCategory cameraSettingsPref;
    private ListPreference resolutionPref;
    private ListPreference qualityPref;
    private Preference transitionVideoPref;
    private Preference transitionAudioPref;
    private Preference watermarkPref;
    private Preference themeApp;
    private Preference emailPref;
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
    private BillingManager billingManager;

//    private Drawable drawableFadeTransitionVideo;
//    private VideonaFormat videoFormat;

    /**
     * Constructor
     *  @param preferencesView
     * @param resolutionPref
     * @param qualityPref
     * @param emailPref
     * @param context
     * @param sharedPreferences
     */
    public PreferencesPresenter(PreferencesView preferencesView,
            Context context, SharedPreferences sharedPreferences,
            PreferenceCategory cameraSettingsPref,
            ListPreference resolutionPref, ListPreference qualityPref,
            Preference transitionVideoPref, Preference themeApp,
            Preference transitionAudioPref, Preference watermarkPref, Preference emailPref,
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
            BillingManager billingManager) {

        this.preferencesView = preferencesView;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.cameraSettingsPref = cameraSettingsPref;
        this.resolutionPref = resolutionPref;
        this.qualityPref = qualityPref;
        this.transitionVideoPref = transitionVideoPref;
        this.transitionAudioPref = transitionAudioPref;
        this.watermarkPref = watermarkPref;
        this.emailPref = emailPref;
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
    }

    /**
     * Checks the available preferences on the device
     */
    public void checkMailValid() {
        emailPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (android.util.Patterns.EMAIL_ADDRESS
                        .matcher((CharSequence) newValue).matches()) {
                    return true;
                } else {
                    preferencesView.showError(R.string.invalid_email);
                    return false;
                }
            }
        });
    }

    public void checkAvailablePreferences() {
        checkUserAccountData();
        if (BuildConfig.FEATURE_FTP) {
            checkUserFTP1Data();
            checkUserFTP2Data();
        } else {
            // Visibility FTP gone
            preferencesView.hideFtpsViews();
        }
        checkCameraSettingsEnabled();
        checkAvailableResolution();
        checkAvailableQuality();
        checkTransitions();
        checkWatermark(BuildConfig.FEATURE_WATERMARK);
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

    private void checkWatermark(boolean isWatermarkBuildConfigActivated) {
        if (isWatermarkBuildConfigActivated) {
            boolean data = getWatermarkPreferenceFromProjectUseCase.isWatermarkActivated();
            preferencesView.setWatermarkSwitchPref(data);
        } else {
            preferencesView.hideWatermarkView();
        }
    }

    private void checkCameraSettingsEnabled() {
        List<Media> media = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (media.size() > 0) {
            cameraSettingsPref.setEnabled(false);
            isPreferenceAvailable = false;
        } else {
            cameraSettingsPref.setEnabled(true);
            isPreferenceAvailable = true;
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
     * Checks user preferences data
     */
    private void checkUserAccountData() {
        checkUserAccountPreference(ConfigPreferences.NAME);
        checkUserAccountPreference(ConfigPreferences.USERNAME);
        checkUserAccountPreference(ConfigPreferences.EMAIL);
    }

    private void checkUserAccountPreference(String key) {
        String data = sharedPreferences.getString(key, null);
        if (data != null && !data.isEmpty()) {
            preferencesView.setSummary(key, data);
            sendPropertyToMixpanel(key, data);
        }
    }

    private void sendPropertyToMixpanel(String key, String data) {
        if (key.equals(ConfigPreferences.NAME)) {
            preferencesView.setUserPropertyToMixpanel("$first_name", data);
        }
        if (key.equals(ConfigPreferences.USERNAME)) {
            preferencesView.setUserPropertyToMixpanel("$username", data);
        }
        if (key.equals(ConfigPreferences.EMAIL)) {
            preferencesView.setUserPropertyToMixpanel("$account_email", data);
        }
    }


    /**
     * Checks supported resolutions on camera
     */
    private void checkAvailableResolution() {
        ArrayList<String> resolutionNames = new ArrayList<>();
        ArrayList<String> resolutionValues = new ArrayList<>();
        String defaultResolution = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION;

        if (!isPreferenceAvailable) {
            resolutionPref.setTitle(R.string.resolution);
            resolutionPref.setSummary(R.string.preference_not_available);
            return;
        }

        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.good_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.good_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.good_resolution_name);
            }
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.low_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.low_resolution_name);
            }
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.high_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.high_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.high_resolution_name);
            }
        }
        if (resolutionNames.size() > 0 && defaultResolution != null) {
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames,
                    resolutionValues);
            if (updateDefaultPreference(key, resolutionNames)) {
                preferencesView.setDefaultPreference(resolutionPref, defaultResolution, key);
            } else {
                preferencesView.setPreference(resolutionPref, sharedPreferences.getString(key, ""));
            }
        } else {
            resolutionNames.add(context.getResources().getString(R.string.good_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.good_resolution_value));
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames,
                    resolutionValues);
        }
    }

    /**
     * Checks supported qualities on camera
     */
    private void checkAvailableQuality() {
        ArrayList<String> qualityNames = new ArrayList<>();
        ArrayList<String> qualityValues = new ArrayList<>();
        String defaultQuality = context.getResources()
                .getString(ProfileSharedPreferencesRepository.DEFAULT_VIDEO_QUALITY_NAME);
        String qualityKey = ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY;

        if (!isPreferenceAvailable) {
            qualityPref.setTitle(R.string.quality);
            qualityPref.setSummary(R.string.preference_not_available);
            return;
        }

        qualityNames.add(context.getResources().getString(R.string.low_quality_name));
        qualityValues.add(context.getResources().getString(R.string.low_quality_value));
        qualityNames.add(context.getResources().getString(R.string.good_quality_name));
        qualityValues.add(context.getResources().getString(R.string.good_quality_value));
        qualityNames.add(context.getResources().getString(R.string.high_quality_name));
        qualityValues.add(context.getResources().getString(R.string.high_quality_value));

        if (qualityNames.size() > 0) {
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
            if (updateDefaultPreference(qualityKey, qualityNames)) {
                preferencesView.setDefaultPreference(qualityPref, defaultQuality, qualityKey);
            } else {
                preferencesView.setPreference(qualityPref,
                        sharedPreferences.getString(qualityKey, ""));
            }
        } else {
            qualityNames.add(context.getResources().getString(R.string.good_quality_name));
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
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


    @Override
    public void historyPurchasedItems(List<Purchase> purchasesList) {
        for(Purchase purchase: purchasesList){
            if(purchase.getSku().compareTo(Constants.IN_APP_BILLING_ITEM_DARK_THEME) == 0) {
                preferencesView.itemDarkThemePurchased();
                Log.d(LOG_TAG, "item purchased " + purchase.getSku());
            }
            if(purchase.getSku().compareTo(Constants.IN_APP_BILLING_ITEM_WATERMARK) == 0) {
                preferencesView.itemWatermarkPurchased();
                Log.d(LOG_TAG, "item purchased " + purchase.getSku());
            }
        }
    }

    private void checkPurchasedItems() {
        billingManager.queryPurchaseHistoryAsync(this);
    }

    public void initBilling() {
        billingManager.initBillingClient(this);
    }

    @Override
    public void billingClientSetupFinished() {
        if (billingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK &&
            billingManager.isServiceConnected()) {
            checkPurchasedItems();
        } else {
           // preferencesView.showError(R.string.error_message_shop_not_available);
            Log.d(LOG_TAG, "billing client response " +
                billingManager.getBillingClientResponseCode());
        }
    }
}
