package com.videonasocialmedia.vimojo.cameraSettings.repository;

import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.repository.Repository;

/**
 * Created by alvaro on 14/11/17.
 */

public interface CameraPrefRepository extends Repository<CameraPreferences> {

  void update(CameraPreferences item);

  CameraPreferences getCameraPreferences();
}
