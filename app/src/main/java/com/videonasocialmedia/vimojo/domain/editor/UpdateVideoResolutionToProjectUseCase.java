package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoResolutionToProjectUseCase {

    public UpdateVideoResolutionToProjectUseCase(){

    }

    public void updateResolution(VideoResolution.Resolution resolution, Project currentProject){
        Profile profile = currentProject.getProfile();
        profile.setResolution(resolution);
    }
}
