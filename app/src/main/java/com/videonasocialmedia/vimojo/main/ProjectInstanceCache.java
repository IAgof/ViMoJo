package com.videonasocialmedia.vimojo.main;

/**
 * Created by jliarte on 20/04/18.
 */

import com.videonasocialmedia.vimojo.model.entities.editor.Project;

/**
 * This interface is used to abstract current Project instance storing and handling from the Application
 */
public interface ProjectInstanceCache {
  Project getCurrentProject();

  void setCurrentProject(Project project);
}
