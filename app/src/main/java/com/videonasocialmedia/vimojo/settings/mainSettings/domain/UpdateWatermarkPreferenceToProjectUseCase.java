package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;

/**
 * Created by alvaro on 27/02/17.
 */

public class UpdateWatermarkPreferenceToProjectUseCase {
  private ProjectRepository projectRepository;
  private String TAG = "UpdateWatermarkPreferenceToProjectUseCase";

  public UpdateWatermarkPreferenceToProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void setWatermarkActivated(Project currentProject, boolean data) {
    currentProject.setWatermarkActivated(data);
    projectRepository.update(currentProject);
  }
}
