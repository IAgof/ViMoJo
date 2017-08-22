package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 23/11/16.
 */

public class EditorPresenter {
  private String LOG_TAG = "EditorPresenter";

  private EditorActivityView editorActivityView;
  private SharedPreferences sharedPreferences;
  protected UserEventTracker userEventTracker;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  protected Project currentProject;
  private SharedPreferences.Editor preferencesEditor;
  private Context context;
  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
  private final NewClipImporter newClipImporter;
  private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;

  @Inject
  public EditorPresenter(
          EditorActivityView editorActivityView, SharedPreferences sharedPreferences,
          Context context, CreateDefaultProjectUseCase createDefaultProjectUseCase,
          GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          NewClipImporter newClipImporter) {
    this.editorActivityView = editorActivityView;
    this.sharedPreferences = sharedPreferences;
    this.context = context;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.currentProject = loadCurrentProject();
    this.newClipImporter = newClipImporter;
  }

  public Project loadCurrentProject() {
    // TODO(jliarte): this should make use of a repository or use case to load the Project
    return Project.getInstance(null, null, null, null);
  }

  public void getPreferenceUserName() {
    String userNamePreference = sharedPreferences.getString(ConfigPreferences.USERNAME, null);
    if (userNamePreference != null && !userNamePreference.isEmpty()) {
      editorActivityView.showPreferenceUserName(userNamePreference);
    } else {
      editorActivityView.showPreferenceUserName(context.getResources().getString(R.string.username));
    }
  }

  public void getPreferenceEmail() {
    String emailPreference = sharedPreferences.getString(ConfigPreferences.EMAIL, null);
    if (emailPreference != null && !emailPreference.isEmpty()) {
      editorActivityView.showPreferenceEmail(emailPreference);
    } else {
      editorActivityView.showPreferenceEmail(context.getResources().getString(R.string.emailPreference));
    }
  }

  public void createNewProject(String roothPath, String privatePath, boolean isWatermarkFeatured) {
    createDefaultProjectUseCase.createProject(roothPath, privatePath, isWatermarkFeatured);
    clearProjectDataFromSharedPreferences();
    editorActivityView.updateViewResetProject();
  }

  private void clearProjectDataFromSharedPreferences() {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
    preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
  }

  public void init() {
    newClipImporter.relaunchUnfinishedAdaptTasks();
    obtainVideos();
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
    Project currentProject = Project.getInstance(null, null, null, null);
    VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();
    Drawable drawableVideoFadeTransition = currentProject.getVMComposition()
            .getDrawableFadeTransitionVideo();
    relaunchTranscoderTempBackgroundUseCase.relaunchExport(drawableVideoFadeTransition, video,
            videoFormat, currentProject.getProjectPathIntermediateFileAudioFade());
  }
}
