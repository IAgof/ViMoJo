package com.videonasocialmedia.vimojo.main.modules;

import android.content.SharedPreferences;

import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.domain.editor.UpdateVideoResolutionToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateTitleProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.DetailProjectPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.GalleryProjectListPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoListErrorCheckerDelegate;
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
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToVideoFormatUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.MergeVoiceOverAudiosUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateAudioTrackProjectUseCase;
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
  private boolean isRightControlsViewSelected;
  private boolean isPrincipalViewSelected;
  private boolean isFrontCameraSelected;
  private AutoFitTextureView textureView;
  private GLCameraView cameraView = null;
  private boolean externalIntent;
  private String directorySaveVideos;

  public ActivityPresentersModule(VimojoActivity vimojoActivity) {
    this.activity = vimojoActivity;
  }

  public ActivityPresentersModule(RecordActivity activity, boolean externalIntent,
                                  GLCameraView cameraView) {
    this.activity = activity;
    this.externalIntent = externalIntent;
    this.cameraView = cameraView;
  }

  public ActivityPresentersModule(RecordCamera2Activity activity, boolean isFrontCameraSelected,
                                  boolean isPrincipalViewSelected,
                                  boolean isRightControlsViewSelected,
                                  String directorySaveVideos,
                                  AutoFitTextureView textureView) {
    this.activity = activity;
    this.isFrontCameraSelected = isFrontCameraSelected;
    this.isPrincipalViewSelected = isPrincipalViewSelected;
    this.isRightControlsViewSelected = isRightControlsViewSelected;
    this.textureView = textureView;
    this.directorySaveVideos = directorySaveVideos;
  }

  @Provides @PerActivity
  SoundVolumePresenter getSoundVolumePresenter(GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                     GetPreferencesTransitionFromProjectUseCase
                                         getPreferencesTransitionFromProjectUseCase,
                                     AddAudioUseCase addAudioUseCase) {
    return new SoundVolumePresenter((SoundVolumeView) activity, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase, addAudioUseCase);
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
                              GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                              GetMusicFromProjectUseCase getMusicFromProjectUseCase,
                              GetPreferencesTransitionFromProjectUseCase
                                      getPreferencesTransitionFromProjectUseCase,
                              AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
                              ModifyTrackUseCase modifyTrackUseCase) {
    return new MusicDetailPresenter((MusicDetailView) activity, userEventTracker,
            getMediaListFromProjectUseCase, getMusicFromProjectUseCase,
            getPreferencesTransitionFromProjectUseCase, addAudioUseCase, removeAudioUseCase,
            modifyTrackUseCase, activity);
  }

  @Provides @PerActivity
  EditPresenter provideEditPresenter(UserEventTracker userEventTracker,
                                     RemoveVideoFromProjectUseCase removeVideosFromProjectUseCase,
                                     ReorderMediaItemUseCase reorderMediaItemUseCase,
                                     GetMusicFromProjectUseCase getMusicFromProjectUseCase,
                                     GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                     GetPreferencesTransitionFromProjectUseCase
                                     getPreferencesTransitionFromProjectUseCase) {
    return new EditPresenter((EditActivity) activity, (EditActivity) activity,
            userEventTracker, removeVideosFromProjectUseCase, reorderMediaItemUseCase,
            getMusicFromProjectUseCase, getMediaListFromProjectUseCase,
            getPreferencesTransitionFromProjectUseCase);
  }

  @Provides @PerActivity
  SoundPresenter provideSoundPresenter(GetMediaListFromProjectUseCase
                                           getMediaListFromProjectUseCase,
                                       GetMusicFromProjectUseCase getMusicFromProjectUseCase,
                                       GetPreferencesTransitionFromProjectUseCase
                                            getPreferencesTransitionFromProjectUseCase,
                                       ModifyTrackUseCase modifyTrackUseCase,
                                       VideoListErrorCheckerDelegate
                                           videoListErrorCheckerDelegate) {
    return new SoundPresenter((SoundActivity) activity, getMediaListFromProjectUseCase,
        getMusicFromProjectUseCase, getPreferencesTransitionFromProjectUseCase, modifyTrackUseCase,
        videoListErrorCheckerDelegate);
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
          AddVideoToProjectUseCase addVideoToProjectUseCase,
          UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase,
          GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
          LaunchTranscoderAddAVTransitionsUseCase launchTranscoderAddAVTransitionsUseCase,
          UpdateVideoResolutionToProjectUseCase updateVideoResolutionToProjectUseCase,
          SharedPreferences sharedPreferences) {
    return new GalleryPagerPresenter((GalleryActivity) activity, activity, addVideoToProjectUseCase,
            updateVideoRepositoryUseCase, getVideonaFormatFromCurrentProjectUseCase,
            launchTranscoderAddAVTransitionsUseCase, updateVideoResolutionToProjectUseCase,
            sharedPreferences);
  }

  @Provides @PerActivity
  RecordPresenter provideRecordPresenter(UserEventTracker userEventTracker,
                                         SharedPreferences sharedPreferences,
                                         AddVideoToProjectUseCase addVideoToProjectUseCase,
                                         UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase,
                                         LaunchTranscoderAddAVTransitionsUseCase
                                              launchTranscoderAddAVTransitionsUseCase,
                                         GetVideoFormatFromCurrentProjectUseCase
                                             getVideonaFormatFromCurrentProjectUseCase) {
    return new RecordPresenter(activity, (RecordActivity) activity, userEventTracker, cameraView,
        sharedPreferences, externalIntent, addVideoToProjectUseCase, updateVideoRepositoryUseCase,
        launchTranscoderAddAVTransitionsUseCase, getVideonaFormatFromCurrentProjectUseCase);
  }

  @Provides @PerActivity
  RecordCamera2Presenter provideRecordCamera2Presenter(
                                                       UpdateVideoRepositoryUseCase
                                                           updateVideoRepositoryUseCase,
                                                       LaunchTranscoderAddAVTransitionsUseCase
                                                            launchTranscoderAddAVTransitionsUseCase,
                                                       GetVideoFormatFromCurrentProjectUseCase
                                                           getVideoFormatFromCurrentProjectUseCase,
                                                       AddVideoToProjectUseCase
                                                           addVideoToProjectUseCase,
                                                       AdaptVideoRecordedToVideoFormatUseCase
                                                       adaptVideoRecordedToVideoFormatUseCase){

    return new RecordCamera2Presenter(activity, (RecordCamera2Activity) activity,
        isFrontCameraSelected, isPrincipalViewSelected, isRightControlsViewSelected, textureView,
        directorySaveVideos, updateVideoRepositoryUseCase, launchTranscoderAddAVTransitionsUseCase,
        getVideoFormatFromCurrentProjectUseCase, addVideoToProjectUseCase,
        adaptVideoRecordedToVideoFormatUseCase);
  }

  @Provides @PerActivity
  SplitPreviewPresenter provideSplitPresenter(UserEventTracker userEventTracker,
                                      SplitVideoUseCase splitVideoUseCase,
                                      GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                      ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                                          UpdateVideoRepositoryUseCase
                                              updateVideoRepositoryUseCase) {
    return new SplitPreviewPresenter((VideoSplitActivity) activity, userEventTracker, activity,
            splitVideoUseCase, getMediaListFromProjectUseCase, modifyVideoDurationUseCase,
            updateVideoRepositoryUseCase);
  }

  @Provides @PerActivity
  TrimPreviewPresenter provideTrimPresenter(UserEventTracker userEventTracker,
                                    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                    ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                                    UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase){
    return new TrimPreviewPresenter((VideoTrimActivity) activity, userEventTracker,
        getMediaListFromProjectUseCase, modifyVideoDurationUseCase,
        updateVideoRepositoryUseCase);
  }

  @Provides @PerActivity
  ShareVideoPresenter
  provideVideoSharePresenter(UserEventTracker userEventTracker,
                             SharedPreferences sharedPreferences,
                             CreateDefaultProjectUseCase createDefaultProjectUseCase,
                             AddLastVideoExportedToProjectUseCase
                                     addLastVideoExportedProjectUseCase,
                             ExportProjectUseCase exportProjectUseCase) {
    return new ShareVideoPresenter((ShareActivity) activity, userEventTracker, sharedPreferences,
            activity, createDefaultProjectUseCase, addLastVideoExportedProjectUseCase,
            exportProjectUseCase);
  }

  @Provides @PerActivity
  InitAppPresenter provideInitAppPresenter(CreateDefaultProjectUseCase createDefaultProjectUseCase){
    return new InitAppPresenter((InitAppActivity) activity, createDefaultProjectUseCase);
  }

  @Provides @PerActivity
  EditorPresenter provideEditorPresenter(SharedPreferences sharedPreferences,
                                         CreateDefaultProjectUseCase createDefaultProjectUseCase,
                                         GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                         GetVideoFormatFromCurrentProjectUseCase
                                             getVideonaFormatFromCurrentProjectUseCase,
                                         RelaunchTranscoderTempBackgroundUseCase
                                             relaunchTranscoderTempBackgroundUseCase,
                                         UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase) {
    return new EditorPresenter((EditorActivity) activity, sharedPreferences, activity,
        createDefaultProjectUseCase, getMediaListFromProjectUseCase,
        getVideonaFormatFromCurrentProjectUseCase, relaunchTranscoderTempBackgroundUseCase,
        updateVideoRepositoryUseCase);
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
              GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
              UpdateVideoRepositoryUseCase
                  updateVideoRepositoryUseCase,
              RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase) {
    return new EditTextPreviewPresenter((VideoEditTextActivity) activity, activity,
        userEventTracker, getMediaListFromProjectUseCase, modifyVideoTextAndPositionUseCase,
        getVideonaFormatFromCurrentProjectUseCase, updateVideoRepositoryUseCase,
        relaunchTranscoderTempBackgroundUseCase);
  }

  @Provides
  RemoveMusicFromProjectUseCase provideMusicRemover(MusicRepository musicRepository) {
    return new RemoveMusicFromProjectUseCase(musicRepository);
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
  AddVoiceOverToProjectUseCase  provideVoiceOverSetter(AddMusicToProjectUseCase addMusicToProjectUseCase) {
    return new AddVoiceOverToProjectUseCase(addMusicToProjectUseCase);
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

  @Provides UpdateVideoRepositoryUseCase provideUpdateVideoRepositoryUseCase(VideoRepository
                                                                             videoRepository){
    return new UpdateVideoRepositoryUseCase(videoRepository);
  }

  @Provides UpdateMusicVolumeProjectUseCase provideUpdateMusicVolumeProject(
          ProjectRepository projectRepository) {
    return new UpdateMusicVolumeProjectUseCase(projectRepository);
  }

  @Provides LaunchTranscoderAddAVTransitionsUseCase provideLaunchTranscoderAddAVTransition(
      VideoRepository videoRepository){
   return  new LaunchTranscoderAddAVTransitionsUseCase(videoRepository);
  }

  @Provides AddMusicToProjectUseCase providesAddMusicToProject(MusicRepository musicRepository){
    return new AddMusicToProjectUseCase(musicRepository);
  }

  @Provides GetVideoFormatFromCurrentProjectUseCase provideVideoFormatFromCurrentProjectUseCase(){
    return new GetVideoFormatFromCurrentProjectUseCase();
  }

  @Provides AdaptVideoRecordedToVideoFormatUseCase provideAdaptVideoRecordedToVideoFormatUseCase(){
    return new AdaptVideoRecordedToVideoFormatUseCase();
  }

  @Provides RelaunchTranscoderTempBackgroundUseCase provideRelaunchTranscoderTempBackgroundUseCase(){
    return new RelaunchTranscoderTempBackgroundUseCase();
  }

  @Provides ExportProjectUseCase provideProjectExporter() {
    return new ExportProjectUseCase();
  }

  @Provides ModifyTrackUseCase providesModifyTrackUseCase(ProjectRepository projectRepository,
                                                          TrackRepository trackRepository){
    return new ModifyTrackUseCase(projectRepository, trackRepository);
  }

  @Provides VideoListErrorCheckerDelegate providesVideoListErrorCheckerDelegate(){
    return new VideoListErrorCheckerDelegate();
  }

  @Provides UpdateAudioTrackProjectUseCase providesUpdateAudioTrackProjectUseCase(TrackRepository
                                                                                  trackRepository){
    return new UpdateAudioTrackProjectUseCase(trackRepository);
  }

  @Provides
  AddAudioUseCase providesAddAudioUseCase(ProjectRepository projectRepository, TrackRepository
                                          trackRepository, MusicRepository musicRepository){
    return new AddAudioUseCase(projectRepository, trackRepository, musicRepository);
  }

  @Provides
  RemoveAudioUseCase providesRemoveAudioUseCase(ProjectRepository projectRepository, TrackRepository
      trackRepository, MusicRepository musicRepository){
    return new RemoveAudioUseCase(projectRepository, trackRepository, musicRepository);
  }
}
