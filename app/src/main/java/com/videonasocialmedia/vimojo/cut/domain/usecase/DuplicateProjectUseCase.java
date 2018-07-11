package com.videonasocialmedia.vimojo.cut.domain.usecase;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/12/16.
 */

public class DuplicateProjectUseCase {
  @Inject
  public DuplicateProjectUseCase() {
  }

  public Project duplicate(Project project) throws IllegalItemOnTrack {
    String origPath = project.getProjectPath();
    Project newProject = new Project(project);
    copyFilesToNewProject(origPath, newProject.getProjectPath());
    return newProject;
  }

  private void copyFilesToNewProject(String projectPath, String newProjectPath) {
    try {
      FileUtils.copyDirectory(new File(projectPath), new File(newProjectPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
