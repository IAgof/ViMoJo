package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.domain.project.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectListView;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class RetrieveProjectListPresenter implements OnProjectExportedListener {

  private List<Project> availableProjects;
  private RetrieveProjectListView retrieveProjectListView;
  private UpdateCurrentProjectUseCase updateCurrentProjectUseCase;
  private ProfileSharedPreferencesRepository profileRepository;
  private SharedPreferences sharedPreferences;

  private DuplicateProjectUseCase duplicateProjectUseCase;
  private DeleteProjectUseCase deleteProjectUseCase;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  private CheckIfProjectHasBeenExportedUseCase checkIfProjectHasBeenExportedUseCaseUseCase;

  public RetrieveProjectListPresenter(RetrieveProjectListView retrieveProjectListView,
                                      SharedPreferences sharedPreferences) {
    this.retrieveProjectListView = retrieveProjectListView;
    this.sharedPreferences = sharedPreferences;
    availableProjects = loadListProjects();
    updateCurrentProjectUseCase = new UpdateCurrentProjectUseCase();
    duplicateProjectUseCase = new DuplicateProjectUseCase();
    deleteProjectUseCase = new DeleteProjectUseCase();
    createDefaultProjectUseCase = new CreateDefaultProjectUseCase();
    checkIfProjectHasBeenExportedUseCaseUseCase = new CheckIfProjectHasBeenExportedUseCase();

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
    duplicateProjectUseCase.duplicate(project);
  }

  public void deleteProject(Project project) {
    deleteProjectUseCase.delete(project);
  }

  public void updateProjectList() {
    List<Project> projectList = loadListProjects();
    if (projectList != null && projectList.size() > 0) {
      retrieveProjectListView.showProjectList(projectList);
    } else {
      createDefaultProject();
    }
  }

  public void createDefaultProject() {
    profileRepository = new ProfileSharedPreferencesRepository(sharedPreferences,
        VimojoApplication.getAppContext());
    createDefaultProjectUseCase.createProject(Constants.PATH_APP,
        profileRepository.getCurrentProfile());
    updateProjectList();
  }

  public void updateCurrentProject(Project project) {
    updateCurrentProjectUseCase.updateLastModifactionProject(project);
  }

  public void checkNavigationToShare(Project project) {
    updateCurrentProjectUseCase.updateLastModifactionProject(project);
    checkIfProjectHasBeenExportedUseCaseUseCase.compareDate(project, this);
  }

  @Override
  public void videoExported(String videoPath) {
    retrieveProjectListView.navigateTo(ShareActivity.class, videoPath);
  }

  @Override
  public void exportNewVideo() {
    //// TODO:(alvaro.martinez) 20/12/16 Launch export process. Provisional, go to editActivity.
    retrieveProjectListView.navigateTo(EditActivity.class);
  }
}