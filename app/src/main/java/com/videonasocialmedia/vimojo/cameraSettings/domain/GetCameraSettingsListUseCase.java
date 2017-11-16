package com.videonasocialmedia.vimojo.settings.cameraSettings.domain;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsItem;
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsPackage;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 14/11/17.
 */

public class GetCameraSettingsListUseCase {
  private Context context;

  @Inject
  public GetCameraSettingsListUseCase(Context context) {
    this.context = context;
  }

  public List<CameraSettingsPackage> getCameraSettingsList() {

    List <CameraSettingsPackage> preferenceList = new ArrayList();

    List<CameraSettingsItem> optionList = new ArrayList();
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_INTERFACE_PRO_ID,
        context.getString(R.string.camera_pro)));
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_INTERFACE_BASIC_ID,
        context.getString(R.string.camera_basic)));
    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.camera_pro_o_basic),
        optionList, true));

    optionList = new ArrayList();
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_RESOLUTION_720_ID,
        context.getString(R.string.low_resolution_name)));
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_RESOLUTION_1080_ID,
        context.getString(R.string.good_resolution_name)));
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_RESOLUTION_2160_ID,
        context.getString(R.string.high_resolution_name)));

    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.resolution), optionList,
        true));

    optionList = new ArrayList();
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_FRAME_RATE_24_ID,
        context.getString(R.string.low_frame_rate_name)));
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_FRAME_RATE_25_ID,
        context.getString(R.string.good_frame_rate_name)));
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_FRAME_RATE_30_ID,
        context.getString(R.string.high_frame_rate_name)));

    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.frame_rate), optionList,
        true));

    optionList = new ArrayList();
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_QUALITY_16_ID,
        context.getString(R.string.low_quality_name)));
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_QUALITY_32_ID,
        context.getString(R.string.good_quality_name)));
    optionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_QUALITY_50_ID,
        context.getString(R.string.high_quality_name)));

    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.quality), optionList,
        true));

    return preferenceList;
  }



}
