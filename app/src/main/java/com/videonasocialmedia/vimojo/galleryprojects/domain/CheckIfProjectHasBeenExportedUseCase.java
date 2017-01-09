package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.OnProjectExportedListener;

/**
 * Created by alvaro on 20/12/16.
 */

public class CheckIfProjectHasBeenExportedUseCase {

  public void compareDate(Project project, OnProjectExportedListener listener) {
    if (project.hasVideoExported()
        && project.getDateLastVideoExported().compareTo(project.getLastModification()) == 0) {
      listener.videoExportedNavigateToShareActivity(project);
    } else{
      listener.exportProject(project);
    }
  }
}
