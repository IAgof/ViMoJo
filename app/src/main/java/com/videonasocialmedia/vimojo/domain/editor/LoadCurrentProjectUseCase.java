package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Inject;

/**
 * Created by jliarte on 23/10/16.
 */
public class LoadCurrentProjectUseCase {
  protected ProjectRepository projectRepository;

  @Inject
  public LoadCurrentProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public Project loadCurrentProject() {
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
    }
    return Project.INSTANCE;
  }
}
