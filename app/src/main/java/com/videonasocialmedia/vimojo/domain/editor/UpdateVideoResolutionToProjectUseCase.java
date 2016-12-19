package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoResolutionToProjectUseCase {

    private Project currentProject;
    private ProjectRepository projectRepository = new ProjectRealmRepository();

    public void updateResolution(VideoResolution.Resolution resolution) {
        currentProject = projectRepository.getCurrentProject();
        currentProject.getProfile().setResolution(resolution);
        projectRepository.update(currentProject);
    }
}
