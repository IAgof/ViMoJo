package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectDataSource;

/**
 * Created by alvaro on 27/02/17.
 */

public class UpdateWatermarkPreferenceToProjectUseCase {
  private ProjectDataSource projectRepository;
  private String TAG = "UpdateWatermarkPreferenceToProjectUseCase";

  public UpdateWatermarkPreferenceToProjectUseCase(ProjectDataSource projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void setWatermarkActivated(Project currentProject, boolean data) {
    currentProject.setWatermarkActivated(data);
    projectRepository.update(currentProject);
  }
}
