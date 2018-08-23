package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by alvaro on 27/02/17.
 */

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;

import javax.inject.Inject;

/**
 * Use case for setting composition watermark state.
 */
public class UpdateCompositionWatermark {
  @Inject
  public UpdateCompositionWatermark() {
  }

  public void updateCompositionWatermark(Project project, boolean watermarkActivated) {
    project.setWatermarkActivated(watermarkActivated);
  }
}
