package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter {
  private final InitAppView view;
  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public InitAppPresenter(InitAppView view) {
    this.view = view;
  }

  public void startLoadingProject(String rootPath) {
    // TODO(jliarte): 22/10/16 we should store current project in other place than Project instance.
    //                This is done for convenience only as we should get rid of all
    //                Project.getInstance calls
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
    }
    //TODO Define project title (by date, by project count, ...)
    Project currentProject = Project.getInstance(Constants.PROJECT_TITLE, rootPath, getDefaultFreeProfile());
    projectRepository.update(currentProject);
  }

  //TODO Check user profile, by default 720p free
  private Profile getDefaultFreeProfile() {
    return Profile.getInstance(Profile.ProfileType.free);
  }

}
