package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.Repository;

import java.util.List;

/**
 * Created by jliarte on 20/10/16.
 */

public interface ProjectRepository extends Repository<Project> {
  Project getCurrentProject();
  List<Project> getListProjects();
  void createProject(Project project);
}
