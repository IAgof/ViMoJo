package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.domain.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectListView;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class RetrieveProjectListPresenter {

    private final SharedPreferences sharedPreferences;
    private final Context context;
    private final CreateDefaultProjectUseCase createDefaultProjectUseCase;
    private List<Project> availableProjects;
    private RetrieveProjectListView retrieveProjectListView;
    private UpdateCurrentProjectUseCase updateCurrentProjectUseCase;
    private ProfileSharedPreferencesRepository profileRepository;

    public RetrieveProjectListPresenter(RetrieveProjectListView retrieveProjectListView,
                                        SharedPreferences sharedPreferences, Context context) {
        this.retrieveProjectListView = retrieveProjectListView;
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        createDefaultProjectUseCase = new CreateDefaultProjectUseCase();
        availableProjects = loadListProjects();
        updateCurrentProjectUseCase = new UpdateCurrentProjectUseCase();

    }
    public List<Project> loadListProjects(){
        //TODO use case
        ProjectRealmRepository projectRealmRepository= new ProjectRealmRepository();
        return projectRealmRepository.getListProjects();
    }

    public void getAvailableProjects() {
        if (availableProjects != null)
            retrieveProjectListView.showProjectList(availableProjects);
    }

    public void duplicateProject(Project project) {
      DuplicateProjectUseCase duplicateProjectUseCase = new DuplicateProjectUseCase();
      duplicateProjectUseCase.duplicate(project);
      //UpdateCurrentProjectUseCase updateCurrentProjectUseCase = new UpdateCurrentProjectUseCase();
      //updateCurrentProjectUseCase.updateLastModifactionProject(project);
    }

    public void deleteProject(Project project) {
      DeleteProjectUseCase deleteProjectUseCase = new DeleteProjectUseCase();
      deleteProjectUseCase.delete(project);
    }

    public void updateProjectList() {
        List<Project> projectList = loadListProjects();
        if (projectList != null && projectList.size() > 0) {
                retrieveProjectListView.showProjectList(projectList);
                return;
        }

        retrieveProjectListView.createDefaultProject();
    }

    public void updateCurrentProject(Project project) {
        updateCurrentProjectUseCase.updateLastModifactionProject(project);
    }

    public void resetProject(String pathApp) {
        profileRepository = new ProfileSharedPreferencesRepository(sharedPreferences, context);
        createDefaultProjectUseCase.loadOrCreateProject(pathApp, profileRepository.getCurrentProfile());
    }

    public void checkNavigationToShare(Project project) {
        //TODO use case
        if(project.hasVideoExported()
            && project.getDateLastVideoExported().compareTo(project.getLastModification()) == 0){
            updateCurrentProjectUseCase.updateLastModifactionProject(project);
            retrieveProjectListView.navigateTo(ShareActivity.class, project.getPathLastVideoExported());
        } else {
            updateCurrentProjectUseCase.updateLastModifactionProject(project);
            retrieveProjectListView.navigateTo(EditActivity.class);
        }
    }
}
