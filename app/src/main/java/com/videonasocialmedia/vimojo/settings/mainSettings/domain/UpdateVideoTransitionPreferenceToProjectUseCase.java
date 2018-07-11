package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectDataSource;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateVideoTransitionPreferenceToProjectUseCase {
  private ProjectDataSource projectRepository;

  public UpdateVideoTransitionPreferenceToProjectUseCase(ProjectDataSource projectRepository) {
      this.projectRepository = projectRepository;
  }

  public void setVideoFadeTransitionActivated(Project currentProject, boolean data) {
      currentProject.getVMComposition().setVideoFadeTransitionActivated(data);
      projectRepository.update(currentProject);
  }
}
