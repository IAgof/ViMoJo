package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views;


import com.videonasocialmedia.vimojo.model.entities.editor.Project;

/**
 *
 */
public interface RetrieveProjectClickListener {
    void onClick(Project project);
    void onDuplicateProject(Project project);
    void onDeleteProject(Project project);
    void goToEditActivity(Project project);
    void goToShareActivity(Project project);
    void goToDetailActivity();
}
