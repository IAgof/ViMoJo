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
  public CameraSettings getCameraSettings() {
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
  public void setResolutionSettingSupported(ResolutionSetting resolutionSetting) {
    CameraSettings cameraSettings = getCameraSettings();
    cameraSettings.setResolutionSetting(resolutionSetting);
    update(cameraSettings);
  }

  @Override
  public void setFrameRateSettingSupported(FrameRateSetting frameRateSetting) {
    CameraSettings cameraSettings = getCameraSettings();
    cameraSettings.setFrameRatePreferences(frameRateSetting);
    update(cameraSettings);
  }

  @Override
  public void setResolutionSetting(String resolution) {
    CameraSettings cameraSettings = getCameraSettings();
    cameraSettings.getResolutionSetting().setResolutionSetting(resolution);
    update(cameraSettings);
  }

  @Override
  public void setFrameRateSetting(String frameRate) {
    CameraSettings cameraSettings = getCameraSettings();
    cameraSettings.getFrameRateSetting().setFrameRateSetting(frameRate);
    update(cameraSettings);
  }

  @Override
  public void setQualitySetting(String quality) {
    CameraSettings cameraSettings = getCameraSettings();
    cameraSettings.setQuality(quality);
    update(cameraSettings);
  }

  @Override
  public void setInterfaceSelected(String interfaceSelected) {
    CameraSettings cameraSettings = getCameraSettings();
    cameraSettings.setInterfaceSelected(interfaceSelected);
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
