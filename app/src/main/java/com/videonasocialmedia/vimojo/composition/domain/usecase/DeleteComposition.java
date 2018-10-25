package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by alvaro on 14/12/16.
 */

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;

import javax.inject.Inject;

/**
 * Use Case for deleting an existing {@link Project} from repository.
 */
public class DeleteComposition {
  protected ProjectRepository projectRepository;

  @Inject
  public DeleteComposition(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void delete(Project project) {
    projectRepository.remove(project, DeletePolicy.DELETE_ALL);
    cleanLocalProjectDirectory(project);
  }

  public void deleteOnlyLocal(Project project) {
    projectRepository.remove(project, DeletePolicy.LOCAL_ONLY);
    cleanLocalProjectDirectory(project);
  }

  private void cleanLocalProjectDirectory(Project project) {
    FileUtils.deleteDirectory(new File(project.getProjectPath()));
  }
}
