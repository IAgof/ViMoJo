package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 20/12/16.
 */

public class UpdateTitleProjectUseCase {

  ProjectRepository projectRepository = new ProjectRealmRepository();

  public void setTitle(Project project, String newTitle){
    project.setTitle(newTitle);
    projectRepository.update(project);
  }
}
