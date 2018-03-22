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

  @Inject
  public GetPreferencesTransitionFromProjectUseCase() {
  }

  /***
   * Should query composition
    */
  @Deprecated
  public boolean isAudioFadeTransitionActivated(Project currentProject) {
    return currentProject.getVMComposition().isAudioFadeTransitionActivated();
  }

  /***
   * Should query composition
   */
  @Deprecated
  public boolean isVideoFadeTransitionActivated(Project currentProject) {
    return currentProject.getVMComposition().isVideoFadeTransitionActivated();
  }
}
