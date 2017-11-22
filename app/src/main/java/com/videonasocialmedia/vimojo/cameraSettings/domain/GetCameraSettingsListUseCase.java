package com.videonasocialmedia.vimojo.cameraSettings.domain;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingItems;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingSelectable;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 14/11/17.
 */

public class GetCameraSettingsListUseCase {
  public static final int MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST = 1;
  private CameraSettings cameraSettings;
  private Context context;
  private boolean DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE = true;
  private Project currentProject;

  @Inject
  public GetCameraSettingsListUseCase(Context context, CameraSettingsRepository cameraSettingsRepository) {
    this.context = context;
    currentProject = loadCurrentProject();
    cameraSettings = cameraSettingsRepository.getCameraPreferences();
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public List<CameraSettingSelectable> checkCameraSettingsList() {

    ResolutionSetting resolutionSetting = cameraSettings.getResolutionSetting();
    HashMap<Integer, Boolean> resolutionMap = resolutionSetting.getResolutionsSupportedMap();
    FrameRateSetting frameRateSetting = cameraSettings.getFrameRateSetting();
    HashMap<Integer, Boolean> frameRateMap = frameRateSetting.getFrameRatesSupportedMap();

    List <CameraSettingSelectable> preferenceList = new ArrayList<>();

    List<CameraSettingItems> interfaceProList = new ArrayList<>();
    interfaceProList.add(new CameraSettingItems(Constants.CAMERA_PREF_INTERFACE_PRO_ID,
        context.getString(R.string.camera_pro),
        cameraSettings.isInterfaceProSelected()));
    interfaceProList.add(new CameraSettingItems(Constants.CAMERA_PREF_INTERFACE_BASIC_ID,
        context.getString(R.string.camera_basic),
            !cameraSettings.isInterfaceProSelected()));
    DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE = true;
    preferenceList.add(new CameraSettingSelectable(context.getString(R.string.camera_pro_o_basic),
        interfaceProList, DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE));

    List<CameraSettingItems> resolutionList = new ArrayList<>();
    String resolutionSelected = resolutionSetting.getResolution();
    if(resolutionMap.get(Constants.CAMERA_PREF_RESOLUTION_720_BACK_ID)) {
      resolutionList.add(new CameraSettingItems(Constants.CAMERA_PREF_RESOLUTION_720_BACK_ID,
          context.getString(R.string.low_resolution_name),
          isResolution720Selected(resolutionSelected)));
    }
    if(resolutionMap.get(Constants.CAMERA_PREF_RESOLUTION_1080_BACK_ID)) {
      resolutionList.add(new CameraSettingItems(Constants.CAMERA_PREF_RESOLUTION_1080_BACK_ID,
          context.getString(R.string.good_resolution_name),
          isResolution1080Selected(resolutionSelected)));
    }
    if(resolutionMap.get(Constants.CAMERA_PREF_RESOLUTION_2160_BACK_ID)) {
      resolutionList.add(new CameraSettingItems(Constants.CAMERA_PREF_RESOLUTION_2160_BACK_ID,
          context.getString(R.string.high_resolution_name),
          isResolution2160Selected(resolutionSelected)));
    }
    if(resolutionList.size() > MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST) {
      preferenceList.add(new CameraSettingSelectable(context.getString(R.string.resolution),
          resolutionList, isCameraSettingAvailable(currentProject)));
    }

    List<CameraSettingItems> frameRateList = new ArrayList<>();
    String frameRateSelected = frameRateSetting.getFrameRate();
    if(frameRateMap.get(Constants.CAMERA_PREF_FRAME_RATE_24_ID)) {
      frameRateList.add(new CameraSettingItems(Constants.CAMERA_PREF_FRAME_RATE_24_ID,
          context.getString(R.string.low_frame_rate_name), frameRateSelected.equals(Constants.CAMERA_PREF_FRAME_RATE_24)));
    }
    if(frameRateMap.get(Constants.CAMERA_PREF_FRAME_RATE_25_ID)) {
      frameRateList.add(new CameraSettingItems(Constants.CAMERA_PREF_FRAME_RATE_25_ID,
          context.getString(R.string.good_frame_rate_name),
              frameRateSelected.equals(Constants.CAMERA_PREF_FRAME_RATE_25)));
    }
    if(frameRateMap.get(Constants.CAMERA_PREF_FRAME_RATE_30_ID)) {
      frameRateList.add(new CameraSettingItems(Constants.CAMERA_PREF_FRAME_RATE_30_ID,
          context.getString(R.string.high_frame_rate_name),
              frameRateSelected.equals(Constants.CAMERA_PREF_FRAME_RATE_30)));
    }
    if(frameRateList.size() > MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST) {
      preferenceList.add(new CameraSettingSelectable(context.getString(R.string.frame_rate), frameRateList,
          isCameraSettingAvailable(currentProject)));
    }

    List<CameraSettingItems> qualityList = new ArrayList<>();
    String qualitySelected = cameraSettings.getQuality();
    qualityList.add(new CameraSettingItems(Constants.CAMERA_PREF_QUALITY_16_ID,
        context.getString(R.string.low_quality_name),
            qualitySelected.equals(Constants.CAMERA_PREF_QUALITY_16)));
    qualityList.add(new CameraSettingItems(Constants.CAMERA_PREF_QUALITY_32_ID,
        context.getString(R.string.good_quality_name), qualitySelected.equals(Constants.CAMERA_PREF_QUALITY_32)));
    qualityList.add(new CameraSettingItems(Constants.CAMERA_PREF_QUALITY_50_ID,
        context.getString(R.string.high_quality_name), qualitySelected.equals(Constants.CAMERA_PREF_QUALITY_50)));
    preferenceList.add(new CameraSettingSelectable(context.getString(R.string.quality), qualityList,
        isCameraSettingAvailable(currentProject)));

    return preferenceList;
  }

  private boolean isResolution2160Selected(String resolutionSelected) {
    return resolutionSelected.equals(Constants.CAMERA_PREF_RESOLUTION_2160);
  }

  private boolean isResolution1080Selected(String resolutionSelected) {
    return resolutionSelected.equals(Constants.CAMERA_PREF_RESOLUTION_1080);
  }

  private boolean isResolution720Selected(String resolutionSelected) {
    return resolutionSelected.equals(Constants.CAMERA_PREF_RESOLUTION_720);
  }

  private boolean isCameraSettingAvailable(Project project) {
    return !project.getVMComposition().hasVideos();
  }

}
