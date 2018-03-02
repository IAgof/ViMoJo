package com.videonasocialmedia.vimojo.main.modules;

import android.content.SharedPreferences;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.auth.presentation.view.utils.EmailPatternValidator;
import com.videonasocialmedia.vimojo.auth.presentation.mvp.views.UserAuthView;
import com.videonasocialmedia.vimojo.share.domain.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.share.domain.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.share.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.share.presentation.views.utils.LoggedValidator;
import com.videonasocialmedia.vimojo.sync.UploadToPlatformQueue;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.vimojoapiclient.AuthApiClient;
import com.videonasocialmedia.vimojo.auth.presentation.mvp.presenters.UserAuthPresenter;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.DetailProjectPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.GalleryProjectListPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoListErrorCheckerDelegate;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.internals.di.PerActivity;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.DuplicatePreviewPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.InitAppPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VideoDuplicateActivity;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepositoryFromCameraSettings;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.cameraSettings.domain.GetCameraSettingsMapperSupportedListUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters.CameraSettingsPresenter;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.source.VimojoLicensesProvider;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.domain.GetLicenseVimojoListUseCase;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseDetailPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseListPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseDetailView;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseListView;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateWatermarkPreferenceToProjectUseCase;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.store.presentation.mvp.presenters.VimojoStorePresenter;
import com.videonasocialmedia.vimojo.store.presentation.mvp.views.VimojoStoreView;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverRecordPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverVolumePresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverVolumeView;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.VoiceOverRecordActivity;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.views.activity.VideoSplitActivity;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.presentation.mvp.presenters.EditTextPreviewPresenter;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters.UserProfilePresenter;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 1/12/16.
 */

@Module
public class ActivityPresentersModule {
  private final VimojoActivity activity;
  private AutoFitTextureView textureView;
 // private GLCameraView cameraView = null;
  private boolean externalIntent;
  private String directorySaveVideos;
  private long freeStorage;

  public ActivityPresentersModule(VimojoActivity vimojoActivity) {
    this.activity = vimojoActivity;
  }

/*  public ActivityPresentersModule(RecordActivity activity, boolean externalIntent,
                                  GLCameraView cameraView) {
    this.activity = activity;
    this.externalIntent = externalIntent;
    this.cameraView = cameraView;
  }*/

  public ActivityPresentersModule(RecordCamera2Activity activity,
                                  String directorySaveVideos,
                                  AutoFitTextureView textureView, long freeStorage) {
    this.activity = activity;
    this.textureView = textureView;
    this.directorySaveVideos = directorySaveVideos;
    this.freeStorage = freeStorage;
  }

  @Provides @PerActivity
  VoiceOverVolumePresenter provideVoiceOverVolumePresenter(
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase, ModifyTrackUseCase
                  modifyTrackUseCase, RemoveAudioUseCase removeAudioUseCase) {
    return new VoiceOverVolumePresenter(activity, (VoiceOverVolumeView) activity,
            getMediaListFromProjectUseCase, getPreferencesTransitionFromProjectUseCase,
            getAudioFromProjectUseCase, modifyTrackUseCase, removeAudioUseCase);
  }

  @Provides @PerActivity
  VoiceOverRecordPresenter provideVoiceOverRecordPresenter(
      GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
      GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
      AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
      UserEventTracker userEventTracker) {
    return new VoiceOverRecordPresenter(activity, (VoiceOverRecordActivity) activity,
            getMediaListFromProjectUseCase, getPreferencesTransitionFromProjectUseCase,
            addAudioUseCase, removeAudioUseCase, userEventTracker);
  }

  @Provides @PerActivity
  MusicDetailPresenter provideMusicDetailPresenter(
          UserEventTracker userEventTracker,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
          ModifyTrackUseCase modifyTrackUseCase) {
    return new MusicDetailPresenter((MusicDetailView) activity, userEventTracker,
            getMediaListFromProjectUseCase, getAudioFromProjectUseCase,
            getPreferencesTransitionFromProjectUseCase, addAudioUseCase, removeAudioUseCase,
            modifyTrackUseCase, activity);
  }

