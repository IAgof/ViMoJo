package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

/**
 * Created by ruth on 23/11/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.store.billing.PlayStoreBillingDelegate;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC;
import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;

/**
 * Parent class for three main edit views presenters: {@link EditPresenter},
 * {@link com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.SoundPresenter},
 * and {@link com.videonasocialmedia.vimojo.share.presentation.mvp.presenters.ShareVideoPresenter}
 * with common functionalities for three views and drawer setup and management.
 * This class it's also the one in charge handling
 * {@link com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer}.
 */
public class EditorPresenter implements PlayStoreBillingDelegate.BillingDelegateView {
  private final PlayStoreBillingDelegate playStoreBillingDelegate;
  private static final String LOG_TAG = EditorPresenter.class.getSimpleName();
  public static final float VOLUME_MUTE = 0f;

  private EditorActivityView editorActivityView;
  private VideonaPlayerView videonaPlayerView;
  private SharedPreferences sharedPreferences;
  protected UserEventTracker userEventTracker;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  protected Project currentProject;
  private SharedPreferences.Editor preferencesEditor;
  private Context context;
  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
  private RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
  private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
  private ProjectRepository projectRepository;
  private final NewClipImporter newClipImporter;
  private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;

  private final String THEME_DARK = "dark";
  private final String THEME_LIGHT = "light";
  private final BillingManager billingManager;

  @Inject
  public EditorPresenter(
          EditorActivityView editorActivityView, VideonaPlayerView videonaPlayerView,
          SharedPreferences sharedPreferences, Activity context, UserEventTracker userEventTracker,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase,
          GetAudioFromProjectUseCase getAudioFromProjectUseCase,
          GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          ProjectRepository projectRepository,
          NewClipImporter newClipImporter, BillingManager billingManager) {
    this.editorActivityView = editorActivityView;
    this.videonaPlayerView = videonaPlayerView;
    this.sharedPreferences = sharedPreferences;
    this.context = context;
    this.userEventTracker = userEventTracker;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
    this.removeVideoFromProjectUseCase = removeVideoFromProjectUseCase;
    this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;
    this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.projectRepository = projectRepository;
    this.currentProject = getCurrentProject();
    this.newClipImporter = newClipImporter;
    this.billingManager = billingManager;
    playStoreBillingDelegate = new PlayStoreBillingDelegate(billingManager, this);
  }

  public void init(boolean hasBeenProjectExported, String videoPath) {
    if (!hasBeenProjectExported) {
      initPreviewFromProject();
    } else {
      initPreviewFromVideoExported(videoPath);
    }
  }

  protected void initPreviewFromVideoExported(String videoPath) {
    List<Video> videoList = Collections.singletonList(new Video(videoPath, Video.DEFAULT_VOLUME));
    videonaPlayerView.initPreviewFromVideo(videoList);
  }

  protected void initPreviewFromProject() {
    obtainVideos();
    retrieveMusic();
    retrieveTransitions();
    retrieveVolumeOnTracks();
  }

  public void onPause() {
    if (BuildConfig.VIMOJO_STORE_AVAILABLE) {
      billingManager.destroy();
    }
  }

  public void checkFeaturesAvailable() {
    checkWatermark();
    checkVimojoStore();
  }

  private void checkVimojoStore() {
    if (BuildConfig.VIMOJO_STORE_AVAILABLE) {
      playStoreBillingDelegate.initBilling((Activity) context);
      editorActivityView.setIconsPurchaseInApp();
    } else {
      editorActivityView.setIconsFeatures();
      editorActivityView.hideVimojoStoreViews();
    }
  }

  private void checkWatermark() {
    if (BuildConfig.FEATURE_WATERMARK_SWITCH && !BuildConfig.FEATURE_FORCE_WATERMARK) {
      editorActivityView.watermarkFeatureAvailable();
    } else {
      editorActivityView.hideWatermarkSwitch();
    }
  }

  public Project getCurrentProject() {
    return projectRepository.getCurrentProject();
  }

  public boolean getPreferenceThemeApp() {
    // TODO(jliarte): 27/10/17 improve default theme setting with a build constant
    boolean isActivateDarkTheme = sharedPreferences
            .getBoolean(ConfigPreferences.THEME_APP_DARK, Constants.DEFAULT_THEME_DARK_STATE);
    return isActivateDarkTheme;
  }

