package com.videonasocialmedia.vimojo.main.modules;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.asset.domain.usecase.GetCompositionAssets;
import com.videonasocialmedia.vimojo.asset.domain.usecase.RemoveMedia;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsMapperSupportedListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters.CameraSettingsPresenter;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.DeleteComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.GetCompositions;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionFrameRate;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionInfo;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionQuality;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionResolution;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateCompositionWatermark;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackDataSource;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.DetailProjectPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.GalleryProjectListPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.main.internals.di.PerActivity;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.DuplicatePreviewPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.InitAppPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoListErrorCheckerDelegate;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.InitAppActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VideoDuplicateActivity;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepositoryFromCameraSettings;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.domain.GetLicenseVimojoListUseCase;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseDetailPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseListPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseDetailView;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseListView;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.source.VimojoLicensesProvider;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.share.domain.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.share.domain.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.share.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverRecordPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverVolumePresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverVolumeView;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.VoiceOverRecordActivity;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.views.activity.VideoSplitActivity;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.store.presentation.mvp.presenters.VimojoStorePresenter;
import com.videonasocialmedia.vimojo.store.presentation.mvp.views.VimojoStoreView;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.sync.presentation.UploadToPlatform;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.presentation.mvp.presenters.EditTextPreviewPresenter;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters.UserProfilePresenter;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.featuresToggles.domain.usecase.FetchUserFeatures;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.vimojoapiclient.AuthApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 1/12/16.
 */

@Module
public class ActivityPresentersModule {
  private final VimojoActivity activity;
  private final Project currentProject;
  private final ProjectInstanceCache projectInstanceCache;
  private AutoFitTextureView textureView;
 // private GLCameraView cameraView = null;
  private String directorySaveVideos;
  private long freeStorage;
  private int defaultCameraIdSelected;

  public ActivityPresentersModule(VimojoActivity vimojoActivity) {
    this.activity = vimojoActivity;
    this.currentProject = ((VimojoApplication)this.activity.getApplication()).getCurrentProject();
    this.projectInstanceCache = (ProjectInstanceCache) this.activity.getApplication();
  }

/*  public ActivityPresentersModule(RecordActivity activity, boolean externalIntent,
                                  GLCameraView cameraView) {
    this.activity = activity;
    this.externalIntent = externalIntent;
    this.cameraView = cameraView;
  }*/

  public ActivityPresentersModule(RecordCamera2Activity activity,
                                  String directorySaveVideos,
                                  AutoFitTextureView textureView,
                                  long freeStorage, int defaultCameraIdSelected) {
    this.activity = activity;
    this.textureView = textureView;
    this.directorySaveVideos = directorySaveVideos;
    this.freeStorage = freeStorage;
    this.currentProject = ((VimojoApplication)this.activity.getApplication()).getCurrentProject();
    this.projectInstanceCache = (ProjectInstanceCache) this.activity.getApplication();
    this.defaultCameraIdSelected = defaultCameraIdSelected;
  }

  @Provides @PerActivity
  VoiceOverVolumePresenter provideVoiceOverVolumePresenter(
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase, ModifyTrackUseCase
                  modifyTrackUseCase, RemoveAudioUseCase removeAudioUseCase,
          UpdateComposition updateComposition) {
    return new VoiceOverVolumePresenter(activity, (VoiceOverVolumeView) activity,
            getMediaListFromProjectUseCase, getPreferencesTransitionFromProjectUseCase,
            getAudioFromProjectUseCase, modifyTrackUseCase, removeAudioUseCase,
            projectInstanceCache, updateComposition);
  }

  @Provides @PerActivity
  VoiceOverRecordPresenter provideVoiceOverRecordPresenter(
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
          UserEventTracker userEventTracker, UpdateComposition updateComposition) {
    return new VoiceOverRecordPresenter(activity, (VoiceOverRecordActivity) activity,
            getMediaListFromProjectUseCase, getPreferencesTransitionFromProjectUseCase,
            addAudioUseCase, removeAudioUseCase, userEventTracker, projectInstanceCache,
            updateComposition);
  }

