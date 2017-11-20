package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraPrefToRealmCameraPrefMapper implements Mapper<CameraPreferences, RealmCameraPref> {
  @Override
  public RealmCameraPref map(CameraPreferences cameraPreferences) {
    ResolutionPreference resolutionPreference = cameraPreferences.getResolutionPreference();
    FrameRatePreference frameRatePreference = cameraPreferences.getFrameRatePreference();
    RealmCameraPref realmCameraPref = new RealmCameraPref("RealmCameraPref",
        cameraPreferences.isInterfaceProSelected(),
        cameraPreferences.getResolutionPreference().getResolution(),
        cameraPreferences.getQuality(), cameraPreferences.getFrameRatePreference().getFrameRate(),
        resolutionPreference.isResolutionBack720pSupported(),
        resolutionPreference.isResolutionBack1080pSupported(),
        resolutionPreference.isResolutionBack2160pSupported(),
        resolutionPreference.isResolutionFront720pSupported(),
        resolutionPreference.isResolutionFront1080pSupported(),
        resolutionPreference.isResolutionFront2160pSupported(),
        frameRatePreference.isFrameRate24FpsSupported(),
        frameRatePreference.isFrameRate25FpsSupported(),
        frameRatePreference.isFrameRate30FpsSupported());
    return realmCameraPref;
  }
}
