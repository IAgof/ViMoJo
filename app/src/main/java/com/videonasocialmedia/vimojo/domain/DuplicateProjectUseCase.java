package com.videonasocialmedia.vimojo.domain;

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

  protected ProjectRealmRepository projectRealmRepository = new ProjectRealmRepository();

  public DuplicateProjectUseCase() {

  }

  public void duplicate(Project project) {
    Project newProject = project;
    String newUuid = UUID.randomUUID().toString();
    newProject.setUuid(newUuid);
    String newProjectPath = new File(project.getProjectPath()).getParent() + File.separator + newUuid;
    newProject.setProjectPath(newProjectPath);
    newProject.createFolder(newProjectPath);
    copyFilesToNewProject(project.getProjectPath(), newProjectPath);
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