  @Provides @PerActivity
  EditPresenter provideEditPresenter(UserEventTracker userEventTracker,
                                     GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                     RemoveVideoFromProjectUseCase removeVideosFromProjectUseCase,
                                     ReorderMediaItemUseCase reorderMediaItemUseCase) {
    return new EditPresenter((EditActivity) activity, (EditActivity) activity, (EditActivity) activity,
            userEventTracker, getMediaListFromProjectUseCase, removeVideosFromProjectUseCase,
            reorderMediaItemUseCase);
  }

  @Provides @PerActivity
  SoundPresenter provideSoundPresenter(ModifyTrackUseCase modifyTrackUseCase,
                                       VideoListErrorCheckerDelegate
                                           videoListErrorCheckerDelegate) {
    return new SoundPresenter((SoundActivity) activity, modifyTrackUseCase,
        videoListErrorCheckerDelegate);
  }

  @Provides @PerActivity
  MusicListPresenter provideMusicListPresenter(
          GetMusicListUseCase getMusicListUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase) {
    return new MusicListPresenter((MusicListView) activity, activity, getMusicListUseCase,
        getMediaListFromProjectUseCase, getAudioFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase);
  }

  @Provides @PerActivity
  LicenseListPresenter provideLicenseListPresenter(GetLicenseVimojoListUseCase
                                                       getLicenseVimojoListUseCase) {
    return new LicenseListPresenter((LicenseListView) activity, activity, getLicenseVimojoListUseCase);
  }

  @Provides @PerActivity
  CameraSettingsPresenter provideCameraSettingPresenter(UserEventTracker userEventTracker,
                                                        GetCameraSettingsMapperSupportedListUseCase
                                                            getCameraSettingsMapperSupportedListUseCase,
                                                        CameraSettingsRepository
                                                                cameraSettingsRepository,
                                                        ProjectRepository
                                                          projectRepository) {
    return new CameraSettingsPresenter((CameraSettingsView) activity, userEventTracker,
        getCameraSettingsMapperSupportedListUseCase, cameraSettingsRepository, projectRepository);
  }

  @Provides @PerActivity
  VimojoStorePresenter provideVimojoStorePresenter(BillingManager billingManager) {
    return new VimojoStorePresenter((VimojoStoreView) activity, activity, billingManager);
  }

  @Provides @PerActivity
  LicenseDetailPresenter provideLicenseDetailPresenter(GetLicenseVimojoListUseCase
                                                       getLicenseVimojoListUseCase) {
    return  new LicenseDetailPresenter((LicenseDetailView) activity, activity, getLicenseVimojoListUseCase);
  }

  @Provides @PerActivity
  DuplicatePreviewPresenter provideDuplicatePresenter
          (UserEventTracker userEventTracker, AddVideoToProjectUseCase addVideoToProjectUseCase) {
    return new DuplicatePreviewPresenter((VideoDuplicateActivity) activity, userEventTracker,
            addVideoToProjectUseCase);
  }

  @Provides @PerActivity
  GalleryPagerPresenter provideGalleryPagerPresenter(
          AddVideoToProjectUseCase addVideoToProjectUseCase,
          GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
          ApplyAVTransitionsUseCase applyAVTransitionsUseCase,
          ProjectRepository projectRepository,
          SharedPreferences sharedPreferences, VideoRepository videoRepository) {
    return new GalleryPagerPresenter((GalleryActivity) activity, activity, addVideoToProjectUseCase,
            getVideonaFormatFromCurrentProjectUseCase, applyAVTransitionsUseCase,
            projectRepository, videoRepository, sharedPreferences);
  }

 /* @Provides @PerActivity
  RecordPresenter provideRecordPresenter(
          UserEventTracker userEventTracker, SharedPreferences sharedPreferences,
          AddVideoToProjectUseCase addVideoToProjectUseCase,
          ApplyAVTransitionsUseCase applyAVTransitionsUseCase,
          GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
          VideoRepository videoRepository) {
    return new RecordPresenter(activity, (RecordActivity) activity, userEventTracker, cameraView,
        sharedPreferences, externalIntent, addVideoToProjectUseCase, videoRepository,
            applyAVTransitionsUseCase, getVideonaFormatFromCurrentProjectUseCase);
  }*/

