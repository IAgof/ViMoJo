package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by jliarte on 20/10/16.
 */

public class ProjectRealmRepository implements ProjectRepository {
  protected Mapper<Project, RealmProject> toRealmProjectMapper;
  protected Mapper<RealmProject, Project> toProjectMapper;

  public ProjectRealmRepository() {
    this.toProjectMapper = new RealmProjectToProjectMapper();
    this.toRealmProjectMapper = new ProjectToRealmProjectMapper();
  }

  @Override
  public void add(final Project item) {
    add(Collections.singletonList(item));
  }

  @Override
  public void add(final Iterable<Project> items) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        for (Project item: items) {
          realm.copyToRealm(toRealmProjectMapper.map(item));
        }
      }
    });
  }

  @Override
  public void update(final Project item) {
    item.setLastModification(DateUtils.getDateRightNow());
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmProjectMapper.map(item));
      }
    });
  }

  @Override
  public void updateWithDate(final Project item, String date) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmProjectMapper.map(item));
      }
    });
  }

  @Override
  public void remove(final Project item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmResults<RealmProject> result = realm.where(RealmProject.class).
                equalTo("uuid", item.getUuid()).findAll();
        result.deleteAllFromRealm();
      }
    });
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
    // TODO(jliarte): 6/07/17 fix No space left on device
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmProject> allRealmProjects = realm.where(RealmProject.class).findAll()
        .sort("lastModification", Sort.DESCENDING);
    RealmProject currentRealmProject = null;
    if(allRealmProjects.size() > 0) {
      currentRealmProject = allRealmProjects.first();
    }

    if (currentRealmProject == null) {
      return null;
      // TODO(jliarte): 22/10/16 the return line throws
      // java.lang.IllegalArgumentException: Null objects cannot be copied from Realm
    }
    return toProjectMapper.map(realm.copyFromRealm(currentRealmProject));
  }

  @Override
  public List<Project> getListProjectsByLastModificationDescending() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmProject> allRealmProjects = realm.where(RealmProject.class).findAll()
        .sort("lastModification", Sort.DESCENDING);
    List<Project> projectList = new ArrayList<>();
    for(RealmProject realmProject: allRealmProjects){
      projectList.add(toProjectMapper.map(realm.copyFromRealm(realmProject)));
    }
    return projectList;
  }

  @Override
  public void updateResolution(VideoResolution.Resolution videoResolution) {
    Project project = getCurrentProject();
    project.getProfile().setResolution(videoResolution);
    update(project);
  }

  @Override
  public void updateFrameRate(VideoFrameRate.FrameRate videoFrameRate) {
    Project project = getCurrentProject();
    project.getProfile().setFrameRate(videoFrameRate);
    update(project);
  }

  @Override
  public void updateQuality(VideoQuality.Quality videoQuality) {
    Project project = getCurrentProject();
    project.getProfile().setQuality(videoQuality);
    update(project);
  }

  @Override
  public void setWatermarkActivated(boolean watermarkActivated) {
    Project project = getCurrentProject();
    project.setWatermarkActivated(watermarkActivated);
    update(project);
  }

}
