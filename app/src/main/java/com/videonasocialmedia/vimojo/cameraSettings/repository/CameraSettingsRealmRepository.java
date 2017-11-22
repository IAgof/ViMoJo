package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraSettingsRealmRepository implements CameraSettingsRepository {

  protected Mapper<RealmCameraSettings, CameraSettings> toCameraPreferencesMapper;
  protected Mapper<CameraSettings, RealmCameraSettings> toRealmCameraMapper;

  public CameraSettingsRealmRepository() {
    this.toCameraPreferencesMapper = new RealmCameraSettingsToCameraSettingsMapper();
    this.toRealmCameraMapper = new CameraSettingsToRealmCameraSettingsMapper();
  }

  @Override
  public void update(final CameraSettings item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmCameraMapper.map(item));
      }
    });
  }

  @Override
  public CameraSettings getCameraPreferences() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmCameraSettings> realmResults = realm.where(RealmCameraSettings.class).findAll();
    if (realmResults.size() > 0) {
      return toCameraPreferencesMapper.map(realm.copyFromRealm(realmResults.first()));
    } else {
      //Managed this null in CreateDefaultProjectUseCase.
      return null;
    }
  }

  @Override
  public void setResolutionPreferencesSupported(ResolutionSetting resolutionSetting) {
    CameraSettings cameraSettings = getCameraPreferences();
    cameraSettings.setResolutionSetting(resolutionSetting);
    update(cameraSettings);
  }

  @Override
  public void setFrameRatePreferencesSupported(FrameRateSetting frameRateSetting) {
    CameraSettings cameraSettings = getCameraPreferences();
    cameraSettings.setFrameRatePreferences(frameRateSetting);
    update(cameraSettings);
  }

  @Override
  public void setInterfaceProSelected(boolean interfaceProSelected) {
    CameraSettings cameraSettings = getCameraPreferences();
    cameraSettings.setInterfaceProSelected(interfaceProSelected);
    update(cameraSettings);
  }

  @Override
  public void setResolutionPreference(String resolution) {
    CameraSettings cameraSettings = getCameraPreferences();
    cameraSettings.getResolutionSetting().setResolutionPreference(resolution);
    update(cameraSettings);
  }

  @Override
  public void setFrameRatePreference(String frameRate) {
    CameraSettings cameraSettings = getCameraPreferences();
    cameraSettings.getFrameRateSetting().setFrameRate(frameRate);
    update(cameraSettings);
  }

  @Override
  public void setQualityPreference(String quality) {
    CameraSettings cameraSettings = getCameraPreferences();
    cameraSettings.setQuality(quality);
    update(cameraSettings);
  }

  @Override
  public void createCameraPref(CameraSettings defaultCameraSettings) {
    update(defaultCameraSettings);
  }

  @Override
  public void add(CameraSettings item) {

  }

  @Override
  public void add(Iterable<CameraSettings> items) {

  }

  @Override
  public void remove(CameraSettings item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<CameraSettings> query(Specification specification) {
    return null;
  }
}
