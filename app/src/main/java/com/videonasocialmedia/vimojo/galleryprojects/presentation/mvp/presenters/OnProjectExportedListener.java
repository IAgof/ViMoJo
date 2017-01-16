package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;

/**
 * Created by alvaro on 20/12/16.
 */
public interface OnProjectExportedListener {
  void videoExportedNavigateToShareActivity(Project project);
  void exportProject(Project project);
}
