package com.videonasocialmedia.vimojo.main.modules;

import android.content.SharedPreferences;

import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateTitleProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.DetailProjectPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.GalleryProjectListPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.internals.di.PerActivity;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.DuplicatePreviewPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.InitAppPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.InitAppActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VideoDuplicateActivity;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.MergeVoiceOverAudiosUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateMusicVolumeProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundVolumePresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.VoiceOverPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.VoiceOverActivity;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.views.activity.VideoSplitActivity;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.presentation.mvp.presenters.EditTextPreviewPresenter;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 1/12/16.
 */

@Module
public class ActivityPresentersModule {
  private final VimojoActivity activity;
  private GLCameraView cameraView = null;
  private boolean externalIntent;

  public ActivityPresentersModule(VimojoActivity vimojoActivity) {
    this.activity = vimojoActivity;
  }

  public ActivityPresentersModule(RecordActivity activity, boolean externalIntent,
                                  GLCameraView cameraView) {
    this.activity = activity;
    this.externalIntent = externalIntent;
    this.cameraView = cameraView;
  }

  @Provides @PerActivity
  SoundVolumePresenter getSoundVolumePresenter(RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase,
                                     AddVoiceOverToProjectUseCase addVoiceOverToProjectUseCase,
                                     GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                     GetPreferencesTransitionFromProjectUseCase
                                         getPreferencesTransitionFromProjectUseCase) {
    return new SoundVolumePresenter((SoundVolumeView) activity, removeMusicFromProjectUseCase,
        addVoiceOverToProjectUseCase, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase);
  }

  @Provides @PerActivity
  VoiceOverPresenter voiceOverPresenter(GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                        GetPreferencesTransitionFromProjectUseCase
                                             getPreferencesTransitionFromProjectUseCase,
                                        MergeVoiceOverAudiosUseCase mergeVoiceOverAudiosUseCase) {
    return new VoiceOverPresenter((VoiceOverActivity) activity, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase, mergeVoiceOverAudiosUseCase);
  }

  @Provides @PerActivity
  MusicDetailPresenter
  provideMusicDetailPresenter(UserEventTracker userEventTracker,
                              AddMusicToProjectUseCase addMusicToProjectUseCase,
                              RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase,
                              GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                              GetMusicFromProjectUseCase getMusicFromProjectUseCase,
                              GetPreferencesTransitionFromProjectUseCase
                                      getPreferencesTransitionFromProjectUseCase,
                              UpdateMusicVolumeProjectUseCase updateMusicVolumeProjectUseCase) {
    return new MusicDetailPresenter((MusicDetailView) activity, userEventTracker,
            addMusicToProjectUseCase, removeMusicFromProjectUseCase, getMediaListFromProjectUseCase,
            getMusicFromProjectUseCase, getPreferencesTransitionFromProjectUseCase,
            updateMusicVolumeProjectUseCase, activity);
  }

  @Provides @PerActivity
  EditPresenter provideEditPresenter(UserEventTracker userEventTracker,
                                     RemoveVideoFromProjectUseCase removeVideosFromProjectUseCase,
                                     ReorderMediaItemUseCase reorderMediaItemUseCase,
                                     GetMusicFromProjectUseCase getMusicFromProjectUseCase,
                                     GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                     GetPreferencesTransitionFromProjectUseCase
                                     getPreferencesTransitionFromProjectUseCase) {
    return new EditPresenter((EditActivity) activity, userEventTracker,
        removeVideosFromProjectUseCase, reorderMediaItemUseCase, getMusicFromProjectUseCase,
        getMediaListFromProjectUseCase, getPreferencesTransitionFromProjectUseCase);
  }

  @Provides @PerActivity
  SoundPresenter provideSoundPresenter(GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                       GetPreferencesTransitionFromProjectUseCase
                                            getPreferencesTransitionFromProjectUseCase) {
    return new SoundPresenter((SoundActivity) activity, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase);
  }

  @Provides @PerActivity
  MusicListPresenter provideMusicListPresenter(GetMusicListUseCase getMusicListUseCase,
                                               GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                               GetMusicFromProjectUseCase getMusicFromProjectUseCase,
                                               GetPreferencesTransitionFromProjectUseCase
                                                   getPreferencesTransitionFromProjectUseCase) {
    return new MusicListPresenter((MusicListView) activity, activity, getMusicListUseCase,
        getMediaListFromProjectUseCase, getMusicFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase);
  }

