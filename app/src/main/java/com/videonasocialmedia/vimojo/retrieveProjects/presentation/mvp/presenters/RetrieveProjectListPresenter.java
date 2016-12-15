package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectListView;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class RetrieveProjectListPresenter {

  private List<Project> availableProjects;
  private RetrieveProjectListView retrieveProjectListView;
  private UpdateCurrentProjectUseCase updateCurrentProjectUseCase;

  public RetrieveProjectListPresenter(RetrieveProjectListView retrieveProjectListView) {
    this.retrieveProjectListView = retrieveProjectListView;
    availableProjects = loadListProjects();
    updateCurrentProjectUseCase = new UpdateCurrentProjectUseCase();

  }

  public List<Project> loadListProjects() {
    ProjectRealmRepository projectRealmRepository = new ProjectRealmRepository();
    return projectRealmRepository.getListProjects();
  }

  public void getAvailableProjects() {
    if (availableProjects != null)
      retrieveProjectListView.showProjectList(availableProjects);
  }

  public void duplicateProject(Project project) {
    DuplicateProjectUseCase duplicateProjectUseCase = new DuplicateProjectUseCase();
    duplicateProjectUseCase.duplicate(project);
  }

  public void deleteProject(Project project) {
    DeleteProjectUseCase deleteProjectUseCase = new DeleteProjectUseCase();
    deleteProjectUseCase.delete(project);
  }

  public void updateProjectList() {
    List<Project> projectList = loadListProjects();
    if (projectList != null && projectList.size() > 0) {
      retrieveProjectListView.showProjectList(projectList);
    } else {
      retrieveProjectListView.navigateTo(EditActivity.class);
    }
  }

  public void updateCurrentProject(Project project) {
    updateCurrentProjectUseCase.updateLastModifactionProject(project);
  }

  public void checkNavigationToShare(Project project) {
    updateCurrentProjectUseCase.updateLastModifactionProject(project);
    if (project.hasVideoExported()
        && project.getDateLastVideoExported().compareTo(project.getLastModification()) == 0) {
      retrieveProjectListView.navigateTo(ShareActivity.class, project.getPathLastVideoExported());
    } else {
      retrieveProjectListView.navigateTo(EditActivity.class);
    }
  }
}
