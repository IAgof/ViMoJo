package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;

/**
 * Use case for discarding current project and creating a new one
 */
public class ClearProjectUseCase {
  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public void clearProject(Project project) {
    projectRepository.remove(project);
    cleanTemporalDirectories();
    project.clear();
  }

  private void cleanTemporalDirectories() {
    File intermediateVideoDirectory = new File(Constants.PATH_APP_TEMP_INTERMEDIATE_FILES);
    FileUtils.cleanDirectory(intermediateVideoDirectory);
    // Vimojo versions prior to 14 keeps intermediate files in general tmp path, so we delete
    // just the files there
    File appTmpDirectory = new File(Constants.PATH_APP_TEMP);
    FileUtils.cleanOldVideoIntermediates(appTmpDirectory);
  }
}
