package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 20/12/16.
 */

public class UpdateTitleProjectUseCase {
  ProjectRepository projectRepository;

  @Inject
  public UpdateTitleProjectUseCase(ProjectRepository projectRepository){
    this.projectRepository = projectRepository;
  }

  public void setTitle(Project project, String newTitle){
    project.setTitle(newTitle);
    projectRepository.update(project);
  }
}
