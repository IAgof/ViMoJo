package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;

/**
 * Created by alvaro on 27/02/17.
 */

public class GetWatermarkPreferenceFromProjectUseCase {

  private Project currentProject;

  public GetWatermarkPreferenceFromProjectUseCase(){
    currentProject = Project.getInstance(null, null, null, null);
  }

  public boolean isWatermarkActivated() {
    return currentProject.hasWatermark();
  }
}
