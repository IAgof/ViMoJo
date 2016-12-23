package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views;


import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import java.util.List;

/**
 *
 */
public interface GalleryProjectListView {
    void showProjectList(List<Project> projectList);
    void createDefaultProject();
    void navigateTo(Class cls);
    void navigateTo(Class cls, String path);
}
