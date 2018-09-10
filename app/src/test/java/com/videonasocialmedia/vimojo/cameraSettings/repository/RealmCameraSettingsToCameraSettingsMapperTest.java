package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Test;

import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 16/11/17.
 */

public class RealmCameraSettingsToCameraSettingsMapperTest {

  private String DEFAULT_CAMERA_SETTING_RESOLUTION =
      ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_720;

  @Test
  public void testMapReturnsCameraSettingsObject() {
    RealmCameraSettings realmCameraSettings = new RealmCameraSettings();
    RealmCameraSettingsToCameraSettingsMapper mapper = new RealmCameraSettingsToCameraSettingsMapper();

    CameraSettings cameraSettings = mapper.map(realmCameraSettings);

    assertThat(cameraSettings, notNullValue());
  }

  @Test
  public void testMapReturnsCameraSettingsWithFieldsMapped() {
    RealmCameraSettings defaultRealmCameraSettings = new RealmCameraSettings("cameraSettingsId",
        Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED,
        DEFAULT_CAMERA_SETTING_RESOLUTION, Constants.DEFAULT_CAMERA_SETTING_QUALITY,
        Constants.DEFAULT_CAMERA_SETTING_FRAME_RATE, true, true, false, true, true, false, false,
        false, true, Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED);
    RealmCameraSettingsToCameraSettingsMapper mapper = new RealmCameraSettingsToCameraSettingsMapper();

    CameraSettings cameraSettings = mapper.map(defaultRealmCameraSettings);

    assertThat(cameraSettings.getInterfaceSelected(),
        is(Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED));
    assertThat(cameraSettings.getQuality(), is(Constants.DEFAULT_CAMERA_SETTING_QUALITY));
    assertThat(cameraSettings.getFrameRateSetting().getFrameRate(),
        is(Constants.DEFAULT_CAMERA_SETTING_FRAME_RATE));
    assertThat(cameraSettings.getResolutionSetting().getResolution(),
        is(DEFAULT_CAMERA_SETTING_RESOLUTION));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
                    .get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID), is(true));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
            .get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_BACK_ID), is(true));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
            .get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_BACK_ID), is(false));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
            .get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID), is(true));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
            .get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID), is(true));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
            .get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID), is(false));
    assertThat(cameraSettings.getFrameRateSetting().getFrameRatesSupportedMap()
            .get(CAMERA_SETTING_FRAME_RATE_24_ID), is(false));
    assertThat(cameraSettings.getFrameRateSetting().getFrameRatesSupportedMap()
            .get(CAMERA_SETTING_FRAME_RATE_25_ID), is(false));
    assertThat(cameraSettings.getFrameRateSetting().getFrameRatesSupportedMap()
            .get(CAMERA_SETTING_FRAME_RATE_30_ID), is(true));
    assertThat(cameraSettings.getCameraIdSelected(),
        is(Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED));
  }
}
