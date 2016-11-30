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
import android.preference.Preference;
import android.preference.PreferenceCategory;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideosUseCase;
import com.videonasocialmedia.vimojo.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to show the setting menu.
 */
public class PreferencesPresenter implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Context context;
    private SharedPreferences sharedPreferences;
    private PreferencesView preferencesView;
    private PreferenceCategory cameraSettingsPref;
    private ListPreference resolutionPref;
    private ListPreference qualityPref;
    private ListPreference frameRatePref;
    private Preference emailPref;
    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private boolean isPreferenceAvailable = false;

    /**
     * Constructor
     *  @param preferencesView
     * @param resolutionPref
     * @param qualityPref
     * @param emailPref
     * @param context
     * @param sharedPreferences
     */
    public PreferencesPresenter(PreferencesView preferencesView, PreferenceCategory cameraSettingsPref,
                                ListPreference resolutionPref, ListPreference qualityPref,
                                ListPreference frameRatePref, Preference emailPref, Context context,
                                SharedPreferences sharedPreferences) {

        this.cameraSettingsPref = cameraSettingsPref;
        this.preferencesView = preferencesView;
        this.resolutionPref = resolutionPref;
        this.qualityPref = qualityPref;
        this.frameRatePref = frameRatePref;
        this.emailPref = emailPref;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
    }

    /**
     * Checks the available preferences on the device
     */

    public void checkMailValid(){
        emailPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(android.util.Patterns.EMAIL_ADDRESS.matcher((CharSequence) newValue).matches())
                    return true;
                else {
                    preferencesView.showError(R.string.invalid_email);
                    return false;
                   }
            }
        });
    }

    public void checkAvailablePreferences() {
        checkUserAccountData();
        checkUserFTP1Data();
        checkUserFTP2Data();
        checkCameraSettingsEnabled();
        checkAvailableResolution();
        checkAvailableQuality();
        checkAvailableFrameRate();
    }

    private void checkCameraSettingsEnabled() {

        List<Media> media = getMediaListFromProjectUseCase.getMediaListFromProject();
        if(media.size()>0) {
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
        if (data != null && !data.isEmpty()) {
            preferencesView.setSummary(key, data);
            sendPropertyToMixpanel(key, data);
        }
    }

    private void sendPropertyToMixpanel(String key, String data) {
        if(key.equals(ConfigPreferences.NAME))
            preferencesView.setUserPropertyToMixpanel("$first_name",data);
        if(key.equals(ConfigPreferences.USERNAME))
            preferencesView.setUserPropertyToMixpanel("$username",data);
        if(key.equals(ConfigPreferences.EMAIL))
            preferencesView.setUserPropertyToMixpanel("$account_email",data);
    }


    /**
     * Checks supported resolutions on camera
     */
    private void checkAvailableResolution() {
        ArrayList<String> resolutionNames = new ArrayList<>();
        ArrayList<String> resolutionValues =  new ArrayList<>();
        String defaultResolution = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION; //"list_preference_resolution";

        if(!isPreferenceAvailable){
            resolutionPref.setTitle(R.string.resolution);
            resolutionPref.setSummary(R.string.preference_not_available);
            return;
        }

        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.low_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.low_resolution_name);
            }
        }
        if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false)) {
            resolutionNames.add(context.getResources().getString(R.string.good_resolution_name));
            resolutionValues.add(context.getResources().getString(R.string.good_resolution_value));
            if (defaultResolution == null) {
                defaultResolution = context.getResources().getString(R.string.good_resolution_name);
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
            preferencesView.setAvailablePreferences(resolutionPref, resolutionNames, resolutionValues);
            if (updateDefaultPreference(key, resolutionNames)) {
                preferencesView.setDefaultPreference(resolutionPref, defaultResolution, key);
            } else {
                preferencesView.setPreference(resolutionPref, sharedPreferences.getString(key, ""));
            }
        } else {
            resolutionNames.add(context.getResources().getString(R.string.low_resolution_name));
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

        if(!isPreferenceAvailable){
            qualityPref.setTitle(R.string.quality);
            qualityPref.setSummary(R.string.preference_not_available);
            return;
        }

        qualityNames.add(context.getResources().getString(R.string.high_quality_name));
        qualityValues.add(context.getResources().getString(R.string.high_quality_value));
        if (defaultQuality == null) {
            defaultQuality = context.getResources().getString(R.string.high_quality_name);
        }

        qualityNames.add(context.getResources().getString(R.string.low_quality_name));
        qualityValues.add(context.getResources().getString(R.string.low_quality_value));
        if (defaultQuality == null) {
            defaultQuality = context.getResources().getString(R.string.low_quality_name);
        }

        qualityNames.add(context.getResources().getString(R.string.good_quality_name));
        qualityValues.add(context.getResources().getString(R.string.good_quality_value));
        if (defaultQuality == null) {
            defaultQuality = context.getResources().getString(R.string.good_quality_name);
        }

        if (qualityNames.size() > 0 && defaultQuality != null) {
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
            if (updateDefaultPreference(key, qualityNames)) {
                preferencesView.setDefaultPreference(qualityPref, defaultQuality, key);
            } else {
                preferencesView.setPreference(qualityPref, sharedPreferences.getString(key, ""));
            }
        } else {
            qualityNames.add(context.getResources().getString(R.string.high_quality_name));
            preferencesView.setAvailablePreferences(qualityPref, qualityNames, qualityValues);
        }
    }

    private void checkAvailableFrameRate(){
        ArrayList<String> frameRateNames = new ArrayList<>();
        ArrayList<String> frameRateValues = new ArrayList<>();
        String defaultFrameRate = null;
        String key = ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE; //"list_preference_quality";

        if(!isPreferenceAvailable){
            frameRatePref.setTitle(R.string.frame_rate);
            frameRatePref.setSummary(R.string.preference_not_available);
            return;
        }

        if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_25FPS_SUPPORTED, false)) {
            frameRateNames.add(context.getResources().getString(R.string.good_frame_rate_name));
            frameRateValues.add(context.getResources().getString(R.string.good_frame_rate_value));
            if (defaultFrameRate == null) {
                defaultFrameRate = context.getResources().getString(R.string.good_frame_rate_name);
            }
        }

        if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_24FPS_SUPPORTED, false)) {
            frameRateNames.add(context.getResources().getString(R.string.low_frame_rate_name));
            frameRateValues.add(context.getResources().getString(R.string.low_frame_rate_value));
            if (defaultFrameRate == null) {
                defaultFrameRate = context.getResources().getString(R.string.low_frame_rate_name);
            }
        }

        if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_30FPS_SUPPORTED, false)) {
            frameRateNames.add(context.getResources().getString(R.string.high_frame_rate_name));
            frameRateValues.add(context.getResources().getString(R.string.high_frame_rate_value));
            if (defaultFrameRate == null) {
                defaultFrameRate = context.getResources().getString(R.string.high_frame_rate_name);
            }
        }

        if (frameRateNames.size() > 0 && defaultFrameRate != null) {
            preferencesView.setAvailablePreferences(frameRatePref, frameRateNames, frameRateValues);
            if (updateDefaultPreference(key, frameRateNames)) {
                preferencesView.setDefaultPreference(frameRatePref, defaultFrameRate, key);
            } else {
                preferencesView.setPreference(frameRatePref, sharedPreferences.getString(key, ""));
            }
        } else {
            frameRateNames.add(context.getResources().getString(R.string.good_frame_rate_name));
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
