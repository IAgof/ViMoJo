package com.videonasocialmedia.vimojo.repository.camera;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by alvaro on 14/11/17.
 */

public class CameraToRealmCameraMapper  implements Mapper<CameraPreferences, RealmCamera> {
  @Override
  public RealmCamera map(CameraPreferences cameraPreferences) {
    ResolutionPreference resolutionPreference = cameraPreferences.getResolutionPreference();
    FrameRatePreference frameRatePreference = cameraPreferences.getFrameRatePreference();
    RealmCamera realmCamera = new RealmCamera("cameraPreferences",
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
    return realmCamera;
  }
}
