package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by jliarte on 21/10/16.
 */

public class ProjectToRealmProjectMapper implements Mapper<Project, RealmProject> {
  @Override
  public RealmProject map(Project project) {
    RealmProject realmProject = new RealmProject(project.getTitle(), project.getProjectPath(),
            project.getProfile().getQuality().name(), project.getProfile().getResolution().name());
    if (project.hasMusic()) {
      realmProject.musicTitle = project.getMusic().getMusicTitle();
    }
    return realmProject;
  }
}
