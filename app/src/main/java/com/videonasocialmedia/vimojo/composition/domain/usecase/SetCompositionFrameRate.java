package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by jliarte on 21/08/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;

import javax.inject.Inject;

/**
 * Use case for setting composition Frame Rate
 */
public class SetCompositionFrameRate {
  @Inject public SetCompositionFrameRate() {
  }

  public void updateFrameRate(Project project, VideoFrameRate.FrameRate frameRate) {
    project.getProfile().setFrameRate(frameRate);
  }
}
