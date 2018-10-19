package com.videonasocialmedia.vimojo.composition.repository;

/**
 * Created by jliarte on 20/10/16.
 */

import android.util.Log;

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.datasource.CompositionApiDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.repository.DataPersistanceType;
import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.repository.ReadPolicy;
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
  private final boolean cloudBackupAvailable;
  private Comparator<Project> dateComparatorDescending = (Comparator<Project>) (left, right) -> {
    return DateUtils.parseStringDate(right.getLastModification())
            .compareTo(DateUtils.parseStringDate(left.getLastModification())); // use your logic
  };

  @Inject
  public ProjectRepository(ProjectRealmDataSource projectRealmDataSource,
                           CompositionApiDataSource compositionApiDataSource,
                           boolean cloudBackupAvailable) {
    this.projectRealmDataSource = projectRealmDataSource;
    this.compositionApiDataSource = compositionApiDataSource;
    this.cloudBackupAvailable = cloudBackupAvailable;
  }

  @Override
  public void add(Project item) {
    Log.d(LOG_TAG, "ProjectRepo.add project " + item);
    this.projectRealmDataSource.add(item);
    // TODO(jliarte): 12/07/18 get success/error on API add and reflect it in local data sources? - sync status/date
    if (cloudBackupAvailable) {
      this.compositionApiDataSource.add(item);
    }
  }

  @Override
  public void add(Iterable<Project> items) {
    // TODO(jliarte): 18/07/18 foreach item call this.add
    this.projectRealmDataSource.add(items);
  }

  @Override
  public void update(Project item) {
    item.updateDateOfModification(DateUtils.getDateRightNow());
    this.projectRealmDataSource.update(item);
    if (cloudBackupAvailable) {
      this.compositionApiDataSource.update(item);
    }
  }

  @Override
  public void remove(Project item, DeletePolicy policy) {
    if (policy.useLocal()) {
      this.projectRealmDataSource.remove(item);
    }
    if (policy.useRemote()) {
      this.compositionApiDataSource.remove(item);
    }
  }

  @Override
  public Project getById(String id, ReadPolicy readPolicy) {
    return getById(id, ReadPolicy.READ_ALL);
  }

  @Override
  public void remove(Project item) {
    remove(item, DeletePolicy.DELETE_ALL);
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
    return Collections.min(projects, dateComparatorDescending);
  }

  public Project getLastModifiedProject() {
    // TODO(jliarte): 23/08/18 getting results from API too?
    return this.projectRealmDataSource.getLastModifiedProject();
  }

  public List<Project> getListProjectsByLastModificationDescending() {
    List<Project> realmProjects = projectRealmDataSource
            .getListProjectsByLastModificationDescending();
    if (!cloudBackupAvailable) {
      return realmProjects;
    }
    List<Project> apiCompositions =
        compositionApiDataSource.getListProjectsByLastModificationDescending(); // TODO(jliarte): 27/07/18 change to query by specification?
    return mergeCompositions(realmProjects, apiCompositions);
  }

  private List<Project> mergeCompositions(List<Project> realmProjects,
                                          List<Project> apiCompositions) {
    HashMap<String, Project> compositionHash = new HashMap<>();
    for (Project project : realmProjects) {
      if (project != null) { // TODO(jliarte): 7/09/18 workarround for fixing local null project NPE - research reason
        compositionHash.put(project.getUuid(), project);
      }
    }
    for (Project apiComposition : apiCompositions) {
      if (compositionHash.get(apiComposition.getUuid()) != null) {
        // (jliarte): 10/08/18 apiComposition has not details in cascade, should call this.getByUuid
        Project project = this.getById(apiComposition.getUuid());
        project.setDataPersistanceType(DataPersistanceType.ALL);
        compositionHash.put(apiComposition.getUuid(), project);
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

}