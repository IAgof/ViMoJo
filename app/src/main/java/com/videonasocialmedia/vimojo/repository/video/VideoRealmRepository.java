package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.RealmSpecification;
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
  public List<Video> getAllVideos(){
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmVideo> realmResults = realm.where(RealmVideo.class).findAll();
    List<Video> videoList = new ArrayList<>();
    for(RealmVideo realmVideo: realmResults){
      videoList.add(toVideoMapper.map(realm.copyFromRealm(realmVideo)));
    }
    return videoList;
  }

  @Override
  public void add(final Video item) {
    final Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmVideo realmVideo = toRealmVideoMapper.map(item);
        realm.copyToRealm(realmVideo);
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
  public void remove(final Video item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmResults<RealmVideo> result = realm.where(RealmVideo.class).
                equalTo("uuid", item.getUuid()).findAll();
        result.deleteAllFromRealm();
      }
    });
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Video> query(Specification specification) {
    final RealmSpecification realmSpecification = (RealmSpecification) specification;
    final Realm realm = Realm.getDefaultInstance();
    final RealmResults<RealmVideo> realmResults = realmSpecification.toRealmResults(realm);
    final List<Video> videos = new ArrayList<>();
    for (RealmVideo realmVideo : realmResults) {
      videos.add(toVideoMapper.map(realmVideo));
    }
    realm.close();

    return videos;
  }

}
