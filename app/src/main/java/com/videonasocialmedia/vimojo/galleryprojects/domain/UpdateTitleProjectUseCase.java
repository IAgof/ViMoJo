package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 20/12/16.
 */

public class UpdateTitleProjectUseCase {
  ProjectRepository projectRepository;

  public UpdateTitleProjectUseCase(ProjectRepository projectRepository){
    this.projectRepository = projectRepository;
  }

  public void setTitle(Project project, String newTitle){
    project.setTitle(newTitle);
    projectRepository.update(project);
  }
}
