/*
 * Copyright (C) 2017 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.cameraSettings.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoFrameRateToProjectUseCase {
    private Project currentProject;
    protected ProjectRepository projectRepository;

    /**
     * Default constructor with project repository argument.
     *
     * @param projectRepository the project repository.
     */
    @Inject public UpdateVideoFrameRateToProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void updateFrameRate(VideoFrameRate.FrameRate frameRate) {
        currentProject = Project.getInstance(null, null, null, null);
        currentProject.getProfile().setFrameRate(frameRate);
        projectRepository.update(currentProject);
    }
}
