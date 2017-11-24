package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.repository.Mapper;

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

/**
 * Created by alvaro on 14/11/17.
 */

public class RealmCameraSettingsToCameraSettingsMapper implements
        Mapper<RealmCameraSettings, CameraSettings> {

  @Override
  public CameraSettings map(RealmCameraSettings realmCameraSettings) {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID,
            realmCameraSettings.resolutionBack720pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID,
            realmCameraSettings.resolutionBack1080pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID,
            realmCameraSettings.resolutionBack2160pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID,
            realmCameraSettings.resolutionFront720pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID,
            realmCameraSettings.resolutionFront1080pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID,
            realmCameraSettings.resolutionFront2160pSupported);
    ResolutionSetting resolutionSetting = new ResolutionSetting(realmCameraSettings.resolution,
        resolutionsSupportedMap);
    HashMap<Integer, Boolean> frameRatesSupportedMap = new HashMap<>();
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, realmCameraSettings.frameRate24FpsSupported);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, realmCameraSettings.frameRate25FpsSupported);
    frameRatesSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, realmCameraSettings.frameRate30FpsSupported);
    FrameRateSetting frameRateSetting = new FrameRateSetting(realmCameraSettings.frameRate,
        frameRatesSupportedMap);
    CameraSettings cameraSettings = new CameraSettings(resolutionSetting,
            frameRateSetting, realmCameraSettings.quality, realmCameraSettings.interfaceSelected);
    return cameraSettings;
  }
}
