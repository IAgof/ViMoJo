package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;

/**
 * Created by jliarte on 20/10/16.
 */

public class ProjectRealmRepository implements ProjectRepository {
  protected Mapper<Project, RealmProject> toRealmProjectMapper;
  protected Mapper<RealmProject, Project> toProjectMapper;
  protected VideoRepository videoRepository = new VideoRealmRepository();

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
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmProjectMapper.map(item));
      }
    });
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
    Realm realm = Realm.getDefaultInstance();
    RealmProject currentRealmProject = realm.where(RealmProject.class).findFirst();
    if (currentRealmProject == null) {
      return null;
      // TODO(jliarte): 22/10/16 the return line throws
      // java.lang.IllegalArgumentException: Null objects cannot be copied from Realm
    }
    return toProjectMapper.map(realm.copyFromRealm(currentRealmProject));
  }
}
