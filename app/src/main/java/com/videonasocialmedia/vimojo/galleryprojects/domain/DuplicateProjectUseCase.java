package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by alvaro on 14/12/16.
 */

public class DuplicateProjectUseCase {

  private ProjectRepository projectRepository = new ProjectRealmRepository();

  public void duplicate(Project project) {
    String origPath = project.getProjectPath();
    Project newProject = new Project(project);
    copyFilesToNewProject(origPath, newProject.getProjectPath());
    projectRepository.update(newProject);
  }

  private void copyFilesToNewProject(String projectPath, String newProjectPath) {
    try {
      FileUtils.copyDirectory(new File(projectPath), new File(newProjectPath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