  @Provides @PerActivity
  MusicDetailPresenter provideMusicDetailPresenter(
          UserEventTracker userEventTracker,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
          ModifyTrackUseCase modifyTrackUseCase, GetMusicListUseCase getMusicListUseCase,
          UpdateComposition updateComposition) {
    return new MusicDetailPresenter((MusicDetailView) activity, activity, userEventTracker,
            getMediaListFromProjectUseCase, getAudioFromProjectUseCase,
            getPreferencesTransitionFromProjectUseCase, addAudioUseCase, removeAudioUseCase,
            modifyTrackUseCase, getMusicListUseCase, projectInstanceCache, updateComposition);
  }

  @Provides @PerActivity
  EditPresenter provideEditPresenter(UserEventTracker userEventTracker,
                                     GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                     RemoveVideoFromProjectUseCase removeVideosFromProjectUseCase,
                                     ReorderMediaItemUseCase reorderMediaItemUseCase,
                                     UpdateComposition updateComposition, RemoveMedia removeMedia) {
    return new EditPresenter((EditActivity) activity, activity, (EditActivity) activity,
            userEventTracker, getMediaListFromProjectUseCase, removeVideosFromProjectUseCase,
            reorderMediaItemUseCase, projectInstanceCache, updateComposition,
            removeMedia);
  }

  @Provides @PerActivity
  SoundPresenter provideSoundPresenter(
          ModifyTrackUseCase modifyTrackUseCase, UpdateComposition updateComposition,
          @Named("voiceOverAvailable") boolean voiceOverAvailable) {
    return new SoundPresenter((SoundActivity) activity, modifyTrackUseCase, projectInstanceCache,
        updateComposition, voiceOverAvailable);
  }

  @Provides @PerActivity
  MusicListPresenter provideMusicListPresenter(
          GetMusicListUseCase getMusicListUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase) {
    return new MusicListPresenter((MusicListView) activity, activity, getMusicListUseCase,
            getMediaListFromProjectUseCase, getAudioFromProjectUseCase,
            getPreferencesTransitionFromProjectUseCase, projectInstanceCache);
  }

  @Provides @PerActivity
  LicenseListPresenter provideLicenseListPresenter(
          GetLicenseVimojoListUseCase getLicenseVimojoListUseCase) {
    return new LicenseListPresenter((LicenseListView) activity, activity,
            getLicenseVimojoListUseCase);
  }

  @Provides @PerActivity
  CameraSettingsPresenter provideCameraSettingPresenter(
          UserEventTracker userEventTracker,
          GetCameraSettingsMapperSupportedListUseCase getCameraSettingsMapperSupportedListUseCase,
          CameraSettingsDataSource cameraSettingsRepository,
          SetCompositionQuality setCompositionQuality, UpdateComposition updateComposition,
          SetCompositionFrameRate setCompositionFrameRate,
          SetCompositionResolution setCompositionResolution,
          @Named("showCameraProAvailable") boolean showCameraPro,
          @Named("selectFrameRateAvailable") boolean allowSelectFrameRate,
          @Named("selectResolutionAvailable") boolean allowSelectResolution) {
    return new CameraSettingsPresenter((CameraSettingsView) activity, userEventTracker,
        getCameraSettingsMapperSupportedListUseCase, cameraSettingsRepository,
            updateComposition, projectInstanceCache, setCompositionQuality, setCompositionFrameRate,
            setCompositionResolution, showCameraPro, allowSelectFrameRate, allowSelectResolution);
  }

  @Provides @PerActivity
  VimojoStorePresenter provideVimojoStorePresenter(BillingManager billingManager) {
    return new VimojoStorePresenter((VimojoStoreView) activity, activity, billingManager);
  }

  @Provides @PerActivity
  LicenseDetailPresenter provideLicenseDetailPresenter(
          GetLicenseVimojoListUseCase getLicenseVimojoListUseCase) {
    return  new LicenseDetailPresenter((LicenseDetailView) activity, activity,
            getLicenseVimojoListUseCase);
  }