  @Provides @PerActivity
  RecordCamera2Presenter provideRecordCamera2Presenter(
          UserEventTracker userEventTracker, SharedPreferences sharedPreferences,
          AddVideoToProjectUseCase addVideoToProjectUseCase, Camera2Wrapper camera2wrapper,
          NewClipImporter newClipImporter, CameraSettingsRepository cameraSettingsRepository) {
    return new RecordCamera2Presenter(activity, (RecordCamera2Activity) activity, userEventTracker,
            sharedPreferences, addVideoToProjectUseCase, newClipImporter, camera2wrapper,
            cameraSettingsRepository);
  }

  @Provides @PerActivity
  SplitPreviewPresenter provideSplitPresenter(
          UserEventTracker userEventTracker, SplitVideoUseCase splitVideoUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          VideoRepository videoRepository) {
    return new SplitPreviewPresenter((VideoSplitActivity) activity, userEventTracker, activity,
            videoRepository, splitVideoUseCase, getMediaListFromProjectUseCase);
  }

  @Provides @PerActivity
  TrimPreviewPresenter provideTrimPresenter(SharedPreferences sharedPreferences,
                                            UserEventTracker userEventTracker,
                                    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                    ModifyVideoDurationUseCase modifyVideoDurationUseCase) {
    return new TrimPreviewPresenter((VideoTrimActivity) activity, sharedPreferences,
        userEventTracker, getMediaListFromProjectUseCase, modifyVideoDurationUseCase);
  }

  @Provides @PerActivity
  ShareVideoPresenter provideVideoSharePresenter(UserEventTracker userEventTracker,
                                                 SharedPreferences sharedPreferences,
                                                 CreateDefaultProjectUseCase createDefaultProjectUseCase,
                                                 AddLastVideoExportedToProjectUseCase
                                     addLastVideoExportedProjectUseCase,
                                                 ExportProjectUseCase exportProjectUseCase,
                                                 ObtainNetworksToShareUseCase obtainNetworksToShareUseCase,
                                                 GetFtpListUseCase getFtpListUseCase,
                                                 GetAuthToken getAuthToken,
                                                 UploadToPlatformQueue uploadToPlatformQueue,
                                                 LoggedValidator loggedValidator,
                                                 RunSyncAdapterHelper runSyncAdapterHelper) {
    return new ShareVideoPresenter(activity, (ShareActivity) activity, userEventTracker,
            sharedPreferences, createDefaultProjectUseCase, addLastVideoExportedProjectUseCase,
            exportProjectUseCase, obtainNetworksToShareUseCase, getFtpListUseCase,
            getAuthToken, uploadToPlatformQueue, loggedValidator, runSyncAdapterHelper);
  }

  @Provides @PerActivity
  InitAppPresenter provideInitAppPresenter(SharedPreferences sharedPreferences,
          CreateDefaultProjectUseCase createDefaultProjectUseCase, CameraSettingsRepository
          cameraSettingsRepository, RunSyncAdapterHelper runSyncAdapterHelper) {
    return new InitAppPresenter(activity, sharedPreferences, createDefaultProjectUseCase,
        cameraSettingsRepository, runSyncAdapterHelper);
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
          ProjectRepository projectRepository,
          NewClipImporter newClipImporter, BillingManager billingManager) {
    return new EditorPresenter((EditorActivity) activity, (EditorActivity) activity,
        sharedPreferences, activity, userEventTracker, createDefaultProjectUseCase,
        getMediaListFromProjectUseCase, removeVideoFromProjectUseCase, getAudioFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase, relaunchTranscoderTempBackgroundUseCase,
        projectRepository, newClipImporter, billingManager);
  }

  @Provides @PerActivity
  GalleryProjectListPresenter provideGalleryProjectListPresenter(
      ProjectRepository projectRepository,
      SharedPreferences sharedPreferences,
      CreateDefaultProjectUseCase createDefaultProjectUseCase,
      DuplicateProjectUseCase duplicateProjectUseCase,
      DeleteProjectUseCase deleteProjectUseCase,
      CheckIfProjectHasBeenExportedUseCase checkIfProjectHasBeenExportedUseCase) {
    return new GalleryProjectListPresenter((GalleryProjectListActivity) activity, sharedPreferences,
        projectRepository, createDefaultProjectUseCase, duplicateProjectUseCase,
        deleteProjectUseCase, checkIfProjectHasBeenExportedUseCase);
  }

