package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.presentation.mvp.views.InitAppView;

import javax.inject.Inject;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private final InitAppView view;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;

  public InitAppPresenter(InitAppView view,
                          CreateDefaultProjectUseCase createDefaultProjectUseCase) {
    this.view = view;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
  }

  public void startLoadingProject(String rootPath) {
    createDefaultProjectUseCase.loadOrCreateProject(rootPath);
  }
}
