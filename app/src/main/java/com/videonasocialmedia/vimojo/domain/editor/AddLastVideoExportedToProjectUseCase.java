package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.LastVideoExported;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 13/12/16.
 */

public class AddLastVideoExportedToProjectUseCase {
  @Inject
  public AddLastVideoExportedToProjectUseCase() {
  }

  public void addLastVideoExportedToProject(Project currentProject, String pathVideoExported,
                                            String date) {
    LastVideoExported lastVideoExported = new LastVideoExported(pathVideoExported, date);
    currentProject.setLastVideoExported(lastVideoExported);
    currentProject.updateDateOfModification(date);
  }

}
