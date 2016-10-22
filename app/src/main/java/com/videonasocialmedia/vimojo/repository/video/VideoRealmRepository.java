package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.project.RealmProject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Alejandro on 21/10/16.
 */

public class VideoRealmRepository implements VideoRepository {
  protected Mapper<RealmVideo, Video> toVideoMapper;
  protected Mapper<Video, RealmVideo> toRealmVideoMapper;

  public VideoRealmRepository() {
    this.toVideoMapper = new RealmVideoToVideoMapper();
    this.toRealmVideoMapper = new VideoToRealmVideoMapper();
  }

  @Override
  public RealmResults<RealmVideo> getVideos() {
    ArrayList<Video> videoList = new ArrayList<Video>();

    final Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmVideo> realmResults = realm.where(RealmVideo.class).findAll();

    return realmResults;
  }

  @Override
  public void add(final Video item) {
    final Realm realm = Realm.getDefaultInstance();

    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmVideo realmVideo = toRealmVideoMapper.map(item);
        realm.copyToRealm(realmVideo);
//        realmVideo.VIDEO_FOLDER_PATH = item.getMediaPath();
//        realmVideo.fileDuration = item.getFileDuration();
//        realmVideo.tempPath = item.getTempPath();
//        realmVideo.clipText = item.getClipText();
//        realmVideo.clipTextPosition = item.getClipTextPosition();
//        realmVideo.isTextToVideoAdded = item.isTextToVideoAdded();
//        realmVideo.isTrimmedVideo = item.isTrimmedVideo();
//        realmVideo.stopTime = item.getStopTime();
//        realmVideo.startTime = item.getStartTime();
      }
    });

    realm.close();
  }

  @Override
  public void removeAllVideos() {
    final Realm realm = Realm.getDefaultInstance();

    realm.delete(RealmVideo.class);
  }

  @Override
  public void add(Iterable<Video> items) {

  }

  @Override
  public void update(Video item) {
    update(item, null);
  }

  @Override
  public void update(final Video item, final RealmProject project) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmVideoMapper.map(item));
      }
    });
  }

  @Override
  public void remove(Video item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Video> query(Specification specification) {
    return null;
  }
}
