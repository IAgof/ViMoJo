package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {
  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public void loadOrCreateProject(String rootPath, Profile profile) {
    // TODO(jliarte): 22/10/16 we should store current project in other place than Project instance.
    //                This is done for convenience only as we should get rid of all
    //                Project.getInstance calls
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
    }
    // By default project title,
    String projectTitle = DateUtils.getDateRightNow();
    Project currentProject = Project.getInstance(projectTitle, rootPath, profile);
    projectRepository.update(currentProject);
  }

//  //TODO Check user profile, by default 720p 10Mbps FPS25
//  private Profile getDefaultFreeProfile() {
//
//    return Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
//            VideoFrameRate.FrameRate.FPS25);
//  }
}
