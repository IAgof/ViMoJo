package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.views.InitAppView;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private final InitAppView view;
  protected CreateDefaultProjectUseCase createDefaultProjectUseCase =
          new CreateDefaultProjectUseCase();

  public InitAppPresenter(InitAppView view) {
    this.view = view;
  }

  public void startLoadingProject(String rootPath) {
    createDefaultProjectUseCase.loadOrCreateProject(rootPath);
  }
}
