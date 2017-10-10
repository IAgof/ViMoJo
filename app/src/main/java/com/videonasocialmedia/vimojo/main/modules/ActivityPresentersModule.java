package com.videonasocialmedia.vimojo.main.modules;

import android.app.Activity;
import android.content.SharedPreferences;

import com.videonasocialmedia.avrecorder.AudioRecorder;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.domain.editor.UpdateVideoResolutionToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
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
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VideoDuplicateActivity;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.MergeVoiceOverAudiosUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicDetailPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicListPresenter;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.NewVoiceOverPresenter;
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

import java.io.IOException;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 1/12/16.
 */

@Module
public class ActivityPresentersModule {
  private final VimojoActivity activity;
  private String destinationFolderPath = ".project/102893438/tempMixedAudio/voiceOverRecord";
  private int numTracks = 1;
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

  public ActivityPresentersModule(RecordCamera2Activity activity,
                                  String directorySaveVideos,
                                  AutoFitTextureView textureView) {
    this.activity = activity;
    this.textureView = textureView;
    this.directorySaveVideos = directorySaveVideos;
  }

  public ActivityPresentersModule(VoiceOverActivity activity, String destinationFolderPath,
                                  int numTracks){
    this.activity = activity;
    this.destinationFolderPath = destinationFolderPath;
    this.numTracks = numTracks;
  }

  @Provides @PerActivity
  SoundVolumePresenter getSoundVolumePresenter(
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase, AddAudioUseCase addAudioUseCase,
          RemoveAudioUseCase removeAudioUseCase) {
    return new SoundVolumePresenter((SoundVolumeView) activity, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase, getAudioFromProjectUseCase, addAudioUseCase,
        removeAudioUseCase, activity);
  }

  @Provides @PerActivity
  VoiceOverPresenter voiceOverPresenter(
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          MergeVoiceOverAudiosUseCase mergeVoiceOverAudiosUseCase, SessionConfig sessionConfig,
          AudioRecorder audioRecorder) {
    return new VoiceOverPresenter((VoiceOverActivity) activity, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase, mergeVoiceOverAudiosUseCase,sessionConfig,
        audioRecorder);
  }

  @Provides @PerActivity
  NewVoiceOverPresenter provideNewVoiceOverPresenter(
      GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
      GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
      MergeVoiceOverAudiosUseCase mergeVoiceOverAudiosUseCase) {
    return new NewVoiceOverPresenter((VoiceOverActivity) activity, getMediaListFromProjectUseCase,
        getPreferencesTransitionFromProjectUseCase, mergeVoiceOverAudiosUseCase);
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
                                     RemoveVideoFromProjectUseCase removeVideosFromProjectUseCase,
                                     ReorderMediaItemUseCase reorderMediaItemUseCase,
                                     GetAudioFromProjectUseCase getAudioFromProjectUseCase,
                                     GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                     GetPreferencesTransitionFromProjectUseCase
                                     getPreferencesTransitionFromProjectUseCase) {
    return new EditPresenter((EditActivity) activity, (EditActivity) activity,
            userEventTracker, removeVideosFromProjectUseCase, reorderMediaItemUseCase,
        getAudioFromProjectUseCase, getMediaListFromProjectUseCase,
            getPreferencesTransitionFromProjectUseCase);
  }

