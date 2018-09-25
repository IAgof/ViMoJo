package com.videonasocialmedia.vimojo.settings.mainSettings.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.videonasocialmedia.vimojo.init.presentation.views.activity.InitRegisterLoginActivity;
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
import com.videonasocialmedia.vimojo.store.presentation.view.activity.VimojoStoreActivity;
import com.videonasocialmedia.vimojo.userProfile.presentation.views.UserProfileActivity;
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
  protected PreferenceCategory watermarkPrefCategory;
  protected PreferenceCategory moreAppsPrefCategory;
  protected PreferenceCategory authPrefCategory;
  protected SwitchPreference transitionsVideoPref;
  protected SwitchPreference transitionsAudioPref;
  protected SwitchPreference watermarkSwitchPref;
  protected SwitchPreference themeappSwitchPref;
  protected Context context;
  protected SharedPreferences sharedPreferences;
  protected SharedPreferences.Editor editor;
  protected MixpanelAPI mixpanel;
  private boolean darkThemePurchased = false;
  private boolean watermarkPurchased = false;
  private boolean vimojoStoreAvailable = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = VimojoApplication.getAppContext();

    initPreferences();
    FragmentPresentersComponent fragmentPresentersComponent = initComponent();
    fragmentPresentersComponent.inject(this);
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
    preferencesPresenter.pausePresenter();
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

    setupWatermark();
    setupAboutUs();
    setupPrivacyPolicy();
    setupTermOfService();
    setupLicense();
    setupLegalNotice();
    setupThemeApp();
    setupTransitions();
  }

  private void setupTransitions() {
    transitionsVideoPref = (SwitchPreference)
        findPreference(ConfigPreferences.TRANSITION_VIDEO);
    transitionsAudioPref = (SwitchPreference)
        findPreference(ConfigPreferences.TRANSITION_AUDIO);
  }

  private void setupWatermark() {
    watermarkSwitchPref = (SwitchPreference) findPreference(ConfigPreferences.WATERMARK);
  }

  private void setupThemeApp() {
    // TODO(jliarte): 27/10/17 improve default theme setting with a build constant
    themeappSwitchPref = (SwitchPreference) findPreference(ConfigPreferences.THEME_APP_DARK);
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

  private void updateIconLockItemsInAPP(SwitchPreference switchPreference, boolean isPurchased) {
    if (isPurchased) {
      switchPreference.setIcon(context.getDrawable(R.drawable.ic_unlocked));
    } else {
      switchPreference.setIcon(context.getDrawable(R.drawable.ic_locked));
    }
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
  public void setWatermarkSwitchPref(boolean value) {
    watermarkSwitchPref.setChecked(value);
  }

  @Override
  public void setThemeDarkAppPref(String key, boolean isActivate) {
    themeappSwitchPref.setChecked(isActivate);
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
  public void hideWatermarkPreference() {
    watermarkPrefCategory = (PreferenceCategory)
            findPreference(getString(R.string.title_watermark_section));
    if (watermarkPrefCategory != null) {
      getPreferenceScreen().removePreference(watermarkPrefCategory);
    }
  }

  @Override
  public void itemDarkThemePurchased() {
    darkThemePurchased = true;
    updateIconLockItemsInAPP(themeappSwitchPref, darkThemePurchased);
  }

  @Override
  public void itemWatermarkPurchased() {
    watermarkPurchased = true;
    updateIconLockItemsInAPP(watermarkSwitchPref, watermarkPurchased);
  }

  @Override
  public void setVimojoStoreAvailable() {
    vimojoStoreAvailable = true;
  }

  @Override
  public void deactivateDarkTheme() {
    darkThemePurchased = false;
    themeappSwitchPref.setChecked(false);
  }

  @Override
  public void activateWatermark() {
    watermarkPurchased = false;
    watermarkSwitchPref.setChecked(true);
  }

  @Override
  public void setupUserAuthentication(final boolean userLoggedIn) {
    getActivity().runOnUiThread(() -> {
      final Preference authSetting = findPreference("auth");
      if (userLoggedIn) {
        authSetting.setTitle(R.string.sign_out);
      } else {
        authSetting.setTitle(R.string.sign_in_register);
      }
      authSetting.setOnPreferenceClickListener(
              new AuthPreferenceClickListener(userLoggedIn));
    });
  }

  @Override
  public void hideRegisterLoginView() {
    authPrefCategory = (PreferenceCategory)
            findPreference(getString(R.string.titleUserSection));
    if (authPrefCategory != null) {
      getPreferenceScreen().removePreference(authPrefCategory);
    }
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
  public void navigateToInitRegisterLogin() {
    Intent intent = new Intent(VimojoApplication.getAppContext(), InitRegisterLoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private AlertDialog createSignOutDialog() {
    DialogInterface.OnClickListener signOutListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            preferencesPresenter.signOutConfirmed();
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(
            getActivity(), R.style.VideonaDialog);
    return builder.setMessage(getString(R.string.sign_out_message_dialog))
            .setTitle(getString(R.string.sign_out_title_dialog))
            .setPositiveButton(getString(R.string.accept_sign_out), signOutListener)
            .setNegativeButton(getString(R.string.cancel_sign_out), signOutListener)
//                            .withCode(REQUEST_CODE_SIGN_OUT)
            .create();
  }


  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference connectionPref = findPreference(key);
    if (key.equals(ConfigPreferences.THEME_APP_DARK)) {
      if (isDarkThemeAvailable()) {
        // TODO(jliarte): 27/10/17 improve default theme setting with a build constant
        preferencesPresenter.trackThemeApp(sharedPreferences.getBoolean(key, false));
        restartActivity();
      } else if (!darkThemePurchased && themeappSwitchPref.isChecked()) {
        themeappSwitchPref.setChecked(false);
      } else {
        navigateTo(VimojoStoreActivity.class);
      }
      return;
    }
    if (key.equals(ConfigPreferences.WATERMARK)) {
      if (!isWatermarkAvailable()) {
        if (!watermarkSwitchPref.isChecked()) {
          watermarkSwitchPref.setChecked(true);
        } else {
          navigateTo(VimojoStoreActivity.class);
        }
      }
      return;
    }
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

  private boolean isDarkThemeAvailable() {
    return darkThemePurchased || !vimojoStoreAvailable;
  }

  private boolean isWatermarkAvailable() {
    return watermarkPurchased || !vimojoStoreAvailable;
  }

  private void restartActivity() {
    getActivity().finish();
    Intent intent = getActivity().getIntent();
    getActivity().startActivity(intent);
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

  private class AuthPreferenceClickListener implements Preference.OnPreferenceClickListener {
    private final boolean userLoggedIn;

    public AuthPreferenceClickListener(boolean userLoggedIn) {
      this.userLoggedIn = userLoggedIn;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
      if (userLoggedIn) {
        AlertDialog dialog = createSignOutDialog();
        dialog.show();
      }
      return true;
    }
  }
}
