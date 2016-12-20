package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by alvaro on 14/12/16.
 */

public class DuplicateProjectUseCase {

  private ProjectRealmRepository projectRealmRepository = new ProjectRealmRepository();

  public DuplicateProjectUseCase() {

  }

  public void duplicate(Project project) {
    String origPath = project.getProjectPath();
    Project newProject = new Project(project);
    copyFilesToNewProject(origPath, newProject.getProjectPath());
    projectRealmRepository.createProject(newProject);
  }

  private void copyFilesToNewProject(String projectPath, String newProjectPath) {
    try {
      FileUtils.copyDirectory(new File(projectPath), new File(newProjectPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
