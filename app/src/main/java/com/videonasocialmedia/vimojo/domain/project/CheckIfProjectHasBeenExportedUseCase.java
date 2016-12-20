package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters.OnProjectExportedListener;

/**
 * Created by alvaro on 20/12/16.
 */

public class CheckIfProjectHasBeenExportedUseCase {

  public void compareDate(Project project, OnProjectExportedListener listener) {
    if (project.hasVideoExported()
        && project.getDateLastVideoExported().compareTo(project.getLastModification()) == 0) {
      listener.videoExported(project.getPathLastVideoExported());
    } else{
      listener.exportNewVideo();
    }
  }
}