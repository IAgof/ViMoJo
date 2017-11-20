package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraPrefToRealmCameraPrefMapper;
import com.videonasocialmedia.vimojo.cameraSettings.repository.RealmCameraPref;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class CameraPrefToRealmCameraPrefMapperTest {

  @Test
  public void testMapReturnsARealmCameraPrefInstance() {
    ResolutionPreference resolutionPreference = new ResolutionPreference("1080p", true, true, true,
        true, true, false);
    FrameRatePreference frameRatePreference = new FrameRatePreference("30 fps", false, false, true);
    String quality = "16 Mbps";
    boolean interfaceProSelected = true;
    CameraPreferences cameraPreferences = new CameraPreferences(resolutionPreference,
        frameRatePreference, quality, interfaceProSelected);
    CameraPrefToRealmCameraPrefMapper mapper = new CameraPrefToRealmCameraPrefMapper();

    RealmCameraPref realmCameraPref = mapper.map(cameraPreferences);

    assertThat(realmCameraPref, instanceOf(RealmCameraPref.class));
  }

  @Test
  public void testMapReturnsCameraPrefObjectWithMappedFields() {
    ResolutionPreference resolutionPreference = new ResolutionPreference("1080p", true, true, true,
        true, true, false);
    FrameRatePreference frameRatePreference = new FrameRatePreference("30 fps", false, false, true);
    String quality = "16 Mbps";
    boolean interfaceProSelected = true;
    CameraPreferences cameraPreferences = new CameraPreferences(resolutionPreference,
        frameRatePreference, quality, interfaceProSelected);
    CameraPrefToRealmCameraPrefMapper mapper = new CameraPrefToRealmCameraPrefMapper();

    RealmCameraPref realmCameraPref = mapper.map(cameraPreferences);

    assertThat(realmCameraPref.resolution, is(resolutionPreference.getResolution()));
    assertThat(realmCameraPref.resolutionBack720pSupported,
        is(resolutionPreference.isResolutionBack720pSupported()));
    assertThat(realmCameraPref.resolutionBack1080pSupported,
        is(resolutionPreference.isResolutionBack1080pSupported()));
    assertThat(realmCameraPref.resolutionBack2160pSupported,
        is(resolutionPreference.isResolutionBack2160pSupported()));
    assertThat(realmCameraPref.resolutionFront720pSupported,
        is((resolutionPreference.isResolutionFront720pSupported())));
    assertThat(realmCameraPref.resolutionFront1080pSupported,
        is(resolutionPreference.isResolutionFront1080pSupported()));
    assertThat(realmCameraPref.resolutionFront2160pSupported,
        is(resolutionPreference.isResolutionFront2160pSupported()));
    assertThat(realmCameraPref.frameRate, is(frameRatePreference.getFrameRate()));
    assertThat(realmCameraPref.frameRate24FpsSupported,
        is(frameRatePreference.isFrameRate24FpsSupported()));
    assertThat(realmCameraPref.frameRate25FpsSupported,
        is(frameRatePreference.isFrameRate25FpsSupported()));
    assertThat(realmCameraPref.frameRate30FpsSupported,
        is(frameRatePreference.isFrameRate30FpsSupported()));
    assertThat(realmCameraPref.quality, is(quality));
    assertThat(realmCameraPref.interfaceProSelected, is(interfaceProSelected));
  }
}
