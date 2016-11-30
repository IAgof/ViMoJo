package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views;


import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import java.util.List;

/**
 *
 */
public interface RetrieveProjectListView {
    void showProjectList(List<Project> projectList);

}
