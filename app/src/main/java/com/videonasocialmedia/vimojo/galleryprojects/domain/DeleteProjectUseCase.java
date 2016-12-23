package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;

/**
 * Created by alvaro on 14/12/16.
 */

public class DeleteProjectUseCase {

  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public void delete(Project project){
    projectRepository.remove(project);
    FileUtils.deleteDirectory(new File(project.getProjectPath()));
  }
}
