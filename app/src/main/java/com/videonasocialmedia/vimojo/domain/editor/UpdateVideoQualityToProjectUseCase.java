package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoQualityToProjectUseCase {

    public UpdateVideoQualityToProjectUseCase(){

    }

    public void updateQuality(VideoQuality.Quality quality, Project currentProject){
        Profile profile = currentProject.getProfile();
        profile.setQuality(quality);
    }
}
