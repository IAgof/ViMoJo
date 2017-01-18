package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;

/**
 * Created by alvaro on 10/01/17.
 */

public class GetPreferencesTransitionFromProjectUseCase {

  private Project currentProject;

  public GetPreferencesTransitionFromProjectUseCase(){
    currentProject = Project.getInstance(null, null, null);
  }

  public boolean isAudioFadeTransitionActivated() {
    return currentProject.isAudioFadeTransitionActivated();
  }

  public boolean isVideoFadeTransitionActivated() {
    return currentProject.isVideoFadeTransitionActivated();
  }
}