  @Provides @PerActivity
  DetailProjectPresenter provideDetailProjectPresenter(ProjectRepository projectRepository){
    return new DetailProjectPresenter(activity, (DetailProjectActivity) activity,
        projectRepository);
  }

  @Provides @PerActivity
  EditTextPreviewPresenter provideEditTextPreviewPresenter(
              UserEventTracker userEventTracker,
              GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
              ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase) {
    return new EditTextPreviewPresenter((VideoEditTextActivity) activity, activity,
        userEventTracker, getMediaListFromProjectUseCase, modifyVideoTextAndPositionUseCase);
  }
  @Provides @PerActivity
  UserProfilePresenter provideUserProfilePresenter(
          SharedPreferences sharedPreferences, ObtainLocalVideosUseCase obtainLocalVideosUseCase,
          GetAuthToken getAuthToken, AuthApiClient authApiClient, UserApiClient userApiClient) {
    return new  UserProfilePresenter(activity, (UserProfileView) activity, sharedPreferences,
        obtainLocalVideosUseCase, getAuthToken, userApiClient);
  }

  @Provides @PerActivity
  UserAuthPresenter provideUserAuthPresenter(AuthApiClient authApiClient,
                                             EmailPatternValidator emailPatternValidator) {
    return new UserAuthPresenter((UserAuthView) activity, activity,
            authApiClient, emailPatternValidator);
  }

  @Provides
  ReorderMediaItemUseCase provideMusicReorderer(ProjectRepository projectRepository) {
    return new ReorderMediaItemUseCase(projectRepository);
  }

  @Provides
  AddVideoToProjectUseCase provideVideoAdder(
          ProjectRepository projectRepository,
          ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase) {
    return new AddVideoToProjectUseCase(projectRepository, launchTranscoderAddAVTransitionUseCase);
  }

  @Provides
  SplitVideoUseCase provideVideoSplitter(AddVideoToProjectUseCase addVideoToProjectUseCase,
                                         ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                                         VideoRepository videoRepository) {
    return new SplitVideoUseCase(addVideoToProjectUseCase, modifyVideoDurationUseCase,
            videoRepository);
  }

