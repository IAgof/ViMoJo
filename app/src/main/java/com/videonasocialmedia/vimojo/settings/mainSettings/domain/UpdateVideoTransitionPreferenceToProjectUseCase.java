package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.composition.domain.model.Project;

import javax.inject.Inject;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateVideoTransitionPreferenceToProjectUseCase {
  @Inject public UpdateVideoTransitionPreferenceToProjectUseCase() {
  }

  public void setVideoFadeTransitionActivated(Project currentProject, boolean data) {
      currentProject.getVMComposition().setVideoFadeTransitionActivated(data);
  }
}
