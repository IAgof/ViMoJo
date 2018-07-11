package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectDataSource;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateAudioTransitionPreferenceToProjectUseCase {

  private ProjectDataSource projectRepository;

  public UpdateAudioTransitionPreferenceToProjectUseCase(ProjectDataSource projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void setAudioFadeTransitionActivated(Project currentProject, boolean data) {
    currentProject.getVMComposition().setAudioFadeTransitionActivated(data);
    projectRepository.update(currentProject);
  }
}
