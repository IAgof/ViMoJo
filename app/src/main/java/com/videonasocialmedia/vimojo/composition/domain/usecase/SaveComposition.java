package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by jliarte on 11/07/18.
 */

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;

import javax.inject.Inject;

/**
 * Use Case for saving a {@link Project} into repository.
 */
public class SaveComposition {
  private final ProjectRepository projectRepository;

  @Inject
  public SaveComposition(ProjectRepository projectRepository) {
    // TODO(jliarte): 11/07/18 should project repo also manage instance cache? as in memory datasource?
    this.projectRepository = projectRepository;
  }

  public void saveComposition(Project project) {
    projectRepository.add(project);
  }
}
