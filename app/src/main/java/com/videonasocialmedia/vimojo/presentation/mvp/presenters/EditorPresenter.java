package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.store.billing.PlayStoreBillingDelegate;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 23/11/16.
 */

public class EditorPresenter implements PlayStoreBillingDelegate.BillingDelegateView {
  private final PlayStoreBillingDelegate playStoreBillingDelegate;
  private static final String LOG_TAG = EditorPresenter.class.getSimpleName();

  private EditorActivityView editorActivityView;
  private SharedPreferences sharedPreferences;
  protected UserEventTracker userEventTracker;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  protected Project currentProject;
  private SharedPreferences.Editor preferencesEditor;
  private Context context;
  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
  private ProjectRepository projectRepository;
  private final NewClipImporter newClipImporter;
  private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;

  private final String THEME_DARK = "dark";
  private final String THEME_LIGHT = "light";
  private final BillingManager billingManager;

  @Inject
  public EditorPresenter(
          EditorActivityView editorActivityView, SharedPreferences sharedPreferences,
          Context context, UserEventTracker userEventTracker,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          ProjectRepository projectRepository,
          NewClipImporter newClipImporter, BillingManager billingManager) {
    this.editorActivityView = editorActivityView;
    this.sharedPreferences = sharedPreferences;
    this.context = context;
    this.userEventTracker = userEventTracker;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.projectRepository = projectRepository;
    this.currentProject = getCurrentProject();
    this.newClipImporter = newClipImporter;
    this.billingManager = billingManager;
    playStoreBillingDelegate = new PlayStoreBillingDelegate(billingManager, this);
  }

  public void init() {
    newClipImporter.relaunchUnfinishedAdaptTasks(currentProject);
    obtainVideos();
    checkFeaturesAvailable();
  }

  public void onPause() {
    if (BuildConfig.VIMOJO_STORE_AVAILABLE) {
      billingManager.destroy();
    }
  }

  private void checkFeaturesAvailable() {
    checkWatermark();
    checkVimojoStore();
  }

  private void checkVimojoStore() {
    if (BuildConfig.VIMOJO_STORE_AVAILABLE) {
      playStoreBillingDelegate.initBilling((Activity) editorActivityView);
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
    // TODO(jliarte): this should make use of a repository or use case to load the Project
    return Project.getInstance(null, null, null, null);
  }

  public boolean getPreferenceThemeApp() {
    // TODO(jliarte): 27/10/17 improve default theme setting with a build constant
    boolean isActivateDarkTheme = sharedPreferences
            .getBoolean(ConfigPreferences.THEME_APP_DARK, Constants.DEFAULT_THEME_DARK_STATE);
    return isActivateDarkTheme;
  }

  private void deactivateDarkThemePreference() {
    sharedPreferences.edit().putBoolean(ConfigPreferences.THEME_APP_DARK, false).commit();
  }

  public void createNewProject(String rootPath, String privatePath) {
    createDefaultProjectUseCase.createProject(rootPath, privatePath, getPreferenceWaterMark());
    clearProjectDataFromSharedPreferences();
    editorActivityView.updateViewResetProject();
  }

  private void clearProjectDataFromSharedPreferences() {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
    preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
  }

  private void obtainVideos() {
    getMediaListFromProjectUseCase.getMediaListFromProject(new OnVideosRetrieved() {
      @Override
      public void onVideosRetrieved(List<Video> videoList) {
        checkIfIsNeededRelaunchTranscodingTempFileTaskVideos(videoList);
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
      if (isShareActivity()) {
        editorActivityView.restartShareActivity(getCurrentProject().getPathLastVideoExported());
      } else {
        editorActivityView.restartActivity();
      }
    }
    if (preference.equals(ConfigPreferences.WATERMARK)) {
      // TODO:(alvaro.martinez) 2/11/17 track watermark applied
      Project project = loadCurrentProject();
      projectRepository.setWatermarkActivated(project, isChecked);
    }
  }

  public void updateTheme() {
    boolean isDarkThemeActivated = getPreferenceThemeApp();
    String currentTheme = getCurrentAppliedTheme();
    if (isDarkThemeActivated && currentTheme.equals(THEME_LIGHT)
            || !isDarkThemeActivated && currentTheme.equals(THEME_DARK)) {
      if (isShareActivity()) {
        editorActivityView.restartShareActivity(getCurrentProject().getPathLastVideoExported());
      } else {
        editorActivityView.restartActivity();
      }
    }
  }

  private boolean isShareActivity() {
    if (context.getClass().getName().compareTo(ShareActivity.class.getName()) == 0) {
      return true;
    } else {
      return false;
    }
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
    if(BuildConfig.FEATURE_FORCE_WATERMARK) {
      return true;
    }
    return sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, false);
  }

  private void activateWatermarkPreference() {
    sharedPreferences.edit().putBoolean(ConfigPreferences.WATERMARK, true).commit();
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
      activateWatermarkPreference();
      editorActivityView.activateWatermark();
    }
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public void updateHeaderViewCurrentProject() {
    // Thumb from first video in current project
    String pathThumbProject = null;
    if(currentProject.getVMComposition().hasVideos()) {
      pathThumbProject = currentProject.getMediaTrack().getItems().get(0).getMediaPath();
    }
    String name = currentProject.getProjectInfo().getTitle();
    String date = DateUtils.toFormatDateDayMonthYear(currentProject.getLastModification());
    editorActivityView.setHeaderViewCurrentProject(pathThumbProject, name, date);
  }

  public void updateTitleCurrentProject(String title) {
    Project project = loadCurrentProject();
    ProjectInfo projectInfo = project.getProjectInfo();
    projectInfo.setTitle(title);
    projectRepository.setProjectInfo(project, projectInfo.getTitle(), projectInfo.getDescription(),
        projectInfo.getProductTypeList());
  }
}
