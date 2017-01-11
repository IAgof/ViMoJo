package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateVideoTransitionPreferenceToProjectUseCase {

  private Project currentProject;
  private ProjectRepository projectRepository;

  public UpdateVideoTransitionPreferenceToProjectUseCase(ProjectRepository projectRepository){
      this.projectRepository = projectRepository;
  }

  public void setVideoFadeTransitionActivated(boolean data) {
      currentProject = Project.getInstance(null,null,null);
      currentProject.setVideoFadeTransitionActivated(data);
      projectRepository.update(currentProject);
  }
}
