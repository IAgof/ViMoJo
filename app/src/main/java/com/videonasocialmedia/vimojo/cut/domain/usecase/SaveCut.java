package com.videonasocialmedia.vimojo.cut.domain.usecase;

/**
 * Created by jliarte on 11/07/18.
 */

import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Inject;

/**
 * Use Case for saving a Cut
 */
public class SaveCut {
  private final ProjectRepository projectRepository;

  @Inject
  public SaveCut(ProjectRepository projectRepository) {
    // TODO(jliarte): 11/07/18 should project repo also manage instance cache? as in memory datasource?
    this.projectRepository = projectRepository;
  }

  public void saveCut(Project project) {
    projectRepository.add(project);
  }
}
