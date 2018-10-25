package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by jliarte on 21/08/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;

import javax.inject.Inject;

/**
 * Use case for setting composition quality
 */
public class SetCompositionQuality {
  @Inject public SetCompositionQuality() {
  }

  public void setQuality(Project project, VideoQuality.Quality videoQuality) {
    project.getProfile().setQuality(videoQuality);
  }
}
