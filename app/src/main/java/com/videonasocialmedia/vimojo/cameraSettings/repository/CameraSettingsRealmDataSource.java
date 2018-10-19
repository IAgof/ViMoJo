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

public class CameraSettingsRealmDataSource implements CameraSettingsDataSource {
  protected Mapper<RealmCameraSettings, CameraSettings> toCameraSettingsMapper;
  protected Mapper<CameraSettings, RealmCameraSettings> toRealmCameraMapper;

  public CameraSettingsRealmDataSource() {
    this.toCameraSettingsMapper = new RealmCameraSettingsToCameraSettingsMapper();
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
      return toCameraSettingsMapper.map(realm.copyFromRealm(realmResults.first()));
    } else {
      //Managed this null in CreateDefaultProjectUseCase.
      return null;
    }
  }

  @Override
  public void setResolutionSettingSupported(CameraSettings cameraSettings,
                                            ResolutionSetting resolutionSetting) {
    cameraSettings.setResolutionSetting(resolutionSetting);
    update(cameraSettings);
  }

  @Override
  public void setFrameRateSettingSupported(CameraSettings cameraSettings,
                                           FrameRateSetting frameRateSetting) {
    cameraSettings.setFrameRateSetting(frameRateSetting);
    update(cameraSettings);
  }

  @Override
  public void setResolutionSetting(CameraSettings cameraSettings, String resolution) {
    cameraSettings.getResolutionSetting().setResolutionSetting(resolution);
    update(cameraSettings);
  }

  @Override
  public void setFrameRateSetting(CameraSettings cameraSettings,
                                  String frameRate) {
    cameraSettings.getFrameRateSetting().setFrameRateSetting(frameRate);
    update(cameraSettings);
  }

  @Override
  public void setQualitySetting(CameraSettings cameraSettings, String quality) {
    cameraSettings.setQuality(quality);
    update(cameraSettings);
  }

  @Override
  public void setInterfaceSelected(CameraSettings cameraSettings, String interfaceSelected) {
    cameraSettings.setInterfaceSelected(interfaceSelected);
    update(cameraSettings);
  }

  @Override
  public void setCameraIdSelected(CameraSettings cameraSettings, int cameraIdSelected) {
    cameraSettings.setCameraIdSelected(cameraIdSelected);
    update(cameraSettings);
  }

  @Override
  public void createCameraSetting(CameraSettings defaultCameraSettings) {
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

  @Override
  public CameraSettings getById(String id) {
    return null;
  }
}
