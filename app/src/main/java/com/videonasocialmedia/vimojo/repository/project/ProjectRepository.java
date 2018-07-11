package com.videonasocialmedia.vimojo.repository.project;

/**
 * Created by jliarte on 20/10/16.
 */

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.cut.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;

import java.util.List;

import javax.inject.Inject;

/**
 * Repository for providing Projects via repository pattern (will be renamed to Cuts!!)
 *   TODO(jliarte): 11/07/18 rename to CutRepository
 */
public class ProjectRepository extends VimojoRepository<Project> {
  // TODO(jliarte): 11/07/18 change to ProjectRealmDataSource?
  private final ProjectRealmDataSource projectRealmDataSource;

  @Inject
  public ProjectRepository(ProjectRealmDataSource projectRealmDataSource) {
    this.projectRealmDataSource = projectRealmDataSource;
  }

  @Override
  public void add(Project item) {
    this.projectRealmDataSource.add(item);
  }

  @Override
  public void add(Iterable<Project> items) {
    this.projectRealmDataSource.add(items);
  }

  @Override
  public void update(Project item) {
    this.projectRealmDataSource.update(item);
  }

  /**
   * {@link DataSource#remove(Object)}
   */
  @Override
  public void remove(Project item) {
    this.projectRealmDataSource.remove(item);
  }

  @Override
  public void remove(Specification specification) {
    this.projectRealmDataSource.remove(specification);
  }

  @Override
  public List<Project> query(Specification specification) {
    return this.projectRealmDataSource.query(specification);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateWithDate(Project item, String date) {
    this.projectRealmDataSource.updateWithDate(item, date);
  }

  public Project getLastModifiedProject() {
    return this.projectRealmDataSource.getLastModifiedProject();
  }

  public List<Project> getListProjectsByLastModificationDescending() {
    return projectRealmDataSource.getListProjectsByLastModificationDescending();
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateResolution(Project project, VideoResolution.Resolution videoResolution) {
    this.projectRealmDataSource.updateResolution(project, videoResolution);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateFrameRate(Project project, VideoFrameRate.FrameRate videoFrameRate) {
    this.updateFrameRate(project, videoFrameRate);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateQuality(Project project, VideoQuality.Quality videoQuality) {
    this.projectRealmDataSource.updateQuality(project, videoQuality);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void setWatermarkActivated(Project project, boolean isChecked) {
    this.setWatermarkActivated(project, isChecked);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void setProjectInfo(Project project, String projectTitle, String projectDescription,
                             List<String> productTypesListSelected) {
    this.projectRealmDataSource.setProjectInfo(project, projectTitle, projectDescription,
            productTypesListSelected);
  }
}
