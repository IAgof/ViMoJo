package com.videonasocialmedia.vimojo.cameraSettings.repository;


import com.videonasocialmedia.vimojo.cameraSettings.repository.RealmCameraPref;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import io.realm.RealmObject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class RealmCameraPrefTest {

  @Test
  public void testRealmMusicExtendsRealmObject() {
    RealmCameraPref realmCameraPref = new RealmCameraPref();
    assertThat(realmCameraPref, CoreMatchers.instanceOf(RealmObject.class));
  }

  @Test
  public void testRealmCameraPrefFields() {

    RealmCameraPref realmCameraPref = new RealmCameraPref();

    realmCameraPref.cameraPreferenceId = "RealmCameraPref";
    realmCameraPref.frameRate = "30 fps";
    realmCameraPref.frameRate24FpsSupported = false;
    realmCameraPref.frameRate25FpsSupported = true;
    realmCameraPref.frameRate30FpsSupported = true;
    realmCameraPref.resolution = "1080p";
    realmCameraPref.resolutionBack720pSupported = true;
    realmCameraPref.resolutionBack1080pSupported = true;
    realmCameraPref.resolutionBack2160pSupported = true;
    realmCameraPref.resolutionFront720pSupported = true;
    realmCameraPref.resolutionFront1080pSupported = true;
    realmCameraPref.resolutionFront2160pSupported = false;
    realmCameraPref.quality = "16 Mbps";

    assertThat(realmCameraPref.cameraPreferenceId, is("RealmCameraPref"));
    assertThat(realmCameraPref.frameRate, is("30 fps"));
    assertThat(realmCameraPref.frameRate24FpsSupported, is(false));
    assertThat(realmCameraPref.frameRate25FpsSupported, is(true));
    assertThat(realmCameraPref.frameRate30FpsSupported, is(true));
    assertThat(realmCameraPref.resolution, is("1080p"));
    assertThat(realmCameraPref.resolutionBack720pSupported, is(true));
    assertThat(realmCameraPref.resolutionBack1080pSupported, is(true));
    assertThat(realmCameraPref.resolutionBack2160pSupported, is(true));
    assertThat(realmCameraPref.resolutionFront720pSupported, is(true));
    assertThat(realmCameraPref.resolutionFront1080pSupported, is(true));
    assertThat(realmCameraPref.resolutionFront2160pSupported, is(false));
    assertThat(realmCameraPref.quality, is("16 Mbps"));

  }

}
