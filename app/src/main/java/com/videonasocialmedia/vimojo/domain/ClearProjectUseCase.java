package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;

import javax.inject.Inject;

/**
 * Use case for discarding current project and creating a new one
 */
public class ClearProjectUseCase {
  protected ProjectRepository projectRepository;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public ClearProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

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
