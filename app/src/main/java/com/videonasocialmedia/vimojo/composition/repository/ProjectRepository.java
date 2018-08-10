package com.videonasocialmedia.vimojo.composition.repository;

/**
 * Created by jliarte on 20/10/16.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.datasource.CompositionApiDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Repository for providing Projects via repository pattern (will be renamed to Compositions!!)
 *   TODO(jliarte): 11/07/18 rename to CompositionRepository
 *
 * <p>This class handles saving and retrieving Projects from different data sources and merge
 * Projects provided by them for returning results.</p>
 */
public class ProjectRepository extends VimojoRepository<Project> {
  private static final String LOG_TAG = ProjectRepository.class.getSimpleName();
  private final ProjectRealmDataSource projectRealmDataSource;
  private final CompositionApiDataSource compositionApiDataSource;
  private Comparator<Project> dateComparatorDescending = (Comparator<Project>) (left, right) -> {
    return DateUtils.parseStringDate(right.getLastModification())
            .compareTo(DateUtils.parseStringDate(left.getLastModification())); // use your logic
  };

  @Inject
  public ProjectRepository(ProjectRealmDataSource projectRealmDataSource,
                           CompositionApiDataSource compositionApiDataSource) {
    this.projectRealmDataSource = projectRealmDataSource;
    this.compositionApiDataSource = compositionApiDataSource;
  }

  @Override
  public void add(Project item) {
    Log.d(LOG_TAG, "ProjectRepo.add project " + item);
    this.projectRealmDataSource.add(item);
    // TODO(jliarte): 18/07/18 feature toggle this
    // TODO(jliarte): 12/07/18 get success/error on API add and reflect it in local data sources? - sync status/date
    this.compositionApiDataSource.add(item);
  }

  @Override
  public void add(Iterable<Project> items) {
    // TODO(jliarte): 18/07/18 foreach item call this.add
    this.projectRealmDataSource.add(items);
  }

  @Override
  public void update(Project item) {
    this.projectRealmDataSource.update(item);
    this.compositionApiDataSource.update(item);
  }

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

  @Override
  public Project getById(String id)  {
    // TODO(jliarte): 8/08/18 get project details in cascade, set API source, and insert into hashmap
    Project realmProject = this.projectRealmDataSource.getById(id);
    Project apiComposition = this.compositionApiDataSource.getById(id);
    if (realmProject != null && apiComposition != null) {
      // TODO(jliarte): 8/08/18 merge projects by date
      return returnLastModified(realmProject, apiComposition);
    } else {
      if (realmProject != null) return realmProject;
      else return apiComposition;
    }
  }

  private Project returnLastModified(Project realmProject, Project apiComposition) {
    // TODO(jliarte): 10/08/18 should we update the other copy?
    ArrayList<Project> projects = new ArrayList<>(2);
    projects.add(realmProject);
    projects.add(apiComposition);
    return Collections.max(projects, dateComparatorDescending);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateWithDate(Project item, String date) {
    this.projectRealmDataSource.updateWithDate(item, date);
  }

  public Project getLastModifiedProject() {
    return this.projectRealmDataSource.getLastModifiedProject();
  }

  public List<Project> getListProjectsByLastModificationDescending() {
    List<Project> realmProjects = projectRealmDataSource
            .getListProjectsByLastModificationDescending();
    List<Project> apiCompositions = compositionApiDataSource.getListProjectsByLastModificationDescending(); // TODO(jliarte): 27/07/18 change to query by specification?
    return mergeCompositions(realmProjects, apiCompositions);
  }

  private List<Project> mergeCompositions(List<Project> realmProjects,
                                          List<Project> apiCompositions) {
    HashMap<String, Project> compositionHash = new HashMap<>();
    for (Project project : realmProjects) {
      compositionHash.put(project.getUuid(), project);
    }
    for (Project apiComposition : apiCompositions) {
      if (compositionHash.get(apiComposition.getUuid()) != null) {
        compositionHash.put(apiComposition.getUuid(),
                returnLastModified(compositionHash.get(apiComposition.getUuid()), apiComposition));
      } else {
        compositionHash.put(apiComposition.getUuid(), this.getById(apiComposition.getUuid()));
      }
    }
    return getSortedCompositionList(compositionHash);
  }

  private List<Project> getSortedCompositionList(HashMap<String, Project> compositionHash) {
    List<Project> list = new ArrayList<>(compositionHash.values());
    Collections.sort(list, dateComparatorDescending);
    return list;
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateResolution(Project project, VideoResolution.Resolution videoResolution) {
    this.projectRealmDataSource.updateResolution(project, videoResolution);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateFrameRate(Project project, VideoFrameRate.FrameRate videoFrameRate) {
    this.projectRealmDataSource.updateFrameRate(project, videoFrameRate);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateQuality(Project project, VideoQuality.Quality videoQuality) {
    this.projectRealmDataSource.updateQuality(project, videoQuality);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void setWatermarkActivated(Project project, boolean isChecked) {
    this.projectRealmDataSource.setWatermarkActivated(project, isChecked);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void setProjectInfo(Project project, String projectTitle, String projectDescription,
                             List<String> productTypesListSelected) {
    this.projectRealmDataSource.setProjectInfo(project, projectTitle, projectDescription,
            productTypesListSelected);
  }
}