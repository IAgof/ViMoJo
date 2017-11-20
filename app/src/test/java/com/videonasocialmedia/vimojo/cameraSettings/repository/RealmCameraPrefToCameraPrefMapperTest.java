package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.repository.RealmCameraPref;
import com.videonasocialmedia.vimojo.cameraSettings.repository.RealmCameraPrefToCameraPrefMapper;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class RealmCameraPrefToCameraPrefMapperTest {

  @Test
  public void testMapReturnsCameraPrefObject() {
    RealmCameraPref realmCameraPref = new RealmCameraPref();
    RealmCameraPrefToCameraPrefMapper mapper = new RealmCameraPrefToCameraPrefMapper();

    CameraPreferences cameraPreferences = mapper.map(realmCameraPref);

    assertThat(cameraPreferences, notNullValue());
  }

  @Test
  public void testMapReturnsCameraPrefWithFieldsMapped() {
    RealmCameraPref defaultRealmCameraPref = new RealmCameraPref("cameraPreferenceId",
        Constants.DEFAULT_CAMERA_PREF_INTERFACE_PRO_SELECTED,
        Constants.DEFAULT_CAMERA_PREF_RESOLUTION, Constants.DEFAULT_CAMERA_PREF_QUALITY,
        Constants.DEFAULT_CAMERA_PREF_FRAME_RATE, true, true, false, true, true, false, false,
        false, true);
    RealmCameraPrefToCameraPrefMapper mapper = new RealmCameraPrefToCameraPrefMapper();

    CameraPreferences cameraPreferences = mapper.map(defaultRealmCameraPref);

    assertThat(cameraPreferences.isInterfaceProSelected(),
        is(Constants.DEFAULT_CAMERA_PREF_INTERFACE_PRO_SELECTED));
    assertThat(cameraPreferences.getQuality(), is(Constants.DEFAULT_CAMERA_PREF_QUALITY));
    assertThat(cameraPreferences.getFrameRatePreference().getFrameRate(),
        is(Constants.DEFAULT_CAMERA_PREF_FRAME_RATE));
    assertThat(cameraPreferences.getResolutionPreference().getResolution(),
        is(Constants.DEFAULT_CAMERA_PREF_RESOLUTION));
    assertThat(cameraPreferences.getResolutionPreference().isResolutionBack720pSupported(),
        is(true));
    assertThat(cameraPreferences.getResolutionPreference().isResolutionBack1080pSupported(),
        is(true));
    assertThat(cameraPreferences.getResolutionPreference().isResolutionBack2160pSupported(),
        is(false));
    assertThat(cameraPreferences.getResolutionPreference().isResolutionFront720pSupported(),
        is(true));
    assertThat(cameraPreferences.getResolutionPreference().isResolutionFront1080pSupported(),
        is(true));
    assertThat(cameraPreferences.getResolutionPreference().isResolutionFront2160pSupported(),
        is(false));
    assertThat(cameraPreferences.getFrameRatePreference().isFrameRate24FpsSupported(), is(false));
    assertThat(cameraPreferences.getFrameRatePreference().isFrameRate25FpsSupported(), is(false));
    assertThat(cameraPreferences.getFrameRatePreference().isFrameRate30FpsSupported(), is(true));
  }
}
