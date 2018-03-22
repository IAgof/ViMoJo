package com.videonasocialmedia.vimojo.domain.project;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;


import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {
  protected ProjectRepository projectRepository;
  private final ProfileRepository profileRepository;
  private final Drawable drawableFadeTransitionVideo;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(
          ProjectRepository projectRepository, ProfileRepository profileRepository) {
    this.projectRepository = projectRepository;
    this.profileRepository = profileRepository;
    drawableFadeTransitionVideo = VimojoApplication.getAppContext()
            .getDrawable(R.drawable.alpha_transition_white);
  }

  public void loadOrCreateProject(Project currentProject, String rootPath, String privatePath,
                                  boolean isWatermarkFeatured) {
    // Default project info
    ProjectInfo projectInfo = new ProjectInfo(DateUtils.getDateRightNow(), "", new ArrayList<>());
    boolean isProjectCreated = false;
    if (currentProject == null) {
      isProjectCreated = true;
      currentProject = new Project(projectInfo, rootPath, privatePath,
          profileRepository.getCurrentProfile());
    }
    currentProject.getVMComposition().setDrawableFadeTransitionVideo(drawableFadeTransitionVideo);
    if ((isProjectCreated && isWatermarkFeatured)) {
      currentProject.setWatermarkActivated(true);
    }
    projectRepository.update(currentProject);
  }

  public void createProject(String rootPath, String privatePath, boolean isWatermarkFeatured) {
    ProjectInfo projectInfo = new ProjectInfo(DateUtils.getDateRightNow(), "", new ArrayList<>());
    Project currentProject = new Project(projectInfo, rootPath, privatePath,
            profileRepository.getCurrentProfile());
    if (isWatermarkFeatured) {
      currentProject.setWatermarkActivated(true);
    }
    projectRepository.update(currentProject);
  }
}
