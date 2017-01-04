package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import javax.inject.Inject;

/**
 * Created by ruth on 23/11/16.
 */

public class EditorPresenter {

  private EditorActivityView editorActivityView;
  private SharedPreferences sharedPreferences;
  protected UserEventTracker userEventTracker;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  protected Project currentProject;
  private SharedPreferences.Editor preferencesEditor;
  private Context context;

  @Inject
  public EditorPresenter(EditorActivityView editorActivityView,
                         SharedPreferences sharedPreferences, Context context,
                         CreateDefaultProjectUseCase createDefaultProjectUseCase) {
    this.editorActivityView = editorActivityView;
    this.sharedPreferences = sharedPreferences;
    this.context = context;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.currentProject = loadCurrentProject();
  }

  public Project loadCurrentProject() {
    // TODO(jliarte): this should make use of a repository or use case to load the Project
    return Project.getInstance(null, null, null);
  }

  public void getPreferenceUserName() {
    String userNamePreference = sharedPreferences.getString(ConfigPreferences.USERNAME, null);
    if(userNamePreference!=null && !userNamePreference.isEmpty())
      editorActivityView.showPreferenceUserName(userNamePreference);
    else{
      editorActivityView.showPreferenceUserName(context.getResources().getString(R.string.username));
    }
  }

  public void getPreferenceEmail() {
    String emailPreference = sharedPreferences.getString(ConfigPreferences.EMAIL, null);
    if(emailPreference!=null && !emailPreference.isEmpty())
      editorActivityView.showPreferenceEmail(emailPreference);
    else {
      editorActivityView.showPreferenceEmail(context.getResources().getString(R.string.emailPreference));
    }

  }

  public void createNewProject(String roothPath){
    createDefaultProjectUseCase.createProject(roothPath);
    clearProjectDataFromSharedPreferences();
    editorActivityView.updateViewResetProject();
  }

  private void clearProjectDataFromSharedPreferences() {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
    preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
  }
}
