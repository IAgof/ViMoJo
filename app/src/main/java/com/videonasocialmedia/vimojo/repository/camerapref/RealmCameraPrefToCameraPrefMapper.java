package com.videonasocialmedia.vimojo.repository.camerapref;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by alvaro on 14/11/17.
 */

public class RealmCameraPrefToCameraPrefMapper implements Mapper<RealmCameraPref, CameraPreferences> {

  @Override
  public CameraPreferences map(RealmCameraPref realmCameraPref) {
    ResolutionPreference resolutionPreference = new ResolutionPreference(realmCameraPref.resolution,
        realmCameraPref.resolutionBack720pSupported, realmCameraPref.resolutionBack1080pSupported,
        realmCameraPref.resolutionBack2160pSupported, realmCameraPref.resolutionFront720pSupported,
        realmCameraPref.resolutionFront1080pSupported, realmCameraPref.resolutionFront2160pSupported);
    FrameRatePreference frameRatePreference = new FrameRatePreference(realmCameraPref.frameRate,
        realmCameraPref.frameRate24FpsSupported, realmCameraPref.frameRate25FpsSupported,
        realmCameraPref.frameRate30FpsSupported);
    CameraPreferences cameraPreferences = new CameraPreferences(resolutionPreference,
        frameRatePreference, realmCameraPref.quality, realmCameraPref.interfaceProSelected);
    return cameraPreferences;
  }
}
