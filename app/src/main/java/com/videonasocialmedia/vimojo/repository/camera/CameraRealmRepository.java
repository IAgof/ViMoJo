package com.videonasocialmedia.vimojo.repository.camera;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraRealmRepository implements CameraRepository {

  protected Mapper<RealmCamera, CameraPreferences> toCameraPreferencesMapper;
  protected Mapper<CameraPreferences, RealmCamera> toRealmCameraMapper;

  public CameraRealmRepository(){
    this.toCameraPreferencesMapper = new RealmCameraToCameraMapper();
    this.toRealmCameraMapper = new CameraToRealmCameraMapper();
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
    RealmResults<RealmCamera> realmResults = realm.where(RealmCamera.class).findAll();
    return toCameraPreferencesMapper.map(realm.copyFromRealm(realmResults.first()));
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
