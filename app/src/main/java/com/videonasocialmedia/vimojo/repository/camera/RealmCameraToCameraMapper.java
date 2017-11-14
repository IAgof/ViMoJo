package com.videonasocialmedia.vimojo.repository.camera;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by alvaro on 14/11/17.
 */

public class RealmCameraToCameraMapper implements Mapper<RealmCamera,CameraPreferences> {

  @Override
  public CameraPreferences map(RealmCamera realmCamera) {
    ResolutionPreference resolutionPreference = new ResolutionPreference(realmCamera.resolution,
        realmCamera.resolutionBack720pSupported, realmCamera.resolutionBack1080pSupported,
        realmCamera.resolutionBack2160pSupported, realmCamera.resolutionFront720pSupported,
        realmCamera.resolutionFront1080pSupported, realmCamera.resolutionFront2160pSupported);
    FrameRatePreference frameRatePreference = new FrameRatePreference(realmCamera.frameRate,
        realmCamera.frameRate24FpsSupported, realmCamera.frameRate25FpsSupported,
        realmCamera.frameRate30FpsSupported);
    CameraPreferences cameraPreferences = new CameraPreferences(resolutionPreference,
        frameRatePreference, realmCamera.quality, realmCamera.interfaceProSelected);
    return cameraPreferences;
  }
}
