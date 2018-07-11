package com.videonasocialmedia.vimojo.importer.repository;

import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.video.datasource.VideoRealmDataSource;
import com.videonasocialmedia.vimojo.repository.video.VideoDataSource;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by jliarte on 24/07/17.
 */

public class VideoToAdaptRealmDataSource implements VideoToAdaptDataSource {
  protected Mapper<RealmVideoToAdapt, VideoToAdapt> toVideoToAdaptMapper;
  protected Mapper<VideoToAdapt, RealmVideoToAdapt> toRealmVideoToAdaptMapper;
  private VideoToAdaptMemoryDataSource cache;

  public VideoToAdaptRealmDataSource() {
    VideoDataSource videoRepository = new VideoRealmDataSource();
    this.toVideoToAdaptMapper = new RealmVideoToAdaptToVideoToAdaptMapper(videoRepository);
    this.toRealmVideoToAdaptMapper = new VideoToAdaptToRealmVideoToAdaptMapper();
    this.cache = new VideoToAdaptMemoryDataSource();
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
    cache.add(item);
  }

  @Override
  public void add(Iterable<VideoToAdapt> items) {
    for (VideoToAdapt videoToAdapt : items) {
      add(videoToAdapt);
    }
  }

  @Override
  public void update(final VideoToAdapt item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmVideoToAdaptMapper.map(item));
      }
    });
    cache.update(item);
  }

  @Override
  public void remove(final VideoToAdapt item) {
    if (item.getVideo() != null) {
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
    cache.remove(item);
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
    replaceCachedItems(videoList);
    return videoList;
  }

  private void replaceCachedItems(List<VideoToAdapt> videoList) {
    int index = 0;
    while (index < videoList.size()) {
      VideoToAdapt retrievedItem = videoList.get(index);
      if (retrievedItem.getVideo() == null) {
        remove(retrievedItem);
      } else {
        VideoToAdapt cachedItem = cache.getByMediaPath(retrievedItem.getVideo().getMediaPath());
        if (cachedItem != null) {
          videoList.set(index, cachedItem);
        }
      }
      index++;
    }
  }

  @Override
  public VideoToAdapt remove(String mediaPath) {
    VideoToAdapt videoToAdapt = getByMediaPath(mediaPath);
    if (videoToAdapt != null) {
      remove(videoToAdapt);
    }
    cache.remove(mediaPath);
    return videoToAdapt;
  }

  @Override
  public VideoToAdapt getByMediaPath(String mediaPath) {
    if (cache.getByMediaPath(mediaPath) != null) {
      return cache.getByMediaPath(mediaPath);
    }
    Realm realm = Realm.getDefaultInstance();
    RealmVideoToAdapt result = realm.where(RealmVideoToAdapt.class).
            equalTo("mediaPath", mediaPath).findFirst();
    return toVideoToAdaptMapper.map(result);
  }
}
