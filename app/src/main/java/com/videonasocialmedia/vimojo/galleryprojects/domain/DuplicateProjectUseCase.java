package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/12/16.
 */

public class DuplicateProjectUseCase {

  protected ProjectRepository projectRepository;

  @Inject
  public DuplicateProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void duplicate(Project project) throws IllegalItemOnTrack {
    String origPath = project.getProjectPath();
    Project newProject = new Project(project);
    copyFilesToNewProject(origPath, newProject.getProjectPath());
    projectRepository.add(newProject);
  }

  private void copyFilesToNewProject(String projectPath, String newProjectPath) {
    try {
      FileUtils.copyDirectory(new File(projectPath), new File(newProjectPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
