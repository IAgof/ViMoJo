package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.LastVideoExported;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 13/12/16.
 */

public class AddLastVideoExportedToProjectUseCase {

  private ProjectRepository projectRepository;

  @Inject
  public AddLastVideoExportedToProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void addLastVideoExportedToProject(String pathVideoExported, String date) {
    //// TODO:(alvaro.martinez) 19/12/16 Move this functionality to VMComposition
    Project currentProject = Project.getInstance(null,null,null,null);
    LastVideoExported lastVideoExported = new LastVideoExported(pathVideoExported, date);
    currentProject.setLastVideoExported(lastVideoExported);
    currentProject.updateDateOfModification(date);
    projectRepository.updateWithDate(currentProject, date);
  }

}