  private void deactivateDarkThemePreference() {
    sharedPreferences.edit().putBoolean(ConfigPreferences.THEME_APP_DARK, false).apply();
  }

  public void createNewProject(String rootPath, String privatePath) {
    createDefaultProjectUseCase.createProject(rootPath, privatePath, getPreferenceWaterMark());
    clearProjectDataFromSharedPreferences();
    editorActivityView.goToRecordOrGalleryScreen();
  }

  private void clearProjectDataFromSharedPreferences() {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
    preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
    preferencesEditor.apply();
  }

  public void obtainVideos() {
    getMediaListFromProjectUseCase.getMediaListFromProject(new OnVideosRetrieved() {
      @Override
      public void onVideosRetrieved(List<Video> videosRetrieved) {
        checkIfIsNeededRelaunchTranscodingTempFileTaskVideos(videosRetrieved);
        List<Video> checkedVideoList = checkMediaPathVideosExistOnDevice(videosRetrieved);
        List<Video> videoCopy = new ArrayList<>(checkedVideoList);
        videonaPlayerView.bindVideoList(videoCopy);
        //Relaunch videos only if Project has videos. Fix problem removing all videos from Edit screen.
        newClipImporter.relaunchUnfinishedAdaptTasks(currentProject);
      }

      @Override
      public void onNoVideosRetrieved() {
      }
    });
  }

  public void checkIfIsNeededRelaunchTranscodingTempFileTaskVideos(List<Video> videoList) {
    for (Video video : videoList) {
      ListenableFuture transcodingJob = video.getTranscodingTask();
      // Condition to relaunch transcoding job.
      if (transcodingJob == null && !video.isTranscodingTempFileFinished()) {
        relaunchTranscoderTempFileJob(video);
        Log.d(LOG_TAG, "Need to relaunch video " + videoList.indexOf(video)
                + " - " + video.getMediaPath());
      }
    }
  }

  private List<Video> checkMediaPathVideosExistOnDevice(List<Video> videoList) {
    List<Video> checkedVideoList = new ArrayList<>();
    for (int index = 0; index < videoList.size(); index++) {
      Video video = videoList.get(index);
      if (!new File(video.getMediaPath()).exists()) {
        // TODO(jliarte): 26/04/17 notify the user we are deleting items from project!!! FIXME
        ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
        mediaToDeleteFromProject.add(video);
        removeVideoFromProjectUseCase.removeMediaItemsFromProject(
            mediaToDeleteFromProject, new OnRemoveMediaFinishedListener() {
              @Override
              public void onRemoveMediaItemFromTrackSuccess() {

              }

              @Override
              public void onRemoveMediaItemFromTrackError() {
                // TODO: 19/2/18 Define on remove media error
                editorActivityView.showError(R.string.addMediaItemToTrackError);
              }
            });
        Log.e(LOG_TAG, video.getMediaPath() + " not found!! deleting from project");
      } else {
        checkedVideoList.add(video);
      }
    }
    return checkedVideoList;
  }

  private void retrieveMusic() {
    if (currentProject.getVMComposition().hasMusic()) {
      getAudioFromProjectUseCase.getMusicFromProject(music -> {
        Music copyMusic = new Music(music);
        videonaPlayerView.bindMusic(copyMusic);
      });
    }
    if (currentProject.getVMComposition().hasVoiceOver()) {
      getAudioFromProjectUseCase.getVoiceOverFromProject(voiceOver -> {
        Music copyVoiceOver = new Music(voiceOver);
        videonaPlayerView.bindVoiceOver(copyVoiceOver);
      });
    }
  }

  private void retrieveTransitions() {
    if (getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()) {
      videonaPlayerView.setVideoFadeTransitionAmongVideos();
    }
    if (getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated() &&
        !currentProject.getVMComposition().hasMusic()) {
      videonaPlayerView.setAudioFadeTransitionAmongVideos();
    }
  }