  @Provides @PerActivity
  DuplicatePreviewPresenter provideDuplicatePresenter(
          UserEventTracker userEventTracker, AddVideoToProjectUseCase addVideoToProjectUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          UpdateComposition updateComposition) {
    return new DuplicatePreviewPresenter((VideoDuplicateActivity) activity, userEventTracker,
            addVideoToProjectUseCase, getMediaListFromProjectUseCase, projectInstanceCache,
            updateComposition);
  }

  @Provides @PerActivity
  GalleryPagerPresenter provideGalleryPagerPresenter(
          AddVideoToProjectUseCase addVideoToProjectUseCase,
          ApplyAVTransitionsUseCase applyAVTransitionsUseCase, SharedPreferences sharedPreferences,
          UpdateComposition updateComposition, SetCompositionResolution setCompositionResolution) {
    return new GalleryPagerPresenter((GalleryActivity) activity, activity, addVideoToProjectUseCase,
            applyAVTransitionsUseCase, sharedPreferences, projectInstanceCache, updateComposition,
            setCompositionResolution);
  }

 /* @Provides @PerActivity
  RecordPresenter provideRecordPresenter(
          UserEventTracker userEventTracker, SharedPreferences sharedPreferences,
          AddVideoToProjectUseCase addVideoToProjectUseCase,
          ApplyAVTransitionsUseCase applyAVTransitionsUseCase,
          GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
          VideoDataSource videoRepository) {
    return new RecordPresenter(activity, (RecordActivity) activity, userEventTracker, cameraView,
        sharedPreferences, externalIntent, addVideoToProjectUseCase, videoRepository,
            applyAVTransitionsUseCase, getVideonaFormatFromCurrentProjectUseCase);
  }*/

  @Provides @PerActivity
  RecordCamera2Presenter provideRecordCamera2Presenter(
          UserEventTracker userEventTracker, SharedPreferences sharedPreferences,
          AddVideoToProjectUseCase addVideoToProjectUseCase, Camera2Wrapper camera2wrapper,
          NewClipImporter newClipImporter, CameraSettingsDataSource cameraSettingsRepository,
          UpdateComposition updateComposition) {
    return new RecordCamera2Presenter(activity, (RecordCamera2Activity) activity, userEventTracker,
            sharedPreferences, addVideoToProjectUseCase, newClipImporter, camera2wrapper,
            cameraSettingsRepository, projectInstanceCache, updateComposition);
  }

  @Provides @PerActivity
  SplitPreviewPresenter provideSplitPresenter(
          UserEventTracker userEventTracker, SplitVideoUseCase splitVideoUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          UpdateComposition updateComposition) {
    return new SplitPreviewPresenter((VideoSplitActivity) activity, userEventTracker,
            splitVideoUseCase, getMediaListFromProjectUseCase, projectInstanceCache,
            updateComposition);
  }

  @Provides @PerActivity
  TrimPreviewPresenter provideTrimPresenter(
          SharedPreferences sharedPreferences, UserEventTracker userEventTracker,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          ModifyVideoDurationUseCase modifyVideoDurationUseCase,
          UpdateComposition updateComposition) {
    return new TrimPreviewPresenter((VideoTrimActivity) activity, sharedPreferences,
        userEventTracker, getMediaListFromProjectUseCase, modifyVideoDurationUseCase,
        projectInstanceCache, updateComposition);
  }

