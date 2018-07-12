package com.videonasocialmedia.vimojo.main;

/**
 * Created by jliarte on 20/04/18.
 */

import com.videonasocialmedia.vimojo.composition.domain.model.Project;

/**
 * This interface is used to abstract current Project instance storing and handling from the Application
 */
public interface ProjectInstanceCache {
  // TODO(jliarte): 11/07/18 rename to CutInstanceCache
  Project getCurrentProject();

  void setCurrentProject(Project project);
}
