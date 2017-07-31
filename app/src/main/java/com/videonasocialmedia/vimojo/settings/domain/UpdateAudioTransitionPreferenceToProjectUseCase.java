package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateAudioTransitionPreferenceToProjectUseCase {

  private Project currentProject;
  private ProjectRepository projectRepository;

  public UpdateAudioTransitionPreferenceToProjectUseCase(ProjectRepository projectRepository){
    this.projectRepository = projectRepository;
  }

  public void setAudioFadeTransitionActivated(boolean data) {
    currentProject = Project.getInstance(null,null,null,null);
    currentProject.setAudioFadeTransitionActivated(data);
    projectRepository.update(currentProject);
  }
}