  @Provides @PerActivity
  ShareVideoPresenter provideVideoSharePresenter(
          UserEventTracker userEventTracker, SharedPreferences sharedPreferences,
          AddLastVideoExportedToProjectUseCase addLastVideoExportedProjectUseCase,
          ExportProjectUseCase exportProjectUseCase,
          ObtainNetworksToShareUseCase obtainNetworksToShareUseCase,
          GetFtpListUseCase getFtpListUseCase, UploadToPlatform uploadToPlatform,
          RunSyncAdapterHelper runSyncAdapterHelper,
          UserAuth0Helper userAuth0Helper, UpdateComposition updateComposition,
          FetchUserFeatures fetchUserFeatures,
          @Named("vimojoPlatformAvailable") boolean vimojoPlatformAvailable,
          @Named("ftpPublishingAvailable") boolean ftpPublishingAvailable,
          @Named("showAds") boolean showAds) {
    return new ShareVideoPresenter(activity, (ShareActivity) activity, userEventTracker,
            sharedPreferences, addLastVideoExportedProjectUseCase,
            exportProjectUseCase, obtainNetworksToShareUseCase, getFtpListUseCase,
            uploadToPlatform, runSyncAdapterHelper, projectInstanceCache,
            userAuth0Helper, updateComposition,
            fetchUserFeatures, vimojoPlatformAvailable, ftpPublishingAvailable, showAds);
  }

  @Provides @PerActivity
  InitAppPresenter provideInitAppPresenter(
          SharedPreferences sharedPreferences,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          CameraSettingsDataSource cameraSettingsRepository,
          RunSyncAdapterHelper runSyncAdapterHelper, SaveComposition saveComposition,
          @Named("watermarkIsForced") boolean watermarkIsForced,
          @Named("showAds") boolean showAds) {
    return new InitAppPresenter(activity, (InitAppActivity) activity, sharedPreferences,
            createDefaultProjectUseCase, cameraSettingsRepository, runSyncAdapterHelper,
            (ProjectInstanceCache) activity.getApplication(), saveComposition,
            watermarkIsForced, showAds);
  }

  @Provides @PerActivity
  EditorPresenter provideEditorPresenter(
          UserEventTracker userEventTracker,
          SharedPreferences sharedPreferences,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          NewClipImporter newClipImporter, BillingManager billingManager,
          SaveComposition saveComposition, UpdateComposition updateComposition,
          RemoveMedia removeMedia, UpdateCompositionWatermark updateWatermark,
          @Named("showWaterMarkSwitch") boolean showWaterMarkSwitch,
          @Named("vimojoStoreAvailable") boolean vimojoStoreAvailable,
          @Named("vimojoPlatformAvailable") boolean vimojoPlatformAvailable,
          @Named("watermarkIsForced") boolean watermarkIsForced) {
    return new EditorPresenter((EditorActivity) activity, (EditorActivity) activity,
            sharedPreferences, activity, userEventTracker, createDefaultProjectUseCase,
            getMediaListFromProjectUseCase, removeVideoFromProjectUseCase,
            getAudioFromProjectUseCase, getPreferencesTransitionFromProjectUseCase,
            relaunchTranscoderTempBackgroundUseCase, newClipImporter,
            billingManager, projectInstanceCache, saveComposition, removeMedia,
            updateWatermark, updateComposition, showWaterMarkSwitch,
            vimojoStoreAvailable, vimojoPlatformAvailable, watermarkIsForced);
  }

  @Provides @PerActivity
  GalleryProjectListPresenter provideGalleryProjectListPresenter(
          ProjectRepository projectRepository, SharedPreferences sharedPreferences,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          DuplicateProjectUseCase duplicateProjectUseCase,
          DeleteComposition deleteComposition, SaveComposition saveComposition,
          UpdateComposition updateComposition, GetCompositions getCompositions,
          GetCompositionAssets getCompositionAssets,
          @Named("watermarkIsForced") boolean watermarkIsForced) {
    return new GalleryProjectListPresenter((GalleryProjectListActivity) activity, sharedPreferences,
            projectRepository, createDefaultProjectUseCase, duplicateProjectUseCase,
            deleteComposition, (ProjectInstanceCache) activity.getApplication(), saveComposition,
            updateComposition, getCompositions, getCompositionAssets, watermarkIsForced);
  }

  @Provides @PerActivity
  DetailProjectPresenter provideDetailProjectPresenter(
          UserEventTracker userEventTracker, UpdateComposition updateComposition,
          SetCompositionInfo setCompositionInfo) {
    return new DetailProjectPresenter(activity, (DetailProjectActivity) activity,
        userEventTracker, projectInstanceCache, updateComposition,
            setCompositionInfo);
  }

