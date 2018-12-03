package com.videonasocialmedia.vimojo.settings.mainSettings.presentation.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.DaggerFragmentPresentersComponent;
import com.videonasocialmedia.vimojo.main.FragmentPresentersComponent;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.main.modules.FragmentPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.activity.AboutActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.LegalNoticeActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.PrivacyPolicyActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.TermsOfServiceActivity;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.activity.LicensesActivity;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, PreferencesView {

  private String LOG_TAG = SettingsFragment.class.getCanonicalName();

  @Inject
  PreferencesPresenter preferencesPresenter;
  protected PreferenceCategory ftp1Pref;
  // TODO:(alvaro.martinez) 12/01/18 Now we only use one FTP, not two. Implement feature, I want to add more FTPs
  //protected PreferenceCategory ftp2Pref;
  protected PreferenceCategory transitionCategory;
  protected PreferenceCategory moreAppsPrefCategory;
  protected SwitchPreference transitionsVideoPref;
  protected SwitchPreference transitionsAudioPref;
  protected Context context;
  protected SharedPreferences sharedPreferences;
  protected SharedPreferences.Editor editor;
  protected MixpanelAPI mixpanel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = VimojoApplication.getAppContext();

    initPreferences();
    FragmentPresentersComponent getFragmentPresentersComponent = initComponent();
    getFragmentPresentersComponent.inject(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    preferencesPresenter.updatePresenter(getActivity());
    sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesPresenter);
  }

  @Override
  public void onPause() {
    super.onPause();
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesPresenter);
  }

  private FragmentPresentersComponent initComponent() {
    return DaggerFragmentPresentersComponent.builder()
            .fragmentPresentersModule(new FragmentPresentersModule(this, context, sharedPreferences,
                    this.getActivity()))
            .systemComponent(((VimojoApplication) getActivity().getApplication()).getSystemComponent())
            .build();
  }

  private void initPreferences() {
    addPreferencesFromResource(R.xml.preferences);

    getPreferenceManager().setSharedPreferencesName(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME);
    sharedPreferences = getActivity().getSharedPreferences(
            ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE);
    editor = sharedPreferences.edit();

    setupAboutUs();
    setupPrivacyPolicy();
    setupTermOfService();
    setupLicense();
    setupLegalNotice();
    setupTransitions();
  }

  private void setupTransitions() {
    transitionsVideoPref = (SwitchPreference)
        findPreference(ConfigPreferences.TRANSITION_VIDEO);
    transitionsAudioPref = (SwitchPreference)
        findPreference(ConfigPreferences.TRANSITION_AUDIO);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View viewRoot = inflater.inflate(R.layout.list, null);
    ListView listView = (ListView) viewRoot.findViewById(android.R.id.list);

    ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listView, false);
    listView.addFooterView(footer, null, false);

    TextView footerText = (TextView) viewRoot.findViewById(R.id.footerText);
    String text = getString(R.string.flavor_name) + " v" + BuildConfig.VERSION_NAME + "\n" +
            getString(R.string.madeIn);
    footerText.setText(text);

    return viewRoot;
  }

  @Override
  public void setAvailablePreferences(ListPreference preference, ArrayList<String> listNames,
                                      ArrayList<String> listValues) {
    int size = listNames.size();
    CharSequence entries[] = new String[size];
    CharSequence entryValues[] = new String[size];
    for (int i = 0; i < size; i++) {
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
    preferencesPresenter.trackQualityAndResolutionAndFrameRateUserTraits(preference.getKey(),
        name);
  }

  @Override
  public void setSummary(String key, String value) {
    Preference preference = findPreference(key);
    preference.setSummary(value);
  }

  @Override
  public void setTransitionsPref(String key, boolean value) {
    if (key.compareTo(ConfigPreferences.TRANSITION_AUDIO) == 0) {
      transitionsAudioPref.setChecked(value);
    }
    if (key.compareTo(ConfigPreferences.TRANSITION_VIDEO) == 0) {
      transitionsVideoPref.setChecked(value);
    }
  }

  @Override
  public void showError(int message) {
    Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void hideFtpsViews() {
    ftp1Pref = (PreferenceCategory) findPreference(getString(R.string.title_FTP1_Section));
    if (ftp1Pref != null) {
      getPreferenceScreen().removePreference(ftp1Pref);
    }
    // TODO:(alvaro.martinez) 12/01/18 Now we only use one FTP, not two. Implement feature, I want to add more FTPs
        /*ftp2Pref = (PreferenceCategory) findPreference(getString(R.string.title_FTP2_Section));
        if (ftp2Pref != null) {
            getPreferenceScreen().removePreference(ftp2Pref);
        }*/
  }

  @Override
  public void showMoreAppsSection() {
    Preference appVideonaPref = findPreference(ConfigPreferences.APP_VIDEONA);
    appVideonaPref.setOnPreferenceClickListener(preference -> {
      navigateToURL(context.getString(R.string.app_videona_campaign_link));
      return true;
    });
    Preference appKamaradaPref = findPreference(ConfigPreferences.APP_KAMARADA);
    appKamaradaPref.setOnPreferenceClickListener(preference -> {
      navigateToURL(context.getString(R.string.app_kamarada_campaign_link));
      return true;
    });
    Preference appVimojoPref = findPreference(ConfigPreferences.APP_VIMOJO);
    appVimojoPref.setOnPreferenceClickListener(preference -> {
      navigateToURL(context.getString(R.string.app_vimojo_campaign_link));
      return true;
    });
  }

  @Override
  public void hideMoreAppsSection() {
    moreAppsPrefCategory = (PreferenceCategory)
            findPreference(getString(R.string.title_more_apps_section));
    if (moreAppsPrefCategory != null) {
      getPreferenceScreen().removePreference(moreAppsPrefCategory);
    }
  }

  @Override
  public void hideTransitions() {
    transitionCategory = (PreferenceCategory)
        findPreference(getString(R.string.title_fade_transition));
    if (transitionCategory != null) {
      getPreferenceScreen().removePreference(transitionCategory);
    }
  }


  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference connectionPref = findPreference(key);
    if (key.compareTo(ConfigPreferences.TRANSITION_VIDEO) == 0
            || key.compareTo(ConfigPreferences.TRANSITION_AUDIO) == 0) {
      return;
    }
    if (!key.equals(ConfigPreferences.PASSWORD_FTP)
            && !key.equals(ConfigPreferences.PASSWORD_FTP2)) {
      connectionPref.setSummary(sharedPreferences.getString(key, ""));
      return;
    }
    preferencesPresenter.trackQualityAndResolutionAndFrameRateUserTraits(key,
            sharedPreferences.getString(key, ""));
    }

  private void setupLegalNotice() {
    Preference legalNoticePref = findPreference(ConfigPreferences.LEGAL_NOTICE);
    legalNoticePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        navigateTo(LegalNoticeActivity.class);
        return true;
      }
    });
  }

  private void setupLicense() {
    Preference licensePref = findPreference(ConfigPreferences.LICENSES);
    licensePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        navigateTo(LicensesActivity.class);
        return true;
      }
    });
  }

  private void setupTermOfService() {
    Preference termOfServicePref = findPreference(ConfigPreferences.TERM_OF_SERVICE);
    termOfServicePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        navigateTo(TermsOfServiceActivity.class);
        return true;
      }
    });
  }

  private void setupPrivacyPolicy() {
    Preference privacyPolicyPref = findPreference(ConfigPreferences.PRIVACY_POLICY);
    privacyPolicyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        navigateTo(PrivacyPolicyActivity.class);
        return true;
      }
    });
  }

  private void navigateToURL(String url) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
  }

  private void setupAboutUs() {
    Preference aboutUsPref = findPreference(ConfigPreferences.ABOUT_US);
    aboutUsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        navigateTo(AboutActivity.class);
        return true;
      }
    });
  }

  private void navigateTo(Class activity) {
    Intent intent = new Intent(VimojoApplication.getAppContext(), activity);
    startActivity(intent);
  }
}
