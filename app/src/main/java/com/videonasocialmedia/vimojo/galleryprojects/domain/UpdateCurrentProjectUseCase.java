package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

/**
 * Created by alvaro on 13/12/16.
 */

public class UpdateCurrentProjectUseCase {

  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public void updateLastModificationAndProjectInstance(Project project){
    project.setLastModification(DateUtils.getDateRightNow());
    projectRepository.update(project);
    Project.INSTANCE = project;
  }
}