  @Provides @PerActivity
  EditTextPreviewPresenter provideEditTextPreviewPresenter(
              UserEventTracker userEventTracker,
              GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
              ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase) {
    return new EditTextPreviewPresenter((VideoEditTextActivity) activity, activity,
        userEventTracker, getMediaListFromProjectUseCase,
        modifyVideoTextAndPositionUseCase, projectInstanceCache);
  }

  @Provides @PerActivity
  UserProfilePresenter provideUserProfilePresenter(
          SharedPreferences sharedPreferences, ObtainLocalVideosUseCase obtainLocalVideosUseCase,
          UserAuth0Helper userAuth0Helper, FetchUserFeatures fetchUserFeatures,
          @Named("vimojoPlatformAvailable") boolean vimojoPlatformAvailable) {
    return new  UserProfilePresenter((UserProfileView) activity, sharedPreferences,
        obtainLocalVideosUseCase, userAuth0Helper,
            fetchUserFeatures, vimojoPlatformAvailable);
  }

  @Provides
  AddVideoToProjectUseCase provideVideoAdder(
          ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase) {
    return new AddVideoToProjectUseCase(launchTranscoderAddAVTransitionUseCase);
  }

  @Provides
  SplitVideoUseCase provideVideoSplitter(AddVideoToProjectUseCase addVideoToProjectUseCase,
                                         ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                                         VideoDataSource videoRepository) {
    return new SplitVideoUseCase(addVideoToProjectUseCase, modifyVideoDurationUseCase,
            videoRepository);
  }

  @Provides
  CreateDefaultProjectUseCase provideDefaultProjectCreator(ProfileRepository profileRepository) {
    return new CreateDefaultProjectUseCase(profileRepository
    );
  }

  @Provides
  GetAudioFromProjectUseCase provideMusicRetriever() {
    return new GetAudioFromProjectUseCase();
  }

  @Provides GetMusicListUseCase provideMusicListUseCase() {
    return new GetMusicListUseCase(activity);
  }

  @Provides GetLicenseVimojoListUseCase provideLicenseListUseCase(
      VimojoLicensesProvider vimojoLicencesProvider) {
    return new GetLicenseVimojoListUseCase(vimojoLicencesProvider);
  }

  @Provides
  GetCameraSettingsMapperSupportedListUseCase provideCameraSettingsListUseCase(
          CameraSettingsDataSource cameraSettingsRepository) {
    return new GetCameraSettingsMapperSupportedListUseCase(
            activity, currentProject, cameraSettingsRepository);
  }

  @Provides
  GetCameraSettingsUseCase provideCameraSettingsUseCase(CameraSettingsDataSource
                                                        cameraSettingsRepository) {
    return new GetCameraSettingsUseCase(cameraSettingsRepository);
  }

  @Provides GetMediaListFromProjectUseCase provideMediaListRetriever() {
    return new GetMediaListFromProjectUseCase();
  }

  @Provides DuplicateProjectUseCase provideDuplicateProject() {
    return new DuplicateProjectUseCase();
  }

  @Provides
  DeleteComposition provideDeleteComposition(ProjectRepository projectRepository,
                                         VideoDataSource videoRepository,
                                         MusicDataSource musicRepository,
                                         TrackDataSource trackRepository) {
    return new DeleteComposition(projectRepository
    );
  }

  @Provides CheckIfProjectHasBeenExportedUseCase provideCheckIfProjectHasBeenExported() {
    return new CheckIfProjectHasBeenExportedUseCase();
  }

  @Provides ModifyVideoDurationUseCase provideModifyVideoDurationUseCase(
          VideoDataSource videoRepository, VideoToAdaptDataSource videoToAdaptRepository) {
    return new ModifyVideoDurationUseCase(videoRepository, videoToAdaptRepository);
  }

