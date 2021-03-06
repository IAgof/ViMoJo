package com.videonasocialmedia.vimojo.main.modules;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmDataSource;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.internals.di.PerFragment;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.vimojo.presentation.views.fragment.VideoGalleryFragment;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.upload.UploadDataSource;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateAudioTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateVideoTransitionPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.views.fragment.SettingsFragment;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
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
  private ProjectInstanceCache projectInstanceCache;
  private Fragment fragment;
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
    this.projectInstanceCache = (ProjectInstanceCache) activity.getApplication();
    this.currentProject = projectInstanceCache.getCurrentProject();
    this.sharedPreferences = sharedPreferences;
  }

  public FragmentPresentersModule(Fragment fragment) {
    this.fragment = fragment;
  }

  // For singleton objects, annotate with same scope as component, i.e. @PerFragment
  @Provides
  @PerFragment
  PreferencesPresenter providePreferencePresenter(
      GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
      UpdateAudioTransitionPreferenceToProjectUseCase
                  updateAudioTransitionPreferenceToProjectUseCase,
      UpdateVideoTransitionPreferenceToProjectUseCase
                  updateVideoTransitionPreferenceToProjectUseCase,
      UpdateIntermediateTemporalFilesTransitionsUseCase
                  updateIntermediateTemporalFilesTransitionsUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          UploadDataSource uploadDataSource, UserEventTracker userEventTracker,
          UpdateComposition updateComposition,
          @Named("ftpPublishingAvailable") boolean ftpPublishingAvailable,
          @Named("hideTransitionPreference") boolean hideTransitionPreference,
          @Named("showMoreAppsPreference") boolean showMoreAppsPreference,
          BackgroundExecutor backgroundExecutor) {
    return new PreferencesPresenter(
            settingsFragment, context, sharedPreferences,
            getPreferencesTransitionFromProjectUseCase,
            updateAudioTransitionPreferenceToProjectUseCase,
            updateVideoTransitionPreferenceToProjectUseCase,
            updateIntermediateTemporalFilesTransitionsUseCase,
            relaunchTranscoderTempBackgroundUseCase,
            uploadDataSource, projectInstanceCache, userEventTracker, updateComposition,
            ftpPublishingAvailable, hideTransitionPreference, showMoreAppsPreference,
            backgroundExecutor);
  }

  @Provides
  @PerFragment
  VideoGalleryPresenter providesVideoGalleryPresenter() {
    return new VideoGalleryPresenter((VideoGalleryFragment) fragment);
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
  AdaptVideoToFormatUseCase proviedAdaptVideoToFormatUseCase(
      VideoToAdaptRealmDataSource videoToAdaptRealmDataSource, MediaRepository mediaRepository) {
    return new AdaptVideoToFormatUseCase(videoToAdaptRealmDataSource, mediaRepository);
  }

  @Provides
  ApplyAVTransitionsUseCase provideLaunchTranscoderAddAVTransition(
      MediaRepository mediaRepository) {
    return  new ApplyAVTransitionsUseCase(currentProject, mediaRepository);
  }

  @Provides
  BillingManager providesBillingManager() {
    return new BillingManager();
  }

  @Provides
  UserAuth0Helper providesUserAuth0Helper(UserApiClient userApiClient,
                                          SharedPreferences sharedPreferences,
                                          UserEventTracker userEventTracker) {
    return new UserAuth0Helper(userApiClient, sharedPreferences, userEventTracker);
  }

  @Provides
  DownloadManager provideDownloadManager() {
    return (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
  }

  @Provides
  ObtainLocalVideosUseCase provideObtainLocalVideosUseCase() {
    return new ObtainLocalVideosUseCase();
  }

}
