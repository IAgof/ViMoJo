package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.CreateDefaultProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.presentation.mvp.views.InitAppView;

import javax.inject.Inject;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private final InitAppView view;
  @Inject protected CreateDefaultProjectUseCase createDefaultProjectUseCase;

  public InitAppPresenter(InitAppView view) {
    this.view = view;
  }

  public void startLoadingProject(String rootPath, Profile profile) {
    createDefaultProjectUseCase.loadOrCreateProject(rootPath, profile);
  }
}
