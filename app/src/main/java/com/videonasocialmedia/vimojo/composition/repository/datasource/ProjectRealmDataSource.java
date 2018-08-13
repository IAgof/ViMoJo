package com.videonasocialmedia.vimojo.composition.repository.datasource;

/**
 * Created by jliarte on 20/10/16.
 */

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.mapper.ProjectToRealmProjectMapper;
import com.videonasocialmedia.vimojo.composition.repository.datasource.mapper.RealmProjectToProjectMapper;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Realm DataSource for projects. Provide local persistance of Projects using Realm
 * via {@link RealmProject} class.
 */
public class ProjectRealmDataSource implements DataSource<Project> {
  protected Mapper<Project, RealmProject> toRealmProjectMapper;
  protected Mapper<RealmProject, Project> toProjectMapper;
  private TrackRealmDataSource trackDataSource;

  @Inject
  public ProjectRealmDataSource(TrackRealmDataSource trackDataSource) {
    this.trackDataSource = trackDataSource;
    this.toProjectMapper = new RealmProjectToProjectMapper();
    this.toRealmProjectMapper = new ProjectToRealmProjectMapper();
  }

  @Override
  public void add(final Project item) {
    add(Collections.singletonList(item));
  }

  @Override
  public void add(final Iterable<Project> items) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      for (Project item: items) {
        realm.copyToRealm(toRealmProjectMapper.map(item));
      }
    });
  }

  @Override
  public void update(final Project item) {
    item.updateDateOfModification(DateUtils.getDateRightNow());
    Realm instance = Realm.getDefaultInstance();
    instance.executeTransactionAsync(
            realm -> realm.copyToRealmOrUpdate(toRealmProjectMapper.map(item)));
    instance.close();
  }

  public void updateWithDate(final Project item, String date) {
    // TODO(jliarte): 10/08/18 date is not used!!
    Realm.getDefaultInstance().executeTransaction(
            realm -> realm.copyToRealmOrUpdate(toRealmProjectMapper.map(item)));
  }

  @Override
  public void remove(final Project item) {
    removeAllProjectTracks(item);
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmResults<RealmProject> result = realm.where(RealmProject.class).
              equalTo("uuid", item.getUuid()).findAll();
      result.deleteAllFromRealm();
    });
  }

  private void removeAllProjectTracks(Project project) {
    removeMediaTrack(project);
    removeAudioTrack(project, Constants.INDEX_AUDIO_TRACK_MUSIC);
    if (project.hasVoiceOver()) {
      removeAudioTrack(project, Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    }
  }

  private void removeAudioTrack(Project item, int indexAudioTrack) {
    ArrayList<AudioTrack> audioTracks = item.getAudioTracks();
    if (audioTracks != null && audioTracks.size() > 0) {
      // TODO(jliarte): 9/08/18 FIXME: sometimes audioTracks is empty
      Track track = audioTracks.get(indexAudioTrack);
      trackDataSource.remove(track);
    }
  }

  private void removeMediaTrack(Project item) {
    Track videoTrack = item.getMediaTrack();
    trackDataSource.remove(videoTrack);
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Project> query(Specification specification) {
    return null;
  }

  @Override
  public Project getById(String id) {
    Realm realm = Realm.getDefaultInstance();
    RealmProject result = realm.where(RealmProject.class).
            equalTo("uuid", id).findFirst();
    return toProjectMapper.map(result);
  }

  public Project getLastModifiedProject() {
    // TODO(jliarte): 6/07/17 fix No space left on device
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmProject> allRealmProjects = realm.where(RealmProject.class).findAll()
        .sort("lastModification", Sort.DESCENDING);
    RealmProject currentRealmProject = null;
    if (allRealmProjects.size() > 0) {
      currentRealmProject = allRealmProjects.first();
    }

    if (currentRealmProject == null) {
      return null;
      // TODO(jliarte): 22/10/16 the return line throws
      // java.lang.IllegalArgumentException: Null objects cannot be copied from Realm
    }
    return toProjectMapper.map(realm.copyFromRealm(currentRealmProject));
  }

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

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateResolution(Project project, VideoResolution.Resolution videoResolution) {
    project.getProfile().setResolution(videoResolution);
    update(project);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateFrameRate(Project project, VideoFrameRate.FrameRate videoFrameRate) {
    project.getProfile().setFrameRate(videoFrameRate);
    update(project);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void updateQuality(Project project, VideoQuality.Quality videoQuality) {
    project.getProfile().setQuality(videoQuality);
    update(project);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void setWatermarkActivated(Project project, boolean watermarkActivated) {
    project.setWatermarkActivated(watermarkActivated);
    update(project);
  }

  // TODO(jliarte): 11/07/18 this is a use case!
  public void setProjectInfo(Project project, String projectTitle, String projectDescription,
                             List<String> productTypesListSelected) {
    ProjectInfo projectInfo = project.getProjectInfo();
    projectInfo.setTitle(projectTitle);
    projectInfo.setDescription(projectDescription);
    projectInfo.setProductTypeList(productTypesListSelected);
    update(project);
  }

}
