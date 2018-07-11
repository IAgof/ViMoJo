package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectDataSource;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import javax.inject.Inject;

/**
 * Created by alvaro on 13/12/16.
 */

public class UpdateCurrentProjectUseCase {

  protected ProjectDataSource projectRepository;

  @Inject
  public UpdateCurrentProjectUseCase(ProjectDataSource projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void updateLastModificationAndProjectInstance(Project project) {
    project.updateDateOfModification(DateUtils.getDateRightNow());
    projectRepository.update(project);
  }
}
