package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import javax.inject.Inject;

/**
 * Created by alvaro on 10/01/17.
 */

/***
 * Should query composition
 */
@Deprecated
public class GetPreferencesTransitionFromProjectUseCase {

  private Project currentProject;

  @Inject
  public GetPreferencesTransitionFromProjectUseCase() {
  }

  /***
   * Should query composition
    */
  @Deprecated
  public boolean isAudioFadeTransitionActivated() {
    currentProject = Project.getInstance(null, null, null, null);
    return currentProject.getVMComposition().isAudioFadeTransitionActivated();
  }

  /***
   * Should query composition
   */
  @Deprecated
  public boolean isVideoFadeTransitionActivated() {
    currentProject = Project.getInstance(null, null, null, null);
    return currentProject.getVMComposition().isVideoFadeTransitionActivated();
  }
}
