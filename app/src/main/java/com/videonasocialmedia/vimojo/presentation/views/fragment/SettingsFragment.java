package com.videonasocialmedia.vimojo.presentation.views.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.vimojo.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaDialogListener;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, PreferencesView {

    protected final int REQUEST_CODE_EXIT_APP = 1;
    protected PreferenceCategory cameraSettingsPref;
    protected Preference emailPref;
    protected ListPreference resolutionPref;
    protected ListPreference qualityPref;
    protected ListPreference frameRatePref;
    protected Preference resolutionPrefNotAvailable;
    protected Preference qualityPrefNotAvailable;
    protected Preference frameRatePrefNotAvailable;
    protected PreferencesPresenter preferencesPresenter;
    protected Context context;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected MixpanelAPI mixpanel;
    protected VideonaDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = VimojoApplication.getAppContext();
        initPreferences();
        preferencesPresenter = new PreferencesPresenter(this,cameraSettingsPref, resolutionPref,
                qualityPref, frameRatePref, emailPref, context, sharedPreferences);
        mixpanel = MixpanelAPI.getInstance(this.getActivity(), BuildConfig.MIXPANEL_TOKEN);
    }

    private void initPreferences() {
        addPreferencesFromResource(R.xml.preferences);

        getPreferenceManager().setSharedPreferencesName(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME);
        sharedPreferences = getActivity().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setupCameraSettings();
        setupMailValid();
    }

    private void setupMailValid() {
        emailPref=findPreference(ConfigPreferences.EMAIL);
    }

    private void setupCameraSettings() {

        cameraSettingsPref = (PreferenceCategory) findPreference(getString(R.string.title_camera_section));

        resolutionPref = (ListPreference) findPreference(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION);
        qualityPref = (ListPreference) findPreference(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY);
        frameRatePref = (ListPreference) findPreference(ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list, null);
        ListView listView = (ListView)v.findViewById(android.R.id.list);

        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listView, false);
        listView.addFooterView(footer, null, false);

        TextView footerText = (TextView)v.findViewById(R.id.footerText);
        String text = getString(R.string.vimojo) + " v" + BuildConfig.VERSION_NAME + "\n" +
                getString(R.string.madeIn);
        footerText.setText(text);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        preferencesPresenter.checkAvailablePreferences();
        preferencesPresenter.checkMailValid();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesPresenter);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesPresenter);
    }

    @Override
    public void setAvailablePreferences(ListPreference preference, ArrayList<String> listNames,
                                        ArrayList<String> listValues) {
        int size = listNames.size();
        CharSequence entries[] = new String[size];
        CharSequence entryValues[] = new String[size];
        for (int i=0; i<size; i++) {
            entries[i] = listNames.get(i);
            entryValues[i] = listValues.get(i);
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
    }

    @Override
    public void setDefaultPreference(ListPreference preference, String name, String key) {
        preference.setValue(name);
        preference.setSummary(name);
        editor.putString(key, name);
        editor.commit();
    }

    @Override
    public void setPreference(ListPreference preference, String name) {
        preference.setValue(name);
        preference.setSummary(name);
        trackQualityAndResolutionAndFrameRateUserTraits(preference.getKey(), name);
    }

    @Override
    public void setSummary(String key, String value) {
        Preference preference = findPreference(key);
        preference.setSummary(value);
    }

    @Override
    public void setCameraSettingsAvailable(boolean isAvailable) {
        if(isAvailable){
            resolutionPrefNotAvailable = findPreference(context.getString(R.string.resolution));
            resolutionPrefNotAvailable.setShouldDisableView(true);
        }
    }

    @Override
    public void showError() {
        Snackbar.make(getView(), R.string.invalid_email,Snackbar.LENGTH_LONG).show();
    }

    private void trackQualityAndResolutionAndFrameRateUserTraits(String key, String value) {
        String property = null;
        if(key.equals(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION))
            property = AnalyticsConstants.RESOLUTION;
        else if(key.equals(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY))
            property = AnalyticsConstants.QUALITY;
        else if(key.equals(ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE))
            property = AnalyticsConstants.FRAME_RATE;
        mixpanel.getPeople().set(property, value.toLowerCase());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference connectionPref = findPreference(key);
        trackQualityAndResolutionAndFrameRateUserTraits(key, sharedPreferences.getString(key, ""));
        if(!key.equals(ConfigPreferences.PASSWORD_FTP) && !key.equals(ConfigPreferences.PASSWORD_FTP2)){
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }

    }
}
