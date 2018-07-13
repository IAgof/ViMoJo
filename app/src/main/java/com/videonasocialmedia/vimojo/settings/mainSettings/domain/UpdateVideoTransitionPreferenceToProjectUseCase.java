package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateVideoTransitionPreferenceToProjectUseCase {
  private ProjectRepository projectRepository;

  public UpdateVideoTransitionPreferenceToProjectUseCase(ProjectRepository projectRepository) {
      this.projectRepository = projectRepository;
  }

  public void setVideoFadeTransitionActivated(Project currentProject, boolean data) {
      currentProject.getVMComposition().setVideoFadeTransitionActivated(data);
      projectRepository.update(currentProject);
  }
}
