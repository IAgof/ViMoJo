package com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter;

import android.widget.RadioGroup;

/**
 * Created by ruth on 14/11/17.
 */

public interface CameraSettingsListClickListener {
  void onCheckedChangeCameraPreference(RadioGroup radioGroup, int checkedId);
}
