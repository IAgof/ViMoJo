package com.videonasocialmedia.vimojo.repository.camera;

import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.repository.Repository;

/**
 * Created by alvaro on 14/11/17.
 */

public interface CameraRepository extends Repository<CameraPreferences> {

  void update(CameraPreferences item);
  CameraPreferences getCameraPreferences();
}
