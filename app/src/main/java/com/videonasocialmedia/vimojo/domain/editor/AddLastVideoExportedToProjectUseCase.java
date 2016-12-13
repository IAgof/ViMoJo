package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.LastVideoExported;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

/**
 * Created by alvaro on 13/12/16.
 */

public class AddLastVideoExportedToProjectUseCase {

  private Project currentProject = Project.getInstance(null, null, null);
  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public AddLastVideoExportedToProjectUseCase(){

  }

  public void addLastVideoExportedToProject(String pathVideoExported){

    LastVideoExported lastVideoExported = new LastVideoExported(pathVideoExported,
        DateUtils.getDateRightNow());

    currentProject.setLastVideoExported(lastVideoExported);

    projectRepository.update(currentProject);

  }

}
