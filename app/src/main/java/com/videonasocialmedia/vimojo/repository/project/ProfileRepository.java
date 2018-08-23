package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;

/**
 * Created by jliarte on 28/11/17.
 */

public interface ProfileRepository {
  /**
   * Get current profile with video parameters selected by user
   * @return current Profile
   */
  Profile getCurrentProfile(boolean isVerticalMode);
}