  @Provides
  CreateDefaultProjectUseCase provideDefaultProjectCreator(
          ProjectRepository projectRepository, ProfileRepository profileRepository) {
    return new CreateDefaultProjectUseCase(projectRepository, profileRepository
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
  GetCameraSettingsMapperSupportedListUseCase provideCameraSettingsListUseCase(CameraSettingsRepository
                                                                   cameraSettingsRepository) {
    return new GetCameraSettingsMapperSupportedListUseCase(activity, cameraSettingsRepository);
  }

  @Provides
  GetCameraSettingsUseCase provideCameraSettingsUseCase(CameraSettingsRepository
                                                        cameraSettingsRepository) {
    return new GetCameraSettingsUseCase(cameraSettingsRepository);
  }

  @Provides GetMediaListFromProjectUseCase provideMediaListRetriever() {
    return new GetMediaListFromProjectUseCase();
  }

  @Provides AddLastVideoExportedToProjectUseCase provideLastVideoExporterAdded(
          ProjectRepository projectRepository) {
    return new AddLastVideoExportedToProjectUseCase(projectRepository);
  }

  @Provides
  UpdateWatermarkPreferenceToProjectUseCase provideUpdateWatermarkProject(ProjectRepository projectRepository) {
    return new UpdateWatermarkPreferenceToProjectUseCase(projectRepository);
  }

  @Provides DuplicateProjectUseCase provideDuplicateProject(ProjectRepository projectRepository) {
    return new DuplicateProjectUseCase(projectRepository);
  }

  @Provides DeleteProjectUseCase provideDeleteProject(ProjectRepository projectRepository,
                                                      VideoRepository videoRepository,
                                                      MusicRepository musicRepository,
                                                      TrackRepository trackRepository) {
    return new DeleteProjectUseCase(projectRepository, videoRepository, musicRepository,
        trackRepository);
  }

  @Provides CheckIfProjectHasBeenExportedUseCase provideCheckIfProjectHasBeenExported() {
    return new CheckIfProjectHasBeenExportedUseCase();
  }

  @Provides ModifyVideoDurationUseCase provideModifyVideoDurationUseCase(
          VideoRepository videoRepository, VideoToAdaptRepository videoToAdaptRepository) {
    return new ModifyVideoDurationUseCase(videoRepository, videoToAdaptRepository);
  }

  @Provides ModifyVideoTextAndPositionUseCase provideModifyVideoTextAndPositionUseCase(
          VideoRepository videoRepository,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          VideoToAdaptRepository videoToAdaptRepository) {
    return new ModifyVideoTextAndPositionUseCase(videoRepository,
            relaunchTranscoderTempBackgroundUseCase, videoToAdaptRepository);
  }

  @Provides
  ApplyAVTransitionsUseCase provideLaunchTranscoderAddAVTransition(
      VideoRepository videoRepository){
   return  new ApplyAVTransitionsUseCase(videoRepository);
  }

  @Provides GetVideoFormatFromCurrentProjectUseCase provideVideoFormatFromCurrentProjectUseCase() {
    return new GetVideoFormatFromCurrentProjectUseCase();
  }

  @Provides
  AdaptVideoToFormatUseCase provideAdaptVideoRecordedToVideoFormatUseCase(
          VideoToAdaptRepository videoToAdaptRepository, VideoRepository videoRepository) {
    return new AdaptVideoToFormatUseCase(videoToAdaptRepository, videoRepository);
  }

  @Provides RelaunchTranscoderTempBackgroundUseCase provideRelaunchTranscoderTempBackgroundUseCase(
          VideoRepository videoRepository) {
    return new RelaunchTranscoderTempBackgroundUseCase(videoRepository);
  }

  @Provides ExportProjectUseCase provideProjectExporter(
          VideoToAdaptRepository videoToAdaptRepository) {
    return new ExportProjectUseCase(videoToAdaptRepository);
  }

  @Provides ModifyTrackUseCase providesModifyTrackUseCase(ProjectRepository projectRepository) {
    return new ModifyTrackUseCase(projectRepository);
  }

  @Provides VideoListErrorCheckerDelegate providesVideoListErrorCheckerDelegate() {
    return new VideoListErrorCheckerDelegate();
  }

  @Provides
  AddAudioUseCase providesAddAudioUseCase(ProjectRepository projectRepository) {
    return new AddAudioUseCase(projectRepository);
  }

  @Provides
  RemoveAudioUseCase providesRemoveAudioUseCase(ProjectRepository projectRepository, TrackRepository
      trackRepository, MusicRepository musicRepository){
    return new RemoveAudioUseCase(projectRepository, trackRepository, musicRepository);
  }

  @Provides
  Camera2Wrapper provideCamera2wrapper(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          GetCameraSettingsUseCase getCameraSettingsUseCase) {
    return new Camera2Wrapper(activity, getCameraSettingsUseCase.getCameraIdSelected(), textureView,
            directorySaveVideos,
            getVideoFormatFromCurrentProjectUseCase
                    .getVideoRecordedFormatFromCurrentProjectUseCase(), freeStorage);
  }

  @Provides VimojoLicensesProvider provideLicenseProvider() {
    return new VimojoLicensesProvider(activity);
  }

  @Provides
  NewClipImporter provideClipImporter(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoToFormatUseCase adaptVideoToFormatUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          VideoRepository videoRepository, VideoToAdaptRepository videoToAdaptRepository,
          ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase) {
    return new NewClipImporter(getVideoFormatFromCurrentProjectUseCase,
            adaptVideoToFormatUseCase, launchTranscoderAddAVTransitionUseCase,
            relaunchTranscoderTempBackgroundUseCase, videoRepository, videoToAdaptRepository);
  }

  @Provides BillingManager provideBillingManager() {
    return new BillingManager();
  }

  @Provides ProfileRepository provideProfileRepository(
          CameraSettingsRepository cameraSettingsRepository) {
    return new ProfileRepositoryFromCameraSettings(cameraSettingsRepository);
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
  UploadToPlatformQueue provideUploadToPlatform() {
      return new UploadToPlatformQueue(activity);
  }

  @Provides
  RunSyncAdapterHelper provideRunSyncAdapterHelper() {
    return new RunSyncAdapterHelper(activity);
  }
}
