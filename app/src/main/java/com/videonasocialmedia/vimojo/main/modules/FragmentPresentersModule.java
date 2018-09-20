package com.videonasocialmedia.vimojo.main.modules;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;


import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.auth0.accountmanager.GetAccount;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.auth0.accountmanager.GetAccount;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateCompositionWatermark;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.internals.di.PerFragment;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.upload.UploadDataSource;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.views.fragment.SettingsFragment;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.featuresToggles.domain.usecase.FetchUserFeatures;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by alvaro on 11/01/17.
 */

@Module
public class FragmentPresentersModule {
  private final ProjectInstanceCache projectInstanceCache;
  private Activity activity;
  private SettingsFragment settingsFragment;
  private Context context;
  private SharedPreferences sharedPreferences;
  private Project currentProject;

  public FragmentPresentersModule(
          SettingsFragment settingsFragment, Context context,
          SharedPreferences sharedPreferences, Activity activity) {
    this.settingsFragment = settingsFragment;
    this.activity = activity;
    this.context = context;
    this.projectInstanceCache = (ProjectInstanceCache) this.activity.getApplication();
    this.currentProject = projectInstanceCache.getCurrentProject();
    this.sharedPreferences = sharedPreferences;
  }

  // For singleton objects, annotate with same scope as component, i.e. @PerFragment
  @Provides
  @PerFragment
  PreferencesPresenter providePreferencePresenter(
      GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
      GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
      UpdateAudioTransitionPreferenceToProjectUseCase
                  updateAudioTransitionPreferenceToProjectUseCase,
      UpdateVideoTransitionPreferenceToProjectUseCase
                  updateVideoTransitionPreferenceToProjectUseCase,
      UpdateIntermediateTemporalFilesTransitionsUseCase
                  updateIntermediateTemporalFilesTransitionsUseCase,
      UpdateCompositionWatermark updateCompositionWatermark,
      RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
      GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
      BillingManager billingManager, UserAuth0Helper userAuth0Helper,
      UploadDataSource uploadRepository, GetAccount getAccount,
      UpdateComposition updateComposition, FetchUserFeatures fetchUserFeatures,
      @Named("vimojoStoreAvailable") boolean vimojoStoreAvailable,
      @Named("showWaterMarkSwitch") boolean showWaterMarkSwitch,
      @Named("vimojoPlatformAvailable") boolean vimojoPlatformAvailable,
      @Named("ftpPublishingAvailable") boolean ftpPublishingAvailable,
      @Named("hideTransitionPreference") boolean hideTransitionPreference,
      @Named("showMoreAppsPreference") boolean showMoreAppsPreference,
      BackgroundExecutor backgroundExecutor) {
    return new PreferencesPresenter(
            settingsFragment, context, sharedPreferences,
            getMediaListFromProjectUseCase,
            getPreferencesTransitionFromProjectUseCase,
            updateAudioTransitionPreferenceToProjectUseCase,
            updateVideoTransitionPreferenceToProjectUseCase,
            updateIntermediateTemporalFilesTransitionsUseCase,
            updateCompositionWatermark, relaunchTranscoderTempBackgroundUseCase,
            getVideonaFormatFromCurrentProjectUseCase, billingManager, userAuth0Helper,
            uploadRepository, projectInstanceCache, getAccount, updateComposition,
            fetchUserFeatures, vimojoStoreAvailable,
            showWaterMarkSwitch, vimojoPlatformAvailable, ftpPublishingAvailable,
            hideTransitionPreference, showMoreAppsPreference, backgroundExecutor);
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
  UpdateIntermediateTemporalFilesTransitionsUseCase provideUpdateIntermediateTempFilesTransitions() {
    return new UpdateIntermediateTemporalFilesTransitionsUseCase();
  }

  @Provides
  RelaunchTranscoderTempBackgroundUseCase provideGetRelaunchTranscoder(
          MediaRepository mediaRepository) {
    return new RelaunchTranscoderTempBackgroundUseCase(currentProject, mediaRepository);
  }

  @Provides
  GetVideoFormatFromCurrentProjectUseCase provideoGetVideonaFormat(
          ProjectRepository projectRepository) {
    return new GetVideoFormatFromCurrentProjectUseCase(projectRepository);
  }

  @Provides
  NewClipImporter clipImporterProvider(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoToFormatUseCase adaptVideosUseCase, VideoDataSource videoRepository,
          VideoToAdaptDataSource videoToAdaptRepository,
          ApplyAVTransitionsUseCase launchAVTranscoderAddAVTransitionUseCase) {
    return new NewClipImporter(getVideoFormatFromCurrentProjectUseCase, adaptVideosUseCase,
            launchAVTranscoderAddAVTransitionUseCase, videoRepository, videoToAdaptRepository
    );
  }

  @Provides
  BillingManager providesBillingManager() {
    return new BillingManager();
  }

  @Provides
  UserAuth0Helper providesUserAuth0Helper(UserApiClient userApiClient) {
    return new UserAuth0Helper(userApiClient);
  }

  @Provides
  DownloadManager provideDownloadManager() {
    return (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
  }

  @Provides
  BackgroundExecutor providesBackgroundExecutor() {
    return new BackgroundExecutor();
  }
}
