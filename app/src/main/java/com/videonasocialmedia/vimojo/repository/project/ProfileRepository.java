package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;

/**
 * Created by alvaro on 24/10/16.
 */

public interface ProfileRepository {
    /**
     * Get current profile with video parameters selected by user
     * @return current Profile
     */
    Profile getCurrentProfile();
}
