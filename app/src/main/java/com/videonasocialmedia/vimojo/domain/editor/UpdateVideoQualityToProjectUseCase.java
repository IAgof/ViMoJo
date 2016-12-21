package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoQualityToProjectUseCase {

    private Project currentProject;
    protected ProjectRepository projectRepository = new ProjectRealmRepository();

    public void updateQuality(VideoQuality.Quality quality) {
        currentProject = Project.getInstance(null, null, null);
        currentProject.getProfile().setQuality(quality);
        projectRepository.update(currentProject);
    }
}
