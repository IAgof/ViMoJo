package com.videonasocialmedia.vimojo.settings.cameraSettings.domain;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsPackage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 14/11/17.
 */

public class GetCameraSettingListUseCase {
  private Context context;

  @Inject
  public GetCameraSettingListUseCase(Context context) {
    this.context = context;
  }

  public List<CameraSettingsPackage> getCameraSettingsList() {

    List <CameraSettingsPackage> preferenceList = new ArrayList();

    List<String> optionList = new ArrayList();
    optionList.add(context.getString(R.string.camera_pro));
    optionList.add(context.getString(R.string.camera_basic));
    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.camera_pro_o_basic), optionList, true));

    optionList = new ArrayList();
    optionList.add(context.getString(R.string.low_resolution_name));
    optionList.add(context.getString(R.string.good_resolution_name));
    optionList.add(context.getString(R.string.high_resolution_name));

    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.resolution), optionList, false));

    optionList = new ArrayList();
    optionList.add(context.getString(R.string.low_frame_rate_name));
    optionList.add(context.getString(R.string.good_frame_rate_name));
    optionList.add(context.getString(R.string.high_frame_rate_name));

    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.frame_rate), optionList, true));

    optionList = new ArrayList();
    optionList.add(context.getString(R.string.low_quality_name));
    optionList.add(context.getString(R.string.good_quality_name));
    optionList.add(context.getString(R.string.high_quality_name));

    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.quality), optionList, true));

    return preferenceList;
  }



}