  @Provides @PerActivity
  SoundPresenter provideSoundPresenter(GetMediaListFromProjectUseCase
                                           getMediaListFromProjectUseCase,
                                       GetAudioFromProjectUseCase getAudioFromProjectUseCase,
                                       GetPreferencesTransitionFromProjectUseCase
                                            getPreferencesTransitionFromProjectUseCase,
                                       ModifyTrackUseCase modifyTrackUseCase,
                                       VideoListErrorCheckerDelegate
                                           videoListErrorCheckerDelegate) {
    return new SoundPresenter((SoundActivity) activity, getMediaListFromProjectUseCase,
        getAudioFromProjectUseCase, getPreferencesTransitionFromProjectUseCase, modifyTrackUseCase,
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
          UpdateVideoResolutionToProjectUseCase updateVideoResolutionToProjectUseCase,
          SharedPreferences sharedPreferences, VideoRepository videoRepository) {
    return new GalleryPagerPresenter((GalleryActivity) activity, activity, addVideoToProjectUseCase,
            getVideonaFormatFromCurrentProjectUseCase, applyAVTransitionsUseCase,
            updateVideoResolutionToProjectUseCase, videoRepository, sharedPreferences);
  }

  @Provides @PerActivity
  RecordPresenter provideRecordPresenter(
          UserEventTracker userEventTracker, SharedPreferences sharedPreferences,
          AddVideoToProjectUseCase addVideoToProjectUseCase,
          ApplyAVTransitionsUseCase applyAVTransitionsUseCase,
          GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
          VideoRepository videoRepository) {
    return new RecordPresenter(activity, (RecordActivity) activity, userEventTracker, cameraView,
        sharedPreferences, externalIntent, addVideoToProjectUseCase, videoRepository,
            applyAVTransitionsUseCase, getVideonaFormatFromCurrentProjectUseCase);
  }

  @Provides @PerActivity
  RecordCamera2Presenter provideRecordCamera2Presenter(
          UserEventTracker userEventTracker, SharedPreferences sharedPreferences,
          AddVideoToProjectUseCase addVideoToProjectUseCase, Camera2Wrapper camera2wrapper,
          NewClipImporter newClipImporter) {
    return new RecordCamera2Presenter(activity, (RecordCamera2Activity) activity, userEventTracker,
            sharedPreferences, addVideoToProjectUseCase, newClipImporter, camera2wrapper);
  }

  @Provides @PerActivity
  SplitPreviewPresenter provideSplitPresenter(
          UserEventTracker userEventTracker, SplitVideoUseCase splitVideoUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          ModifyVideoDurationUseCase modifyVideoDurationUseCase, VideoRepository videoRepository) {
    return new SplitPreviewPresenter((VideoSplitActivity) activity, userEventTracker, activity,
            videoRepository, splitVideoUseCase, getMediaListFromProjectUseCase,
            modifyVideoDurationUseCase);
  }

  @Provides @PerActivity
  TrimPreviewPresenter provideTrimPresenter(UserEventTracker userEventTracker,
                                    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                    ModifyVideoDurationUseCase modifyVideoDurationUseCase) {
    return new TrimPreviewPresenter((VideoTrimActivity) activity, userEventTracker,
        getMediaListFromProjectUseCase, modifyVideoDurationUseCase);
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
  InitAppPresenter provideInitAppPresenter(
          CreateDefaultProjectUseCase createDefaultProjectUseCase) {
    return new InitAppPresenter(createDefaultProjectUseCase);
  }

  @Provides @PerActivity
  EditorPresenter provideEditorPresenter(
          UserEventTracker userEventTracker,
          SharedPreferences sharedPreferences,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          NewClipImporter newClipImporter) {
    return new EditorPresenter((EditorActivity) activity, sharedPreferences, activity,
            userEventTracker, createDefaultProjectUseCase, getMediaListFromProjectUseCase,
            relaunchTranscoderTempBackgroundUseCase, newClipImporter);
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
              ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase) {
    return new EditTextPreviewPresenter((VideoEditTextActivity) activity, activity,
        userEventTracker, getMediaListFromProjectUseCase, modifyVideoTextAndPositionUseCase);
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
  SplitVideoUseCase provideVideoSplitter(AddVideoToProjectUseCase addVideoToProjectUseCase) {
    return new SplitVideoUseCase(addVideoToProjectUseCase);
  }

  @Provides
  CreateDefaultProjectUseCase provideDefaultProjectCreator(
          ProjectRepository projectRepository, ProfileRepository profileRepository,
          TrackRepository trackRepository) {
    return new CreateDefaultProjectUseCase(projectRepository, profileRepository, trackRepository);
  }

  @Provides
  GetAudioFromProjectUseCase provideMusicRetriever() {
    return new GetAudioFromProjectUseCase();
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
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase) {
    return new Camera2Wrapper(activity, RecordCamera2Presenter.DEFAULT_CAMERA_ID, textureView,
            directorySaveVideos,
            getVideoFormatFromCurrentProjectUseCase
                    .getVideoRecordedFormatFromCurrentProjectUseCase());
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

  @Provides
  SessionConfig provideSessionConfig(){
    return new SessionConfig(destinationFolderPath, numTracks);
  }

  @Provides
  AudioRecorder providesAudioRecorder(){
    try {
      return new AudioRecorder(provideSessionConfig());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
