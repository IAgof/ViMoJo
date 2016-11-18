package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoFrameRateToProjectUseCase {

    private Project currentProject;
    protected ProjectRepository projectRepository = new ProjectRealmRepository();

    public void updateFrameRate(VideoFrameRate.FrameRate frameRate) {
        currentProject = Project.getInstance(null, null, null);
        currentProject.getProfile().setFrameRate(frameRate);
        projectRepository.update(currentProject);
    }
}
