package com.videonasocialmedia.vimojo.repository.camerapref;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraPrefRealmRepository implements CameraPrefRepository {

  protected Mapper<RealmCameraPref, CameraPreferences> toCameraPreferencesMapper;
  protected Mapper<CameraPreferences, RealmCameraPref> toRealmCameraMapper;

  public CameraPrefRealmRepository() {
    this.toCameraPreferencesMapper = new RealmCameraPrefToCameraPrefMapper();
    this.toRealmCameraMapper = new CameraPrefToRealmCameraPrefMapper();
  }

  @Override
  public void update(final CameraPreferences item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmCameraMapper.map(item));
      }
    });
  }

  @Override
  public CameraPreferences getCameraPreferences() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmCameraPref> realmResults = realm.where(RealmCameraPref.class).findAll();
    if (realmResults.size() > 0) {
      return toCameraPreferencesMapper.map(realm.copyFromRealm(realmResults.first()));
    } else {
      //Managed this null in CreateDefaultProjectUseCase.
      return null;
    }
  }

  @Override
  public void add(CameraPreferences item) {

  }

  @Override
  public void add(Iterable<CameraPreferences> items) {

  }

  @Override
  public void remove(CameraPreferences item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<CameraPreferences> query(Specification specification) {
    return null;
  }
}