  @Provides @PerActivity
  DuplicatePreviewPresenter provideDuplicatePresenter
          (UserEventTracker userEventTracker, AddVideoToProjectUseCase addVideoToProjectUseCase) {
    return new DuplicatePreviewPresenter((VideoDuplicateActivity) activity, userEventTracker,
            addVideoToProjectUseCase);
  }

  @Provides @PerActivity
  GalleryPagerPresenter provideGalleryPagerPresenter(
          AddVideoToProjectUseCase addVideoToProjectUseCase) {
    return new GalleryPagerPresenter((GalleryActivity) activity, addVideoToProjectUseCase);
  }

  @Provides @PerActivity
  RecordPresenter provideRecordPresenter(SharedPreferences sharedPreferences,
                                         AddVideoToProjectUseCase addVideoToProjectUseCase) {
    return new RecordPresenter(activity, (RecordActivity) activity, cameraView, sharedPreferences,
            externalIntent, addVideoToProjectUseCase);
  }

  @Provides @PerActivity
  SplitPreviewPresenter provideSplitPresenter(UserEventTracker userEventTracker,
                                      SplitVideoUseCase splitVideoUseCase,
                                      GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                      ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                                      GetVideonaFormatFromCurrentProjectUseCase
                                      getVideonaFormatFromCurrentProjectUseCase,
                                          UpdateVideoRepositoryUseCase
                                              updateVideoRepositoryUseCase) {
    return new SplitPreviewPresenter((VideoSplitActivity) activity, userEventTracker, activity,
            splitVideoUseCase, getMediaListFromProjectUseCase, modifyVideoDurationUseCase,
        getVideonaFormatFromCurrentProjectUseCase, updateVideoRepositoryUseCase);
  }

  @Provides @PerActivity
  TrimPreviewPresenter provideTrimPresenter(UserEventTracker userEventTracker,
                                    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                    ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                                    GetVideonaFormatFromCurrentProjectUseCase
                                        getVideonaFormatFromCurrentProjectUseCase,
                                    UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase){
    return new TrimPreviewPresenter((VideoTrimActivity) activity, userEventTracker,
        getMediaListFromProjectUseCase, modifyVideoDurationUseCase,
        getVideonaFormatFromCurrentProjectUseCase, updateVideoRepositoryUseCase);
  }

  @Provides @PerActivity
  ShareVideoPresenter
  provideVideoSharePresenter(UserEventTracker userEventTracker,
                             SharedPreferences sharedPreferences,
                             CreateDefaultProjectUseCase createDefaultProjectUseCase,
                             AddLastVideoExportedToProjectUseCase
                                 addLastVideoExportedProjectUseCase) {
    return new ShareVideoPresenter((ShareActivity) activity, userEventTracker, sharedPreferences,
            activity, createDefaultProjectUseCase, addLastVideoExportedProjectUseCase);
  }

  @Provides @PerActivity
  InitAppPresenter provideInitAppPresenter(CreateDefaultProjectUseCase createDefaultProjectUseCase){
    return new InitAppPresenter((InitAppActivity) activity, createDefaultProjectUseCase);
  }

  @Provides @PerActivity
  EditorPresenter provideEditorPresenter(SharedPreferences sharedPreferences,
                                         CreateDefaultProjectUseCase createDefaultProjectUseCase) {
    return new EditorPresenter((EditorActivity) activity, sharedPreferences, activity,
        createDefaultProjectUseCase);
  }

  @Provides @PerActivity
  GalleryProjectListPresenter provideGalleryProjectListPresenter(
      ProjectRepository projectRepository,
      CreateDefaultProjectUseCase createDefaultProjectUseCase,
      UpdateCurrentProjectUseCase updateCurrentProjectUseCase,
      DuplicateProjectUseCase duplicateProjectUseCase,
      DeleteProjectUseCase deleteProjectUseCase,
      CheckIfProjectHasBeenExportedUseCase checkIfProjectHasBeenExportedUseCase) {
    return new GalleryProjectListPresenter((GalleryProjectListActivity) activity, projectRepository,
        createDefaultProjectUseCase, updateCurrentProjectUseCase, duplicateProjectUseCase,
        deleteProjectUseCase, checkIfProjectHasBeenExportedUseCase);
  }

  @Provides @PerActivity
  DetailProjectPresenter provideDetailProjectPresenter(
      UpdateTitleProjectUseCase updateTitleProjectUseCase){
    return new DetailProjectPresenter((DetailProjectActivity) activity, updateTitleProjectUseCase);
  }

