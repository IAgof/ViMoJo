/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.upload;

import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by alvaro on 6/6/18.
 */

public class UploadRealmRepository implements UploadRepository {

  protected Mapper<RealmUpload, VideoUpload> toVideoUploadMapper;
  protected Mapper<VideoUpload, RealmUpload> toRealmUploadMapper;

  public UploadRealmRepository() {
    this.toVideoUploadMapper = new RealmUploadToUploadMapper();
    this.toRealmUploadMapper = new VideoUploadToRealmUploadMapper();
  }

  @Override
  public List<VideoUpload> getAllVideosToUpload() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmUpload> realmResults = realm.where(RealmUpload.class).findAll();
    List<VideoUpload> videoUploadList = new ArrayList<>();
    for(RealmUpload realmUpload: realmResults){
      videoUploadList.add(toVideoUploadMapper.map(realm.copyFromRealm(realmUpload)));
    }
    realm.close();
    return videoUploadList;
  }

  @Override
  public VideoUpload getVideoToUploadByUUID(String uuid) {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmUpload> realmResults = realm.where(RealmUpload.class).findAll();
    for(RealmUpload realmUpload: realmResults){
      if(realmUpload.uuid.equals(uuid)) {
        return toVideoUploadMapper.map(realm.copyFromRealm(realmUpload));
      }
    }
    realm.close();
    return null;
  }

  @Override
  public void add(VideoUpload item) {
    add(Collections.singletonList(item));
  }

  @Override
  public void add(Iterable<VideoUpload> items) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        for (VideoUpload item: items) {
          realm.copyToRealm(toRealmUploadMapper.map(item));
        }
      }
    });
    realm.close();
  }

  @Override
  public void update(VideoUpload item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmUploadMapper.map(item));
      }
    });
    realm.close();
  }

  @Override
  public void remove(VideoUpload item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmResults<RealmUpload> result = realm.where(RealmUpload.class).
            equalTo("uuid", item.getUuid()).findAll();
        result.deleteAllFromRealm();
      }
    });
    realm.close();
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public void removeAllVideosToUpload() {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmResults<RealmUpload> result = realm.where(RealmUpload.class).findAll();
        result.deleteAllFromRealm();
      }
    });
    realm.close();
  }

  @Override
  public List<VideoUpload> query(Specification specification) {
    return null;
  }
}
