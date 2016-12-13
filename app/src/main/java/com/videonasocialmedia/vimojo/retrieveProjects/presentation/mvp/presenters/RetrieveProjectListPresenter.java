package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LoadCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectListView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class RetrieveProjectListPresenter {

    private List<Project> availableProject;
    private List projectList;
    private RetrieveProjectListView retrieveProjectListView;
    private LoadCurrentProjectUseCase loadCurrentProjectUseCase;

    public RetrieveProjectListPresenter(RetrieveProjectListView retrieveProjectListView) {
        this.retrieveProjectListView = retrieveProjectListView;
        availableProject=loadListProject();

    }
    public List<Project> loadListProject(){
        ProjectRealmRepository projectRealmRepository= new ProjectRealmRepository();
        Project project1= new LoadCurrentProjectUseCase(projectRealmRepository).loadCurrentProject();
        Project project2= new LoadCurrentProjectUseCase(projectRealmRepository).loadCurrentProject();
        projectList = new ArrayList();
        projectList.add(project1);
        projectList.add(project2);
        return projectList;
    }

    public void getAvailableMusic() {
        if (availableProject != null)
            retrieveProjectListView.showProjectList(availableProject);
    }
}
