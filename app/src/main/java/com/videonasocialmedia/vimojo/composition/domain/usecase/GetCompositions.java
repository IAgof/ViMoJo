package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by jliarte on 7/08/18.
 */

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.ReadPolicy;

import java.util.List;

import javax.inject.Inject;

/**
 * Use Case for retrieving a list of {@link Project} from repository.
 */
public class GetCompositions {
  private ProjectRepository projectRepository;

  @Inject
  public GetCompositions(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public List<Project> getListProjectsByLastModificationDescending() {
    return projectRepository.getListProjectsByLastModificationDescending();
  }
}
