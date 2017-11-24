package com.videonasocialmedia.vimojo.cameraSettings.repository;


import org.hamcrest.CoreMatchers;
import org.junit.Test;

import io.realm.RealmObject;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class RealmCameraSettingsTest {

  @Test
  public void testRealmMusicExtendsRealmObject() {
    RealmCameraSettings realmCameraSettings = new RealmCameraSettings();
    assertThat(realmCameraSettings, CoreMatchers.instanceOf(RealmObject.class));
  }

  @Test
  public void testRealmCameraPrefFields() {

    RealmCameraSettings realmCameraSettings = new RealmCameraSettings();

    realmCameraSettings.cameraSettingsId = "RealmCameraSettings";
    realmCameraSettings.frameRate = "30 fps";
    realmCameraSettings.frameRate24FpsSupported = false;
    realmCameraSettings.frameRate25FpsSupported = true;
    realmCameraSettings.frameRate30FpsSupported = true;
    realmCameraSettings.resolution = "1080p";
    realmCameraSettings.resolutionBack720pSupported = true;
    realmCameraSettings.resolutionBack1080pSupported = true;
    realmCameraSettings.resolutionBack2160pSupported = true;
    realmCameraSettings.resolutionFront720pSupported = true;
    realmCameraSettings.resolutionFront1080pSupported = true;
    realmCameraSettings.resolutionFront2160pSupported = false;
    realmCameraSettings.quality = "16 Mbps";
    realmCameraSettings.interfaceSelected = DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;

    assertThat(realmCameraSettings.cameraSettingsId, is("RealmCameraSettings"));
    assertThat(realmCameraSettings.frameRate, is("30 fps"));
    assertThat(realmCameraSettings.frameRate24FpsSupported, is(false));
    assertThat(realmCameraSettings.frameRate25FpsSupported, is(true));
    assertThat(realmCameraSettings.frameRate30FpsSupported, is(true));
    assertThat(realmCameraSettings.resolution, is("1080p"));
    assertThat(realmCameraSettings.resolutionBack720pSupported, is(true));
    assertThat(realmCameraSettings.resolutionBack1080pSupported, is(true));
    assertThat(realmCameraSettings.resolutionBack2160pSupported, is(true));
    assertThat(realmCameraSettings.resolutionFront720pSupported, is(true));
    assertThat(realmCameraSettings.resolutionFront1080pSupported, is(true));
    assertThat(realmCameraSettings.resolutionFront2160pSupported, is(false));
    assertThat(realmCameraSettings.quality, is("16 Mbps"));
    assertThat(realmCameraSettings.interfaceSelected, is(DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED));

  }

}
