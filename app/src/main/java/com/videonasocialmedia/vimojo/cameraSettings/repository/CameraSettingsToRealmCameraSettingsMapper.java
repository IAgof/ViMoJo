package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.repository.Mapper;

import java.util.HashMap;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraSettingsToRealmCameraSettingsMapper implements
        Mapper<CameraSettings, RealmCameraSettings> {
  @Override
  public RealmCameraSettings map(CameraSettings cameraSettings) {
    ResolutionSetting resolutionSetting = cameraSettings.getResolutionSetting();
    HashMap<Integer, Boolean> resolutionsSupportedMap =
            resolutionSetting.getResolutionsSupportedMap();
    FrameRateSetting frameRateSetting = cameraSettings.getFrameRateSetting();
    HashMap<Integer, Boolean> frameRateSupportedMap = frameRateSetting.getFrameRatesSupportedMap();
    RealmCameraSettings realmCameraSettings = new RealmCameraSettings("RealmCameraSettings",
        cameraSettings.getInterfaceSelected(),
        cameraSettings.getResolutionSetting().getResolution(),
        cameraSettings.getQuality(), cameraSettings.getFrameRateSetting().getFrameRate(),
        resolutionsSupportedMap.get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID),
        resolutionsSupportedMap.get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_BACK_ID),
        resolutionsSupportedMap.get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_BACK_ID),
        resolutionsSupportedMap.get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID),
        resolutionsSupportedMap.get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID),
        resolutionsSupportedMap.get(ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID),
        frameRateSupportedMap.get(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID),
        frameRateSupportedMap.get(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID),
        frameRateSupportedMap.get(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID),
        cameraSettings.getCameraIdSelected());
    return realmCameraSettings;
  }
}