  @Provides ModifyVideoTextAndPositionUseCase provideModifyVideoTextAndPositionUseCase(
          VideoDataSource videoRepository,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          VideoToAdaptDataSource videoToAdaptRepository) {
    return new ModifyVideoTextAndPositionUseCase(videoRepository,
            relaunchTranscoderTempBackgroundUseCase, videoToAdaptRepository);
  }

  @Provides
  ApplyAVTransitionsUseCase provideLaunchTranscoderAddAVTransition(
          VideoDataSource videoRepository) {
   return  new ApplyAVTransitionsUseCase(currentProject, videoRepository);
  }

  @Provides GetVideoFormatFromCurrentProjectUseCase
      provideVideoFormatFromCurrentProjectUseCase(ProjectRepository projectRepository) {
    return new GetVideoFormatFromCurrentProjectUseCase(projectRepository);
  }

  @Provides
  AdaptVideoToFormatUseCase provideAdaptVideoRecordedToVideoFormatUseCase(
          VideoToAdaptDataSource videoToAdaptRepository, MediaRepository mediaRepository) {
    return new AdaptVideoToFormatUseCase(videoToAdaptRepository, mediaRepository);
  }

  @Provides RelaunchTranscoderTempBackgroundUseCase
  provideRelaunchTranscoderTempBackgroundUseCase(VideoDataSource videoRepository) {
    return new RelaunchTranscoderTempBackgroundUseCase(currentProject, videoRepository);
  }

  @Provides ExportProjectUseCase provideProjectExporter(
          VideoToAdaptDataSource videoToAdaptRepository) {
    return new ExportProjectUseCase(videoToAdaptRepository);
  }

  @Provides VideoListErrorCheckerDelegate providesVideoListErrorCheckerDelegate() {
    return new VideoListErrorCheckerDelegate();
  }

  @Provides
  Camera2Wrapper provideCamera2wrapper(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          GetCameraSettingsUseCase getCameraSettingsUseCase) {
    return new Camera2Wrapper(activity, getCameraSettingsUseCase.getCameraIdSelected(), textureView,
            directorySaveVideos, getVideoFormatFromCurrentProjectUseCase
                .getVideoRecordedFormatFromCurrentProjectUseCase(currentProject), freeStorage);
  }

  @Provides VimojoLicensesProvider provideLicenseProvider() {
    return new VimojoLicensesProvider(activity);
  }

  @Provides
  NewClipImporter provideClipImporter(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoToFormatUseCase adaptVideoToFormatUseCase,
          VideoDataSource videoRepository, VideoToAdaptDataSource videoToAdaptRepository,
          ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase) {
    return new NewClipImporter(getVideoFormatFromCurrentProjectUseCase,
            adaptVideoToFormatUseCase, launchTranscoderAddAVTransitionUseCase,
            videoRepository,
            videoToAdaptRepository);
  }

  @Provides BillingManager provideBillingManager() {
    return new BillingManager();
  }

  @Provides ProfileRepository provideProfileRepository(
          CameraSettingsDataSource cameraSettingsRepository,
          @Named("showCameraProAvailable") boolean showCameraPro) {
    return new ProfileRepositoryFromCameraSettings(cameraSettingsRepository,
        defaultCameraIdSelected, showCameraPro);
  }

  @Provides ObtainLocalVideosUseCase provideObtainLocalVideosUseCase() {
    return new ObtainLocalVideosUseCase();
  }

  @Provides
  AuthApiClient provideVimojoAuthenticator() {
    return new AuthApiClient();
  }

  @Provides ObtainNetworksToShareUseCase provideObtainNetworksToShareUseCase() {
    return new ObtainNetworksToShareUseCase();
  }

  @Provides GetFtpListUseCase provideGetFtpListUseCase() {
    return new GetFtpListUseCase();
  }

  @Provides
  CompositionApiClient provideCompositionApiClient() {
    return new CompositionApiClient();
  }

  @Provides
  UserAuth0Helper provideUserAuth0Helper(UserApiClient userApiClient) {
    return new UserAuth0Helper(userApiClient);
  }

  @Provides
  DownloadManager provideDownloadManager() {
    return (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
  }

}
