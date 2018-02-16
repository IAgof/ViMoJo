package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views;


import com.videonasocialmedia.vimojo.model.entities.editor.Project;

/**
 * Interface gallery project
 */
public interface GalleryProjectClickListener {
  void onClick(Project project);

  void onDuplicateProject(Project project);

  void onDeleteProject(Project project);

  void goToEditActivity(Project project);

  void goToShareActivity(Project project);

  void goToDetailActivity(Project project);

}
