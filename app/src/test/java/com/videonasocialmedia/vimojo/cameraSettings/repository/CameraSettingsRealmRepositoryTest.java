package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_FRAME_RATE_24;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_FRAME_RATE_30_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_QUALITY_50;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_720;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_SETTING_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_FRAME_RATE;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTING_RESOLUTION;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;

/**
 * Created by alvaro on 16/11/17.
 */

public class CameraSettingsRealmRepositoryTest {

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void cameraSettingsRealmRepositoryConstructorSetsMappers() {
    CameraSettingsRealmRepository repository = new CameraSettingsRealmRepository();

    assertThat(repository.toCameraPreferencesMapper, notNullValue());
    assertThat(repository.toRealmCameraMapper, notNullValue());
  }

  @Test
  public void setResolutionUpdateCameraSettings() {
    CameraSettingsRepository repo = Mockito.spy(new CameraSettingsRealmRepository());
    CameraSettings cameraSettings = getCameraSettings();
    String resolution = CAMERA_SETTING_RESOLUTION_720;
    Mockito.doNothing().when(repo).update(any(CameraSettings.class));
    assertThat(cameraSettings.getResolutionSettingValue(), not(resolution));

    repo.setResolutionSetting(cameraSettings, resolution);

    assertThat(cameraSettings.getResolutionSettingValue(), is(resolution));
  }

  @Test
  public void setQualityUpdateCameraSettings() {
    CameraSettingsRepository repo = Mockito.spy(new CameraSettingsRealmRepository());
    CameraSettings cameraSettings = getCameraSettings();
    String quality = CAMERA_SETTING_QUALITY_50;
    Mockito.doNothing().when(repo).update(any(CameraSettings.class));
    assertThat(cameraSettings.getQuality(), not(quality));

    repo.setQualitySetting(cameraSettings, quality);

    assertThat(cameraSettings.getQuality(), is(quality));
  }

  @Test
  public void setFrameRateUpdateCameraSettings() {
    CameraSettingsRepository repo = Mockito.spy(new CameraSettingsRealmRepository());
    CameraSettings cameraSettings = getCameraSettings();
    String frameRate = CAMERA_SETTING_FRAME_RATE_24;
    Mockito.doNothing().when(repo).update(any(CameraSettings.class));
    assertThat(cameraSettings.getFrameRateSettingValue(), not(frameRate));

    repo.setFrameRateSetting(cameraSettings, frameRate);

    assertThat(cameraSettings.getFrameRateSettingValue(), is(frameRate));
  }

  @Test
  public void setInterfaceSelectedUpdateCameraSettings() {
    CameraSettingsRepository repo = Mockito.spy(new CameraSettingsRealmRepository());
    CameraSettings cameraSettings = getCameraSettings();
    String interfaceSelected = Constants.CAMERA_SETTING_INTERFACE_BASIC;
    Mockito.doNothing().when(repo).update(any(CameraSettings.class));
    assertThat(cameraSettings.getInterfaceSelected(), not(interfaceSelected));

    repo.setInterfaceSelected(cameraSettings, interfaceSelected);

    assertThat(cameraSettings.getInterfaceSelected(), is(interfaceSelected));
  }

  @Test
  public void setFrameRateSupportedValuesUpdateCameraSettings() {
    CameraSettingsRepository repo = Mockito.spy(new CameraSettingsRealmRepository());
    CameraSettings cameraSettings = getCameraSettings();
    boolean frameRate24Supported = true;
    boolean frameRate25Supported = true;
    boolean frameRate30Supported = true;
    HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, frameRate24Supported);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, frameRate25Supported);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, frameRate30Supported);
    FrameRateSetting frameRateSettingSupportedValues =
        new FrameRateSetting(DEFAULT_CAMERA_SETTING_FRAME_RATE, frameRatesSupportedMap);
    Mockito.doNothing().when(repo).update(any(CameraSettings.class));
    assertThat(cameraSettings.getFrameRateSetting().getFrameRatesSupportedMap()
        .get(CAMERA_SETTING_FRAME_RATE_24_ID), not(frameRate24Supported));

    repo.setFrameRateSettingSupported(cameraSettings, frameRateSettingSupportedValues);

    assertThat(cameraSettings.getFrameRateSetting().getFrameRatesSupportedMap()
        .get(CAMERA_SETTING_FRAME_RATE_24_ID), is(frameRate24Supported));
    assertThat(cameraSettings.getFrameRateSetting().getFrameRatesSupportedMap()
        .get(CAMERA_SETTING_FRAME_RATE_25_ID), is(frameRate25Supported));
    assertThat(cameraSettings.getFrameRateSetting().getFrameRatesSupportedMap()
        .get(CAMERA_SETTING_FRAME_RATE_30_ID), is(frameRate30Supported));

  }

  @Test
  public void setResolutionSupportedValuesUpdateCameraSettings() {
    CameraSettingsRepository repo = Mockito.spy(new CameraSettingsRealmRepository());
    CameraSettings cameraSettings = getCameraSettings();
    boolean resolutionBack720Supported = true;
    boolean resolutionBack1080Supported = true;
    boolean resolutionBack4kSupported = true;
    boolean resolutionFront720Supported = true;
    boolean resolutionFront1080Supported = true;
    boolean resolutionFront4kSupported = true;
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, resolutionBack720Supported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, resolutionBack1080Supported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, resolutionBack4kSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, resolutionFront720Supported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, resolutionFront1080Supported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, resolutionFront4kSupported);
    ResolutionSetting resolutionSettingSupportedValues =
        new ResolutionSetting(DEFAULT_CAMERA_SETTING_RESOLUTION, resolutionsSupportedMap);
    Mockito.doNothing().when(repo).update(any(CameraSettings.class));

    repo.setResolutionSettingSupported(cameraSettings, resolutionSettingSupportedValues);

    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
        .get(CAMERA_SETTING_RESOLUTION_720_BACK_ID), is(resolutionBack720Supported));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
        .get(CAMERA_SETTING_RESOLUTION_1080_BACK_ID), is(resolutionBack1080Supported));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
        .get(CAMERA_SETTING_RESOLUTION_2160_BACK_ID), is(resolutionBack4kSupported));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
        .get(CAMERA_SETTING_RESOLUTION_720_FRONT_ID), is(resolutionFront720Supported));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
        .get(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID), is(resolutionFront1080Supported));
    assertThat(cameraSettings.getResolutionSetting().getResolutionsSupportedMap()
        .get(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID), is(resolutionFront4kSupported));
  }

  private CameraSettings getCameraSettings() {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, false);
    ResolutionSetting resolutionSetting = new ResolutionSetting(DEFAULT_CAMERA_SETTING_RESOLUTION,
        resolutionsSupportedMap);
    HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, false);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, true);
    FrameRateSetting frameRateSetting = new FrameRateSetting("30 fps", frameRatesSupportedMap);
    String quality = "16 Mbps";
    String interfaceSelected = DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
    return new CameraSettings(resolutionSetting,
        frameRateSetting, quality, interfaceSelected);
  }

}
