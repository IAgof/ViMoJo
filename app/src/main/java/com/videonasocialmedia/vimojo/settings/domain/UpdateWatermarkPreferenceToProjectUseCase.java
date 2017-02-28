package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.io.File;

/**
 * Created by alvaro on 27/02/17.
 */

public class UpdateWatermarkPreferenceToProjectUseCase {

  private Project currentProject;
  private ProjectRepository projectRepository;

  public UpdateWatermarkPreferenceToProjectUseCase(ProjectRepository projectRepository){
    this.projectRepository = projectRepository;
    currentProject = Project.getInstance(null,null,null);
  }

  public void setWatermarkActivated(boolean data) {
    currentProject.setWatermarkActivated(data);
    projectRepository.update(currentProject);
  }

  public boolean isWatermarkResourceDownloaded(String rootPath){
    File f = new File(currentProject.getResourceWatermarkFilePath(rootPath));
    return f.exists();
  }

}
