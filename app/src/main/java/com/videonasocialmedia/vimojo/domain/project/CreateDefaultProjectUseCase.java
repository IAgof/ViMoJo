package com.videonasocialmedia.vimojo.domain.project;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;


import javax.inject.Inject;

/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {

  protected ProfileRepository profileRepository;
  protected ProjectRepository projectRepository;
  protected TrackRepository trackRepository;
  private final Drawable drawableFadeTransitionVideo;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(ProjectRepository projectRepository, ProfileRepository
                                             profileRepository, TrackRepository trackRepository) {
    this.projectRepository = projectRepository;
    this.profileRepository = profileRepository;
    this.trackRepository = trackRepository;
    drawableFadeTransitionVideo = VimojoApplication.getAppContext()
            .getDrawable(R.drawable.alpha_transition_white);
  }

  public void loadOrCreateProject(String rootPath, boolean isWatermarkFeatured) {

    // By default project title,
    String projectTitle = DateUtils.getDateRightNow();
    // TODO(jliarte): 22/10/16 we should store current project in other place than Project instance.
    //                This is done for convenience only as we should get rid of all
    //                Project.getInstance calls
    boolean isProjectCreated = false;
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
      isProjectCreated = true;
    }

    Project currentProject = Project.getInstance(projectTitle, rootPath,
        profileRepository.getCurrentProfile());
    currentProject.getVMComposition().setDrawableFadeTransitionVideo(drawableFadeTransitionVideo);
    if ((isProjectCreated && isWatermarkFeatured) || areWeIntoFlavorVimojo()) {
      currentProject.setWatermarkActivated(true);
    }
    projectRepository.update(currentProject);
  }

  private boolean areWeIntoFlavorVimojo() {
    return BuildConfig.FLAVOR.compareTo(Constants.FLAVOR_VIMOJO) == 0;
  }

  public void createProject(String rootPath, boolean isWatermarkFeatured) {
    String projectTitle = DateUtils.getDateRightNow();
    Project currentProject = new Project(projectTitle, rootPath,
        profileRepository.getCurrentProfile());
    if (isWatermarkFeatured || areWeIntoFlavorVimojo()) {
      currentProject.setWatermarkActivated(true);
    }
    Project.INSTANCE = currentProject;
    projectRepository.update(currentProject);
  }

}
