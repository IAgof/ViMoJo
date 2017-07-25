package com.videonasocialmedia.vimojo.importer.repository;

import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by jliarte on 24/07/17.
 */

public class VideoToAdaptRealmRepository implements VideoToAdaptRepository {
  protected Mapper<RealmVideoToAdapt, VideoToAdapt> toVideoToAdaptMapper;
  protected Mapper<VideoToAdapt, RealmVideoToAdapt> toRealmVideoToAdaptMapper;

  public VideoToAdaptRealmRepository() {
    VideoRepository videoRepository = new VideoRealmRepository();
    this.toVideoToAdaptMapper = new RealmVideoToAdaptToVideoToAdaptMapper(videoRepository);
    this.toRealmVideoToAdaptMapper = new VideoToAdaptToRealmVideoToAdaptMapper();
  }

  @Override
  public void add(final VideoToAdapt item) {
    final Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmVideoToAdapt realmVideoToAdapt = toRealmVideoToAdaptMapper.map(item);
        realm.copyToRealm(realmVideoToAdapt);
      }
    });
    realm.close();
  }

  @Override
  public void add(Iterable<VideoToAdapt> items) {
    for (VideoToAdapt videoToAdapt : items) {
      add(videoToAdapt);
    }
  }

  @Override
  public void update(VideoToAdapt item) {

  }

  @Override
  public void remove(final VideoToAdapt item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmResults<RealmVideoToAdapt> result = realm.where(RealmVideoToAdapt.class).
                equalTo("mediaPath", item.getVideo().getMediaPath()).findAll();
        result.deleteAllFromRealm();
      }
    });
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<VideoToAdapt> query(Specification specification) {
    return null;
  }

  @Override
  public int getItemCount() {
    Realm realm = Realm.getDefaultInstance();
    return realm.where(RealmVideoToAdapt.class).findAll().size();
  }

  @Override
  public List<VideoToAdapt> getAllVideos() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmVideoToAdapt> realmResults = realm.where(RealmVideoToAdapt.class).findAll();
    List<VideoToAdapt> videoList = new ArrayList<>();
    for(RealmVideoToAdapt realmVideoToAdapt: realmResults){
      videoList.add(toVideoToAdaptMapper.map(realm.copyFromRealm(realmVideoToAdapt)));
    }
    return videoList;
  }

  @Override
  public VideoToAdapt remove(String mediaPath) {
    VideoToAdapt videoToAdapt = getByMediaPath(mediaPath);
    remove(videoToAdapt);
    return videoToAdapt;
  }

  @Override
  public VideoToAdapt getByMediaPath(String mediaPath) {
    Realm realm = Realm.getDefaultInstance();
    RealmVideoToAdapt result = realm.where(RealmVideoToAdapt.class).
            equalTo("mediaPath", mediaPath).findFirst();
    return toVideoToAdaptMapper.map(result);
  }
}
