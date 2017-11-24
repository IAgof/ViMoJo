package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;

import org.junit.Test;

import java.util.HashMap;

import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_FRAME_RATE_30_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class CameraPrefToRealmCameraSettingsMapperTest {

  @Test
  public void testMapReturnsARealmCameraPrefInstance() {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, false);
    ResolutionSetting resolutionSetting = new ResolutionSetting("1080p", resolutionsSupportedMap);
    HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, true);
    FrameRateSetting frameRateSetting = new FrameRateSetting("30 fps", frameRatesSupportedMap);
    String quality = "16 Mbps";
    String interfaceSelected = DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
    CameraSettings cameraSettings = new CameraSettings(resolutionSetting,
            frameRateSetting, quality, interfaceSelected);
    CameraSettingsToRealmCameraSettingsMapper mapper = new CameraSettingsToRealmCameraSettingsMapper();

    RealmCameraSettings realmCameraSettings = mapper.map(cameraSettings);

    assertThat(realmCameraSettings, instanceOf(RealmCameraSettings.class));
  }

  @Test
  public void testMapReturnsCameraPrefObjectWithMappedFields() {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, false);
    ResolutionSetting resolutionSetting = new ResolutionSetting("1080p", resolutionsSupportedMap);
    HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, true);
    FrameRateSetting frameRateSetting = new FrameRateSetting("30 fps", frameRatesSupportedMap);
    String quality = "16 Mbps";
    String interfaceSelected = DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
    CameraSettings cameraSettings = new CameraSettings(resolutionSetting,
            frameRateSetting, quality, interfaceSelected);
    CameraSettingsToRealmCameraSettingsMapper mapper = new CameraSettingsToRealmCameraSettingsMapper();

    RealmCameraSettings realmCameraSettings = mapper.map(cameraSettings);

    assertThat(realmCameraSettings.resolution, is(resolutionSetting.getResolution()));
    assertThat(realmCameraSettings.resolutionBack720pSupported,
        is(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_720_BACK_ID)));
    assertThat(realmCameraSettings.resolutionBack1080pSupported,
        is(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_1080_BACK_ID)));
    assertThat(realmCameraSettings.resolutionBack2160pSupported,
        is(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_2160_BACK_ID)));
    assertThat(realmCameraSettings.resolutionFront720pSupported,
        is((resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_720_FRONT_ID))));
    assertThat(realmCameraSettings.resolutionFront1080pSupported,
        is(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID)));
    assertThat(realmCameraSettings.resolutionFront2160pSupported,
        is(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID)));
    assertThat(realmCameraSettings.frameRate, is(frameRateSetting.getFrameRate()));
    assertThat(realmCameraSettings.frameRate24FpsSupported,
        is(frameRatesSupportedMap.get(CAMERA_SETTING_FRAME_RATE_24_ID)));
    assertThat(realmCameraSettings.frameRate25FpsSupported,
        is(frameRatesSupportedMap.get(CAMERA_SETTING_FRAME_RATE_25_ID)));
    assertThat(realmCameraSettings.frameRate30FpsSupported,
        is(frameRatesSupportedMap.get(CAMERA_SETTING_FRAME_RATE_30_ID)));
    assertThat(realmCameraSettings.quality, is(quality));
    assertThat(realmCameraSettings.interfaceSelected, is(interfaceSelected));
  }
}