  protected void retrieveVolumeOnTracks() {
    if (currentProject.getVMComposition().hasMusic()) {
      Track musicTrack = currentProject.getAudioTracks().get(INDEX_AUDIO_TRACK_MUSIC);
      if (musicTrack.isMuted()) {
        videonaPlayerView.setMusicVolume(VOLUME_MUTE);
      } else {
        videonaPlayerView.setMusicVolume(musicTrack.getVolume());
      }
    }

    if (currentProject.getVMComposition().hasVoiceOver()) {
      Track voiceOverTrack = currentProject.getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER);
      if (voiceOverTrack.isMuted()) {
        videonaPlayerView.setVoiceOverVolume(VOLUME_MUTE);
      } else {
        videonaPlayerView.setVoiceOverVolume(voiceOverTrack.getVolume());
      }
    }

    if (currentProject.getVMComposition().hasVideos()) {
      Track mediaTrack = currentProject.getMediaTrack();
      if (mediaTrack.isMuted()) {
        videonaPlayerView.setVideoMute();
      } else {
        videonaPlayerView.setVideoVolume(mediaTrack.getVolume());
      }
    }
  }

  private void relaunchTranscoderTempFileJob(Video video) {
    Project currentProject = getCurrentProject();
    relaunchTranscoderTempBackgroundUseCase.relaunchExport(video, currentProject);
  }

  public void switchPreference(final boolean isChecked, String preference) {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putBoolean(preference, isChecked);
    preferencesEditor.apply();
    if (preference.equals(ConfigPreferences.THEME_APP_DARK)) {
      userEventTracker.trackThemeAppDrawerChanged(isChecked);
      editorActivityView.restartActivity(context.getClass());
    }
    if (preference.equals(ConfigPreferences.WATERMARK)) {
      // TODO:(alvaro.martinez) 2/11/17 track watermark applied
      projectRepository.setWatermarkActivated(currentProject, isChecked);
      if (isShareActivity()) {
        editorActivityView.restartActivity(context.getClass());
      }
    }
  }

  public void updateTheme() {
    boolean isDarkThemeActivated = getPreferenceThemeApp();
    String currentTheme = getCurrentAppliedTheme();
    if (isDarkThemeActivated && currentTheme.equals(THEME_LIGHT)
            || !isDarkThemeActivated && currentTheme.equals(THEME_DARK)) {
      editorActivityView.restartActivity(context.getClass());
    }
  }

  private boolean isShareActivity() {
    return context.getClass().getName().equals(ShareActivity.class.getName());
  }

  private String getCurrentAppliedTheme() {
    String currentTheme;
    TypedValue outValue = new TypedValue();
    context.getTheme().resolveAttribute(R.attr.themeName, outValue, true);
    if (THEME_DARK.equals(outValue.string)) {
      currentTheme = THEME_DARK;
    } else {
      currentTheme = THEME_LIGHT;
    }
    return currentTheme;
  }

  public boolean getPreferenceWaterMark() {
    if (BuildConfig.FEATURE_FORCE_WATERMARK) {
      return true;
    }
    return currentProject.hasWatermark();
  }

  @Override
  public void itemDarkThemePurchased(boolean purchased) {
    if (purchased) {
      editorActivityView.itemDarkThemePurchased();
    } else {
      deactivateDarkThemePreference();
      editorActivityView.deactivateDarkTheme();
    }
  }

  @Override
  public void itemWatermarkPurchased(boolean purchased) {
    if (purchased) {
      editorActivityView.itemWatermarkPurchased();
    } else {
      editorActivityView.activateWatermark();
    }
  }

  public void updateHeaderViewCurrentProject() {
    // Thumb from first video in current project
    String pathThumbProject = null;
    if (currentProject.getVMComposition().hasVideos()) {
      pathThumbProject = currentProject.getMediaTrack().getItems().get(0).getMediaPath();
    }
    String name = currentProject.getProjectInfo().getTitle();
    String date = DateUtils.toFormatDateDayMonthYear(currentProject.getLastModification());
    editorActivityView.setHeaderViewCurrentProject(pathThumbProject, name, date);
  }

  public void updateTitleCurrentProject(String title) {
    Project project = getCurrentProject();
    ProjectInfo projectInfo = project.getProjectInfo();
    projectInfo.setTitle(title);
    projectRepository.setProjectInfo(project, projectInfo.getTitle(), projectInfo.getDescription(),
        projectInfo.getProductTypeList());
  }
}
