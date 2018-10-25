package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by jliarte on 21/08/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;

import javax.inject.Inject;

/**
 * Use case for setting composition resolution.
 */
public class SetCompositionResolution {
  @Inject public SetCompositionResolution() {
  }

  public void setResolution(Project project, VideoResolution.Resolution resolution) {
    project.getProfile().setResolution(resolution);
  }
}
