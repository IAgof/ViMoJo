package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
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
  public void setResolutionPreferencesSupported(ResolutionPreference resolutionPreference) {
    CameraPreferences cameraPreferences = getCameraPreferences();
    cameraPreferences.setResolutionPreference(resolutionPreference);
    update(cameraPreferences);
  }

  @Override
  public void setFrameRatePreferencesSupported(FrameRatePreference frameRatePreference) {
    CameraPreferences cameraPreferences = getCameraPreferences();
    cameraPreferences.setFrameRatePreferences(frameRatePreference);
    update(cameraPreferences);
  }

  @Override
  public void setInterfaceProSelected(boolean interfaceProSelected) {
    CameraPreferences cameraPreferences = getCameraPreferences();
    cameraPreferences.setInterfaceProSelected(interfaceProSelected);
    update(cameraPreferences);
  }

  @Override
  public void setResolutionPreference(String resolution) {
    CameraPreferences cameraPreferences = getCameraPreferences();
    cameraPreferences.getResolutionPreference().setResolutionPreference(resolution);
    update(cameraPreferences);
  }

  @Override
  public void setFrameRatePreference(String frameRate) {
    CameraPreferences cameraPreferences = getCameraPreferences();
    cameraPreferences.getFrameRatePreference().setFrameRatePreference(frameRate);
    update(cameraPreferences);
  }

  @Override
  public void setQualityPreference(String quality) {
    CameraPreferences cameraPreferences = getCameraPreferences();
    cameraPreferences.setQuality(quality);
    update(cameraPreferences);
  }

  @Override
  public void createCameraPref(CameraPreferences defaultCameraPreferences) {
    update(defaultCameraPreferences);
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
