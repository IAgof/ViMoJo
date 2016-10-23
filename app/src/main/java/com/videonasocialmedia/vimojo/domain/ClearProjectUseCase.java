package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Use case for discarding current project and creating a new one
 */
public class ClearProjectUseCase {
  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public void clearProject(Project project) {
    projectRepository.remove(project);
    project.clear();
  }
}
