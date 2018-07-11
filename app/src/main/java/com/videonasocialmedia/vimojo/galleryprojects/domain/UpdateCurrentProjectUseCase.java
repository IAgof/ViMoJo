package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import javax.inject.Inject;

/**
 * Created by alvaro on 13/12/16.
 */

public class UpdateCurrentProjectUseCase {

  protected ProjectRepository projectRepository;

  @Inject
  public UpdateCurrentProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void updateLastModificationAndProjectInstance(Project project) {
    project.updateDateOfModification(DateUtils.getDateRightNow());
    projectRepository.update(project);
  }
}
