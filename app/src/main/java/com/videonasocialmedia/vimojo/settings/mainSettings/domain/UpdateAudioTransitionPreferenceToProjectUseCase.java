package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateAudioTransitionPreferenceToProjectUseCase {

  private ProjectRepository projectRepository;

  public UpdateAudioTransitionPreferenceToProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void setAudioFadeTransitionActivated(Project currentProject, boolean data) {
    currentProject.getVMComposition().setAudioFadeTransitionActivated(data);
    projectRepository.update(currentProject);
  }
}
