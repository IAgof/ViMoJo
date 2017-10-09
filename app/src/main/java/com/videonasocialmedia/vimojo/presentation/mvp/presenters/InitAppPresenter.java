package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;

import javax.inject.Inject;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;

  @Inject
  public InitAppPresenter(CreateDefaultProjectUseCase createDefaultProjectUseCase) {
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
  }

  public void startLoadingProject(String rootPath, String privatePath,
                                  boolean isWatermarkFeatured) {
    createDefaultProjectUseCase.loadOrCreateProject(rootPath, privatePath, isWatermarkFeatured);
  }
}
