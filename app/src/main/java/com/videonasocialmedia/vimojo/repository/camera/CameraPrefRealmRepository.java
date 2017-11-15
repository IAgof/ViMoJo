package com.videonasocialmedia.vimojo.repository.camera;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraPrefRealmRepository implements CameraPrefRepository {

  protected Mapper<RealmCameraPref, CameraPreferences> toCameraPreferencesMapper;
  protected Mapper<CameraPreferences, RealmCameraPref> toRealmCameraMapper;

  public CameraPrefRealmRepository(){
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
    if(realmResults.size() > 0) {
      return toCameraPreferencesMapper.map(realm.copyFromRealm(realmResults.first()));
    } else {
      RealmCameraPref defaultRealmCameraPref = new RealmCameraPref("cameraPreferenceId",
              Constants.DEFAULT_CAMERA_PREF_INTERFACE_PRO_SELECTED,
              Constants.DEFAULT_CAMERA_PREF_RESOLUTION, Constants.DEFAULT_CAMERA_PREF_QUALITY,
              Constants.DEFAULT_CAMERA_PREF_FRAME_RATE, true, true, false, true, true, false, false,
              false, true);
        return toCameraPreferencesMapper.map(defaultRealmCameraPref);
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
