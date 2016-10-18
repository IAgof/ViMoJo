/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideosUseCase;
import com.videonasocialmedia.vimojo.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.ArrayList;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Context context;
    private SharedPreferences sharedPreferences;
    private PreferencesView preferencesView;
    private ListPreference resolutionPref;
    private ListPreference qualityPref;
    private ListPreference frameRatePref;
    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;

    /**
     * Constructor
     *
     * @param preferencesView
     * @param resolutionPref
     * @param qualityPref
     * @param context
     * @param sharedPreferences
     */
    public PreferencesPresenter(PreferencesView preferencesView, ListPreference resolutionPref,
                                ListPreference qualityPref, ListPreference frameRatePref,
                                Context context, SharedPreferences sharedPreferences) {
        this.preferencesView = preferencesView;
        this.resolutionPref = resolutionPref;
        this.qualityPref = qualityPref;
        this.frameRatePref = frameRatePref;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
    }

    /**
     * Checks the available preferences on the device
     */
    public void checkAvailablePreferences() {
        checkUserAccountData();
        checkUserFTPData();
        checkAvailableResolution();
        checkAvailableQuality();
        checkAvailableFrameRate();
    }

    private void checkUserFTPData() {
        checkUserFTPPreference(ConfigPreferences.HOST);
        checkUserFTPPreference(ConfigPreferences.USERNAMEFTP);
        checkUserFTPPreference(ConfigPreferences.EDITED_VIDEO_DESTINATION);
        checkUserFTPPreference(ConfigPreferences.UNEDITED_VIDEO_DESTINATION);
    }

    private void checkUserFTPPreference(String key) {

        String data = sharedPreferences.getString(key, null);
        if (data != null && !data.isEmpty())
            preferencesView.setSummary(key, data);

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
        if (data != null && !data.isEmpty())
            preferencesView.setSummary(key, data);
    }


    /**
     * Checks supported resolutions on camera
     */
    private void checkAvailableResolution() {
        ArrayList<String> resolutionNames = new ArrayList<>();
        ArrayList<String> resolutionValues =  new ArrayList<>();
        String defaultResolution = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION; //"list_preference_resolution";

        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_value));
            resolutionValues.add(context.getResources().getString(R.string.low_resolution_value));
            defaultResolution = context.getResources().getString(R.string.low_resolution_value);
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.good_resolution_value));
            resolutionValues.add(context.getResources().getString(R.string.good_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.good_resolution_value);
            }
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.high_resolution_value));
            resolutionValues.add(context.getResources().getString(R.string.high_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.high_resolution_value);
            }
        }
        if (resolutionNames.size() > 0 && defaultResolution != null) {
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames, resolutionValues);
            if (updateDefaultPreference(key, resolutionValues)) {
                preferencesView.setDefaultPreference(resolutionPref, defaultResolution, key);
            } else {
                preferencesView.setPreference(resolutionPref, sharedPreferences.getString(key, ""));
            }
        } else {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_value));
            resolutionValues.add(context.getResources().getString(R.string.low_resolution_value));
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames, resolutionValues);
        }
    }

    /**
     * Checks supported qualities on camera
     */
    private void checkAvailableQuality() {
        ArrayList<String> qualityNames = new ArrayList<>();
        ArrayList<String> qualityValues = new ArrayList<>();
        String defaultQuality = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY; //"list_preference_quality";

        qualityNames.add(context.getResources().getString(R.string.high_quality_value));
        qualityValues.add(context.getResources().getString(R.string.high_quality_value));
        if (defaultQuality == null) {
            defaultQuality = context.getResources().getString(R.string.high_quality_value);
        }

        qualityNames.add(context.getResources().getString(R.string.low_quality_value));
        qualityValues.add(context.getResources().getString(R.string.low_quality_value));
        if (defaultQuality == null) {
            defaultQuality = context.getResources().getString(R.string.low_quality_value);
        }

        qualityNames.add(context.getResources().getString(R.string.good_quality_value));
        qualityValues.add(context.getResources().getString(R.string.good_quality_value));
        if (defaultQuality == null) {
            defaultQuality = context.getResources().getString(R.string.good_quality_value);
        }

        if (qualityNames.size() > 0 && defaultQuality != null) {
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
            if (updateDefaultPreference(key, qualityValues)) {
                preferencesView.setDefaultPreference(qualityPref, defaultQuality, key);
            } else {
                preferencesView.setPreference(qualityPref, sharedPreferences.getString(key, ""));
            }
        } else {
            qualityNames.add(context.getResources().getString(R.string.high_quality_value));
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
        }
    }

    private void checkAvailableFrameRate(){
        ArrayList<String> frameRateNames = new ArrayList<>();
        ArrayList<String> frameRateValues = new ArrayList<>();
        String defaultFrameRate = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE; //"list_preference_quality";

        if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_25FPS_SUPPORTED, false)) {
            frameRateNames.add(context.getResources().getString(R.string.good_frame_rate_value));
            frameRateValues.add(context.getResources().getString(R.string.good_frame_rate_value));
            if (defaultFrameRate == null) {
                defaultFrameRate = context.getResources().getString(R.string.good_frame_rate_value);
            }
        }

        if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_24FPS_SUPPORTED, false)) {
            frameRateNames.add(context.getResources().getString(R.string.low_frame_rate_value));
            frameRateValues.add(context.getResources().getString(R.string.low_frame_rate_value));
            if (defaultFrameRate == null) {
                defaultFrameRate = context.getResources().getString(R.string.low_frame_rate_value);
            }
        }

        if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_30FPS_SUPPORTED, false)) {
            frameRateNames.add(context.getResources().getString(R.string.high_frame_rate_value));
            frameRateValues.add(context.getResources().getString(R.string.high_frame_rate_value));
            if (defaultFrameRate == null) {
                defaultFrameRate = context.getResources().getString(R.string.high_frame_rate_value);
            }
        }

        if (frameRateNames.size() > 0 && defaultFrameRate != null) {
            preferencesView.setAvailablePreferences(frameRatePref, frameRateNames, frameRateValues);
            if (updateDefaultPreference(key, frameRateValues)) {
                preferencesView.setDefaultPreference(frameRatePref, defaultFrameRate, key);
            } else {
                preferencesView.setPreference(frameRatePref, sharedPreferences.getString(key, ""));
            }
        } else {
            frameRateNames.add(context.getResources().getString(R.string.good_frame_rate_value));
            preferencesView.setAvailablePreferences(frameRatePref, frameRateNames, frameRateValues);
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
        if (key.compareTo(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY) == 0 ||
                key.compareTo(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION) == 0) {
            if (BuildConfig.FLAVOR.compareTo("stable") == 0) {
                RemoveVideosUseCase videoRemover = new RemoveVideosUseCase();
                videoRemover.removeMediaItemsFromProject();
            }
        }
    }

    public boolean checkIfWhatsappIsInstalled() {
        return obtainNetworksToShareUseCase.checkIfSocialNetworkIsInstalled("whatsapp");
    }

}
