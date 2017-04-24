package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import javax.inject.Inject;

/**
 * Created by alvaro on 10/01/17.
 */

public class GetPreferencesTransitionFromProjectUseCase {

  private Project currentProject;

  @Inject
  public GetPreferencesTransitionFromProjectUseCase(){
  }

  public boolean isAudioFadeTransitionActivated() {
    currentProject = Project.getInstance(null, null, null);
    return currentProject.isAudioFadeTransitionActivated();
  }

  public boolean isVideoFadeTransitionActivated() {
    currentProject = Project.getInstance(null, null, null);
    return currentProject.isVideoFadeTransitionActivated();
  }
}
