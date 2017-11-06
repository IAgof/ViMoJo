package com.videonasocialmedia.vimojo.main.modules;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.main.internals.di.PerFragment;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetWatermarkPreferenceFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateWatermarkPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.views.fragment.SettingsFragment;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;


import dagger.Module;
import dagger.Provides;

/**
 * Created by alvaro on 11/01/17.
 */

@Module
public class FragmentPresentersModule {

  private Activity activity;
  private ListPreference qualityPref;
  private SwitchPreference transitionAudioPref;
  private SwitchPreference transitionVideoPref;
  private SwitchPreference watermarkPref;
  private SwitchPreference themeAppPref;
  private ListPreference frameRatePref;
  private Preference emailPref;
  private ListPreference resolutionPref;
  private PreferenceCategory cameraSettingsPref;
  private SettingsFragment settingsFragment;
  private Context context;
  private SharedPreferences sharedPreferences;

  public FragmentPresentersModule() {
  }

  public FragmentPresentersModule(SettingsFragment settingsFragment, Context context,
                                  SharedPreferences sharedPreferences,
                                  PreferenceCategory cameraSettingsPref,
                                  ListPreference resolutionPref,
                                  ListPreference qualityPref,
                                  SwitchPreference transitionsVideoPref,
                                  SwitchPreference transitionsAudioPref,
                                  SwitchPreference watermarkPref,
                                  SwitchPreference themeAppPref,
                                  Preference emailPref,
                                  Activity activity) {
    this.settingsFragment = settingsFragment;
    this.context = context;
    this.sharedPreferences = sharedPreferences;
    this.cameraSettingsPref = cameraSettingsPref;
    this.resolutionPref = resolutionPref;
    this.qualityPref = qualityPref;
    this.transitionVideoPref = transitionsVideoPref;
    this.transitionAudioPref = transitionsAudioPref;
    this.watermarkPref = watermarkPref;
    this.themeAppPref=themeAppPref;
    this.emailPref = emailPref;
    this.activity = activity;

  }

  // For singleton objects, annotate with same scope as component, i.e. @PerFragment
  @Provides
  @PerFragment
  public PreferencesPresenter providePreferencePresenter(
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
      GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
      BillingManager billingManager) {

    return new PreferencesPresenter(settingsFragment, context, sharedPreferences,
        cameraSettingsPref, resolutionPref, qualityPref, transitionVideoPref,
        transitionAudioPref, watermarkPref, themeAppPref, emailPref, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase,
        updateAudioTransitionPreferenceToProjectUseCase,
        updateVideoTransitionPreferenceToProjectUseCase,
        updateIntermediateTemporalFilesTransitionsUseCase,
        getWatermarkPreferenceFromProjectUseCase,
        updateWatermarkPreferenceToProjectUseCase,
        relaunchTranscoderTempBackgroundUseCase,
        getVideonaFormatFromCurrentProjectUseCase,
        billingManager);
  }

  @Provides
  GetMediaListFromProjectUseCase provideGetMediaListFromProject() {
    return new GetMediaListFromProjectUseCase();
  }

  @Provides
  GetPreferencesTransitionFromProjectUseCase provideGetPreferencesTransitionFromProject() {
    return new GetPreferencesTransitionFromProjectUseCase();
  }

  @Provides
  UpdateAudioTransitionPreferenceToProjectUseCase provideUpdateAudioTransitionPreference(
      ProjectRepository projectRepository) {
    return new UpdateAudioTransitionPreferenceToProjectUseCase(projectRepository);
  }

  @Provides
  UpdateVideoTransitionPreferenceToProjectUseCase provideUpdateVideoTransitionPreference(
      ProjectRepository projectRepository) {
    return new UpdateVideoTransitionPreferenceToProjectUseCase(projectRepository);
  }

  @Provides
  UpdateIntermediateTemporalFilesTransitionsUseCase provideUpdateIntermediateTempFilesTransitions(
      GetMediaListFromProjectUseCase getMediaListFromProjectUseCase) {
    return new UpdateIntermediateTemporalFilesTransitionsUseCase(getMediaListFromProjectUseCase);
  }

  @Provides
  UpdateWatermarkPreferenceToProjectUseCase provideUpdateWatermarkPreference(
          ProjectRepository projectRepository) {
    return new UpdateWatermarkPreferenceToProjectUseCase(projectRepository);
  }

  @Provides
  GetWatermarkPreferenceFromProjectUseCase provideGetWatermarkPreference() { 
    return new GetWatermarkPreferenceFromProjectUseCase();
  }

  @Provides
  RelaunchTranscoderTempBackgroundUseCase provideGetRelaunchTranscoder(
          VideoRepository videoRepository) {
    return new RelaunchTranscoderTempBackgroundUseCase(videoRepository);
  }

  @Provides
  GetVideoFormatFromCurrentProjectUseCase provideoGetVideonaFormat() {
    return new GetVideoFormatFromCurrentProjectUseCase();
  }

  @Provides
  NewClipImporter clipImporterProvider(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoToFormatUseCase adaptVideosUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscodingUseCase,
          VideoRepository videoRepository, VideoToAdaptRepository videoToAdaptRepository,
          ApplyAVTransitionsUseCase launchAVTranscoderAddAVTransitionUseCase) {
    return new NewClipImporter(getVideoFormatFromCurrentProjectUseCase, adaptVideosUseCase,
            launchAVTranscoderAddAVTransitionUseCase, relaunchTranscodingUseCase, videoRepository,
            videoToAdaptRepository);
  }

  @Provides
  BillingManager providesBillingManager() {
    return new BillingManager(activity);
  }

}
