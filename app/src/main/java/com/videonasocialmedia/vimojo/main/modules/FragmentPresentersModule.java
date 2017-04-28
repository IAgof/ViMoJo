package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.main.internals.di.PerFragment;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.domain.GetWatermarkPreferenceFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.domain.UpdateWatermarkPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.vimojo.settings.presentation.views.fragment.SettingsFragment;


import dagger.Module;
import dagger.Provides;

/**
 * Created by alvaro on 11/01/17.
 */

@Module
public class FragmentPresentersModule {

  private ListPreference qualityPref;
  private SwitchPreference transitionAudioPref;
  private SwitchPreference transitionVideoPref;
  private SwitchPreference watermarkPref;
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
                                  ListPreference frameRatePref,
                                  SwitchPreference transitionsVideoPref,
                                  SwitchPreference transitionsAudioPref,
                                  SwitchPreference watermarkPref,
                                  Preference emailPref) {
    this.settingsFragment = settingsFragment;
    this.context = context;
    this.sharedPreferences = sharedPreferences;
    this.cameraSettingsPref = cameraSettingsPref;
    this.resolutionPref = resolutionPref;
    this.qualityPref = qualityPref;
    this.frameRatePref = frameRatePref;
    this.transitionVideoPref = transitionsVideoPref;
    this.transitionAudioPref = transitionsAudioPref;
    this.watermarkPref = watermarkPref;
    this.emailPref = emailPref;

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
             UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase){

    return new PreferencesPresenter(settingsFragment, context, sharedPreferences,
        cameraSettingsPref, resolutionPref, qualityPref, frameRatePref, transitionVideoPref,
        transitionAudioPref, watermarkPref, emailPref, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase,
        updateAudioTransitionPreferenceToProjectUseCase,
        updateVideoTransitionPreferenceToProjectUseCase,
        updateIntermediateTemporalFilesTransitionsUseCase,
        getWatermarkPreferenceFromProjectUseCase,
        updateWatermarkPreferenceToProjectUseCase,
        updateVideoRepositoryUseCase);
  }

  @Provides
  GetMediaListFromProjectUseCase provideGetMediaListFromProject(){
    return new GetMediaListFromProjectUseCase();
  }

  @Provides
  GetPreferencesTransitionFromProjectUseCase provideGetPreferencesTransitionFromProject(){
    return new GetPreferencesTransitionFromProjectUseCase();
  }

  @Provides
  UpdateAudioTransitionPreferenceToProjectUseCase provideUpdateAudioTransitionPreference(
      ProjectRepository projectRepository){
    return new UpdateAudioTransitionPreferenceToProjectUseCase(projectRepository);
  }

  @Provides
  UpdateVideoTransitionPreferenceToProjectUseCase provideUpdateVideoTransitionPreference(
      ProjectRepository projectRepository){
    return new UpdateVideoTransitionPreferenceToProjectUseCase(projectRepository);
  }

  @Provides
  UpdateIntermediateTemporalFilesTransitionsUseCase provideUpdateIntermediateTempFilesTransitions(
      GetMediaListFromProjectUseCase getMediaListFromProjectUseCase){
    return new UpdateIntermediateTemporalFilesTransitionsUseCase(getMediaListFromProjectUseCase);
  }

  @Provides
  UpdateVideoRepositoryUseCase provideUpdateVideoRepositoryUseCase(VideoRepository videoRepository){
    return new UpdateVideoRepositoryUseCase(videoRepository);
  }

  @Provides
  UpdateWatermarkPreferenceToProjectUseCase provideUpdateWatermarkPreference(ProjectRepository
                                                                             projectRepository){
    return new UpdateWatermarkPreferenceToProjectUseCase(projectRepository);
  }

  @Provides
  GetWatermarkPreferenceFromProjectUseCase provideGetWatermarkPreference(){
    return new GetWatermarkPreferenceFromProjectUseCase();
  }

}
