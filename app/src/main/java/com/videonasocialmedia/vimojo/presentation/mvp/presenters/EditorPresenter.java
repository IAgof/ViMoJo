package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.ClearProjectUseCase;
import com.videonasocialmedia.vimojo.domain.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LoadCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.NavigatorDrawerView;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ToolbarNavigator;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import javax.inject.Inject;

/**
 * Created by ruth on 23/11/16.
 */

public class EditorPresenter {

  private  NavigatorDrawerView navigatorDrawerView;
  private SharedPreferences sharedPreferences;
  private ProfileRepository profileRepository;
  protected UserEventTracker userEventTracker;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  protected Project currentProject;
  private SharedPreferences.Editor preferencesEditor;

  private ToolbarNavigator.ProjectModifiedCallBack projectModifiedCallBack;
  private Context context;

  public EditorPresenter(NavigatorDrawerView navigatorDrawerView, ToolbarNavigator.ProjectModifiedCallBack projectModifiedCallBack,
                         SharedPreferences sharedPreferences, Context context) {

    this.navigatorDrawerView=navigatorDrawerView;
    this.projectModifiedCallBack = projectModifiedCallBack;
    this.sharedPreferences=sharedPreferences;
    this.context=context;
    createDefaultProjectUseCase = new CreateDefaultProjectUseCase();
    this.currentProject=loadCurrentProject();


  }
  public Project loadCurrentProject() {
    ProjectRealmRepository projectRealmRepository= new ProjectRealmRepository();
    return new LoadCurrentProjectUseCase(projectRealmRepository).loadCurrentProject();
  }

  public void getPreferenceUserName() {
    String userNamePreference = sharedPreferences.getString(ConfigPreferences.USERNAME, null);
    if(userNamePreference!=null && !userNamePreference.isEmpty())
      navigatorDrawerView.showPreferenceUserName(userNamePreference);
    else{
      navigatorDrawerView.showPreferenceUserName(context.getResources().getString(R.string.username));
    }
  }

  public void getPreferenceEmail() {
    String emailPreference = sharedPreferences.getString(ConfigPreferences.EMAIL, null);
    if(emailPreference!=null && !emailPreference.isEmpty())
      navigatorDrawerView.showPreferenceEmail(emailPreference);
    else {
      navigatorDrawerView.showPreferenceEmail(context.getResources().getString(R.string.emailPreference));
    }

  }


  public void resetProject() {
    String rootPath= sharedPreferences.getString(ConfigPreferences.PRIVATE_PATH,"");
    clearProjectDataFromSharedPreferences();

      new ClearProjectUseCase().clearProject(currentProject);
      profileRepository = new ProfileSharedPreferencesRepository(sharedPreferences, context);
      createDefaultProjectUseCase.loadOrCreateProject(rootPath, profileRepository.getCurrentProfile());
      navigatorDrawerView.updateViewResetProject();
  }

  private void clearProjectDataFromSharedPreferences() {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
    preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
  }
}
