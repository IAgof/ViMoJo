package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoFrameRateToProjectUseCase {

    public UpdateVideoFrameRateToProjectUseCase(){

    }

    public void updateFrameRate(VideoFrameRate.FrameRate frameRate, Project currentProject){
        Profile profile = currentProject.getProfile();
        profile.setFrameRate(frameRate);
    }
}
