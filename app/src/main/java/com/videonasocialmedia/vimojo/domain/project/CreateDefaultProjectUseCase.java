package com.videonasocialmedia.vimojo.domain.project;

/**
 * Created by jliarte on 23/10/16.
 */

// TODO(jliarte): 20/04/18 delete Drawable dependency
import android.graphics.drawable.Drawable;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Use Case for create project with default settings
 */
public class CreateDefaultProjectUseCase {
  private final ProfileRepository profileRepository;

  /**
   * Default constructor with project repository argument.
   *
   * @param profileRepository the default profile constructor.
   */
  @Inject public CreateDefaultProjectUseCase(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  /**
   * Create a project with default settings. Sets empty {@link ProjectInfo} and default
   * {@link com.videonasocialmedia.videonamediaframework.model.media.Profile}
   *
   * @param rootPath
   * @param privatePath
   * @param isWatermarkFeatured
   * @param drawableFadeTransitionVideo
   * @return created project instance
   */
  public Project createProject(String rootPath, String privatePath, boolean isWatermarkFeatured,
                               Drawable drawableFadeTransitionVideo, boolean isVerticalMode) {
    ProjectInfo projectInfo = new ProjectInfo(DateUtils.getDateRightNow(), "",
            new ArrayList<>());
    Project currentProject = new Project(projectInfo, rootPath, privatePath,
            profileRepository.getCurrentProfile(isVerticalMode));
    if (isWatermarkFeatured) {
      currentProject.setWatermarkActivated(true);
    }
    currentProject.getVMComposition().setDrawableFadeTransitionVideo(drawableFadeTransitionVideo);
    return currentProject;
  }

}
