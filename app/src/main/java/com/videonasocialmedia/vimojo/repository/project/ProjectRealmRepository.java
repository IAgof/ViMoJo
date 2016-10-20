package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.List;

/**
 * Created by jliarte on 20/10/16.
 */

public class ProjectRealmRepository implements ProjectRepository {
  private Mapper<Project, ProjectRealm> toProjectMapper;

  @Override
  public void add(Project item) {

  }

  @Override
  public void add(Iterable<Project> items) {

  }

  @Override
  public void update(Project item) {

  }

  @Override
  public void remove(Project item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Project> query(Specification specification) {
    return null;
  }

  @Override
  public Project getCurrentProject() {
    return Project.getInstance(null, null, null);
  }
}
