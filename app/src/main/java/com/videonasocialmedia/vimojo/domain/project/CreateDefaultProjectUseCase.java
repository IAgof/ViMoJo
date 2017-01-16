package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import javax.inject.Inject;

/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {

  protected ProfileRepository profileRepository;
  protected ProjectRepository projectRepository;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(ProjectRepository projectRepository, ProfileRepository
                                             profileRepository) {
    this.projectRepository = projectRepository;
    this.profileRepository = profileRepository;
  }

  public void loadOrCreateProject(String rootPath) {

    // By default project title,
    String projectTitle = DateUtils.getDateRightNow();
    // TODO(jliarte): 22/10/16 we should store current project in other place than Project instance.
    //                This is done for convenience only as we should get rid of all
    //                Project.getInstance calls
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
    }

    Project currentProject = Project.getInstance(projectTitle, rootPath, profileRepository.getCurrentProfile());
    projectRepository.update(currentProject);
  }

  public void createProject(String rootPath){
    String projectTitle = DateUtils.getDateRightNow();
    Project currentProject = new Project(projectTitle,rootPath, profileRepository.getCurrentProfile());
    Project.INSTANCE = currentProject;
    projectRepository.update(currentProject);
  }

}
