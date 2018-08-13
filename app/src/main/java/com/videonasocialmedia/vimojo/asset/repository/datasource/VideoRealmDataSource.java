package com.videonasocialmedia.vimojo.asset.repository.datasource;

/**
 * Created by Alejandro on 21/10/16.
 */

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.RealmSpecification;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.composition.repository.datasource.RealmProject;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.RealmVideoToVideoMapper;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.VideoToRealmVideoMapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Realm DataSource for videos. Provide local persistance of {@link Video} using Realm
 * via {@link RealmVideo} class.
 */
public class VideoRealmDataSource implements VideoDataSource {
  protected Mapper<RealmVideo, Video> toVideoMapper;
  protected Mapper<Video, RealmVideo> toRealmVideoMapper;

  @Inject
  public VideoRealmDataSource() {
    this.toVideoMapper = new RealmVideoToVideoMapper();
    this.toRealmVideoMapper = new VideoToRealmVideoMapper();
  }

  @Override
  public List<Video> getAllVideos() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmVideo> realmResults = realm.where(RealmVideo.class).findAll();
    List<Video> videoList = new ArrayList<>();
    for(RealmVideo realmVideo: realmResults) {
      videoList.add(toVideoMapper.map(realm.copyFromRealm(realmVideo)));
    }
    return videoList;
  }

  @Override
  public void add(final Video item) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmVideo realmVideo = toRealmVideoMapper.map(item);
      realm.copyToRealm(realmVideo);
    });
    Realm.getDefaultInstance().close();
  }

  @Override
  public void removeAllVideos() {
    final Realm realm = Realm.getDefaultInstance();

    realm.delete(RealmVideo.class);
  }

  @Override
  public void setSuccessTranscodingVideo(Video video) {
    video.resetNumTriesToExportVideo();
    video.setTranscodingTempFileFinished(true);
    video.setVideoError(null);
    update(video);
  }

  @Override
  public void setErrorTranscodingVideo(Video video, String message) {
    video.setVideoError(message);
    video.setTranscodingTempFileFinished(true);
    update(video);
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
    Realm.getDefaultInstance().executeTransaction(
            realm -> realm.copyToRealmOrUpdate(toRealmVideoMapper.map(item)));
  }

  @Override
  public void remove(final Video item) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmResults<RealmVideo> result = realm.where(RealmVideo.class).
              equalTo("uuid", item.getUuid()).findAll();
      result.deleteAllFromRealm();
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

  @Override
  public Video getById(String id) {
    Realm realm = Realm.getDefaultInstance();
    RealmVideo result = realm.where(RealmVideo.class).
            equalTo("uuid", id).findFirst();
    return toVideoMapper.map(result);
  }

}