  @Provides @PerActivity
  EditTextPreviewPresenter provideEditTextPreviewPresenter(
              UserEventTracker userEventTracker,
              GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
              ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase,
              GetVideonaFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
              UpdateVideoRepositoryUseCase
                  updateVideoRepositoryUseCase) {
    return new EditTextPreviewPresenter((VideoEditTextActivity) activity, activity,
        userEventTracker, getMediaListFromProjectUseCase, modifyVideoTextAndPositionUseCase,
        getVideonaFormatFromCurrentProjectUseCase, updateVideoRepositoryUseCase);
  }

  @Provides
  RemoveMusicFromProjectUseCase provideMusicRemover(ProjectRepository projectRepository) {
    return new RemoveMusicFromProjectUseCase(projectRepository);
  }

  @Provides
  ReorderMediaItemUseCase provideMusicReorderer(ProjectRepository projectRepository) {
    return new ReorderMediaItemUseCase(projectRepository);
  }

  @Provides
  AddVideoToProjectUseCase provideVideoAdder(ProjectRepository projectRepository) {
    return new AddVideoToProjectUseCase(projectRepository);
  }

  @Provides
  SplitVideoUseCase provideVideoSplitter(AddVideoToProjectUseCase addVideoToProjectUseCase) {
    return new SplitVideoUseCase(addVideoToProjectUseCase);
  }

  @Provides
  CreateDefaultProjectUseCase provideDefaultProjectCreator(ProjectRepository projectRepository,
                                                            ProfileRepository profileRepository) {
    return new CreateDefaultProjectUseCase(projectRepository, profileRepository);
  }

  @Provides
  AddVoiceOverToProjectUseCase
  provideVoiceOverSetter(ProjectRepository projectRepository,
                         AddMusicToProjectUseCase addMusicToProjectUseCase,
                         RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase) {
    return new AddVoiceOverToProjectUseCase(projectRepository, addMusicToProjectUseCase,
        removeMusicFromProjectUseCase);
  }

  @Provides
  GetMusicFromProjectUseCase provideMusicRetriever() {
    return new GetMusicFromProjectUseCase();
  }

  @Provides GetMusicListUseCase provideMusicListUseCase() {
    return new GetMusicListUseCase(activity);
  }

  @Provides GetMediaListFromProjectUseCase provideMediaListRetriever() {
    return new GetMediaListFromProjectUseCase();
  }

  @Provides AddLastVideoExportedToProjectUseCase provideLastVideoExporterAdded(
          ProjectRepository projectRepository) {
    return new AddLastVideoExportedToProjectUseCase(projectRepository);
  }

  @Provides
  UpdateTitleProjectUseCase provideUpdateTitleProject(ProjectRepository projectRepository) {
    return new UpdateTitleProjectUseCase(projectRepository);
  }

  @Provides
  UpdateCurrentProjectUseCase provideUpdateCurrentProject(ProjectRepository projectRepository) {
    return new UpdateCurrentProjectUseCase(projectRepository);
  }

  @Provides DuplicateProjectUseCase provideDuplicateProject(ProjectRepository projectRepository) {
    return new DuplicateProjectUseCase(projectRepository);
  }

  @Provides DeleteProjectUseCase provideDeleteProject(ProjectRepository projectRepository,
                                                      VideoRepository videoRepository) {
    return new DeleteProjectUseCase(projectRepository, videoRepository);
  }

  @Provides CheckIfProjectHasBeenExportedUseCase provideCheckIfProjectHasBeenExported() {
    return new CheckIfProjectHasBeenExportedUseCase();
  }

  @Provides ModifyVideoDurationUseCase provideModifyVideoDurationUseCase(VideoRepository
                                                                             videoRepository){
    return new ModifyVideoDurationUseCase(videoRepository);
  }

  @Provides ModifyVideoTextAndPositionUseCase provideModifyVideoTextAndPositionUseCase(
      VideoRepository videoRepository) {
    return new ModifyVideoTextAndPositionUseCase(videoRepository);
  }

  @Provides GetVideonaFormatFromCurrentProjectUseCase
                                              provideGetVideonaFormatFromCurrentProjectUseCase(){
    return new GetVideonaFormatFromCurrentProjectUseCase();
  }

  @Provides UpdateVideoRepositoryUseCase provideUpdateVideoRepositoryUseCase(VideoRepository
                                                                             videoRepository){
    return new UpdateVideoRepositoryUseCase(videoRepository);
  }

  @Provides UpdateMusicVolumeProjectUseCase provideUpdateMusicVolumeProject(
          ProjectRepository projectRepository) {
    return new UpdateMusicVolumeProjectUseCase(projectRepository);
  }

}
