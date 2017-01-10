package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateAudioTransitionPreferenceToProjectUseCase {

  private Project currentProject;
  private ProjectRepository projectRepository = new ProjectRealmRepository();

  public UpdateAudioTransitionPreferenceToProjectUseCase(){

  }

  public void setAudioFadeTransitionActivated(boolean data) {
    currentProject = Project.getInstance(null,null,null);
    currentProject.setAudioFadeTransitionActivated(data);
    projectRepository.update(currentProject);
  }
}
