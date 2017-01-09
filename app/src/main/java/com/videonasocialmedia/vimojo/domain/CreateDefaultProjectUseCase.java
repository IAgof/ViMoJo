package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.inject.Inject;

/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {
  protected ProjectRepository projectRepository;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void loadOrCreateProject(String rootPath, Profile profile) {
    // TODO(jliarte): 22/10/16 we should store current project in other place than Project instance.
    //                This is done for convenience only as we should get rid of all
    //                Project.getInstance calls
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
    }
    //TODO Define project title (by date, by project count, ...)
    Project currentProject = Project.getInstance(Constants.PROJECT_TITLE, rootPath, profile);
    projectRepository.update(currentProject);
  }

//  //TODO Check user profile, by default 720p 10Mbps FPS25
//  private Profile getDefaultFreeProfile() {
//
//    return Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
//            VideoFrameRate.FrameRate.FPS25);
//  }
}