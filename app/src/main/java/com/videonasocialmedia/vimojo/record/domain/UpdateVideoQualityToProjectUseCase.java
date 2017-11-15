package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoQualityToProjectUseCase {

    private Project currentProject;
    protected ProjectRepository projectRepository;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public UpdateVideoQualityToProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void updateQuality(VideoQuality.Quality quality) {
        currentProject = Project.getInstance(null, null, null, null);
        currentProject.getProfile().setQuality(quality);
        projectRepository.update(currentProject);
    }
}
