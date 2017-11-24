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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.*;

/**
 * Created by ruth on 14/11/17.
 */

public class GetCameraSettingsListUseCase {
  public static final int MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST = 1;
  private static boolean DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE = true;
  private CameraSettings cameraSettings;
  private Context context;
  private Project currentProject;

  @Inject
  public GetCameraSettingsListUseCase(Context context, CameraSettingsRepository
      cameraSettingsRepository) {
    this.context = context;
    currentProject = loadCurrentProject();
    cameraSettings = cameraSettingsRepository.getCameraSettings();
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public List<CameraSettingSelectable> checkCameraSettingsList(HashMap<Integer, String> resolutionNames,
                                                               HashMap<Integer, String> qualityNames,
                                                               HashMap<Integer, String> frameRateNames,
                                                               HashMap<Integer, String> interfaceNames) {

    ResolutionSetting resolutionSetting = cameraSettings.getResolutionSetting();
    HashMap<Integer, Boolean> resolutionsSupportedMap = resolutionSetting.getResolutionsSupportedMap();
    FrameRateSetting frameRateSetting = cameraSettings.getFrameRateSetting();
    HashMap<Integer, Boolean> frameRateSupportedMap = frameRateSetting.getFrameRatesSupportedMap();

    List <CameraSettingSelectable> preferenceList = new ArrayList<>();

    List<CameraSettingItems> interfaceList = new ArrayList<>();
    String interfaceSelected = cameraSettings.getInterfaceSelected();
    interfaceList.add(new CameraSettingItems(CAMERA_SETTING_INTERFACE_PRO_ID,
        context.getString(R.string.camera_pro), interfaceNames.get(CAMERA_SETTING_INTERFACE_PRO_ID)
            .equals(interfaceSelected)));
    interfaceList.add(new CameraSettingItems(CAMERA_SETTING_INTERFACE_BASIC_ID,
        context.getString(R.string.camera_basic),
            interfaceNames.get(CAMERA_SETTING_INTERFACE_BASIC_ID).equals(interfaceSelected)));
    preferenceList.add(new CameraSettingSelectable(context.getString(R.string.camera_pro_o_basic),
        interfaceList, DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE));

    List<CameraSettingItems> resolutionList = new ArrayList<>();
    String resolutionSelected = resolutionSetting.getResolution();
    if(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_720_BACK_ID)) {
      resolutionList.add(new CameraSettingItems(CAMERA_SETTING_RESOLUTION_720_BACK_ID,
          context.getString(R.string.low_resolution_name),
          resolutionNames.get(CAMERA_SETTING_RESOLUTION_720_BACK_ID).equals(resolutionSelected)));
    }
    if(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_1080_BACK_ID)) {
      resolutionList.add(new CameraSettingItems(CAMERA_SETTING_RESOLUTION_1080_BACK_ID,
          context.getString(R.string.good_resolution_name),
          resolutionNames.get(CAMERA_SETTING_RESOLUTION_1080_BACK_ID).equals(resolutionSelected)));
    }
    if(resolutionsSupportedMap.get(CAMERA_SETTING_RESOLUTION_2160_BACK_ID)) {
      resolutionList.add(new CameraSettingItems(CAMERA_SETTING_RESOLUTION_2160_BACK_ID,
          context.getString(R.string.high_resolution_name),
          resolutionNames.get(CAMERA_SETTING_RESOLUTION_2160_BACK_ID).equals(resolutionSelected)));
    }
    if(resolutionList.size() > MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST) {
      preferenceList.add(new CameraSettingSelectable(context.getString(R.string.resolution),
          resolutionList, isCameraSettingAvailable(currentProject)));
    }

    List<CameraSettingItems> frameRateList = new ArrayList<>();
    String frameRateSelected = frameRateSetting.getFrameRate();
    if(frameRateSupportedMap.get(CAMERA_SETTING_FRAME_RATE_24_ID)) {
      frameRateList.add(new CameraSettingItems(CAMERA_SETTING_FRAME_RATE_24_ID,
          context.getString(R.string.low_frame_rate_name), frameRateNames.get(CAMERA_SETTING_FRAME_RATE_24_ID).equals(frameRateSelected)));
    }
    if(frameRateSupportedMap.get(CAMERA_SETTING_FRAME_RATE_25_ID)) {
      frameRateList.add(new CameraSettingItems(CAMERA_SETTING_FRAME_RATE_25_ID,
          context.getString(R.string.good_frame_rate_name), frameRateNames.get(CAMERA_SETTING_FRAME_RATE_25_ID).equals(frameRateSelected)));
    }
    if(frameRateSupportedMap.get(CAMERA_SETTING_FRAME_RATE_30_ID)) {
      frameRateList.add(new CameraSettingItems(CAMERA_SETTING_FRAME_RATE_30_ID,
          context.getString(R.string.high_frame_rate_name), frameRateNames.get(CAMERA_SETTING_FRAME_RATE_30_ID).equals(frameRateSelected)));
    }
    if(frameRateList.size() > MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST) {
      preferenceList.add(new CameraSettingSelectable(context.getString(R.string.frame_rate), frameRateList,
          isCameraSettingAvailable(currentProject)));
    }

    List<CameraSettingItems> qualityList = new ArrayList<>();
    String qualitySelected = cameraSettings.getQuality();
    qualityList.add(new CameraSettingItems(CAMERA_SETTING_QUALITY_16_ID,
        context.getString(R.string.low_quality_name), qualityNames.get(CAMERA_SETTING_QUALITY_16_ID).equals(qualitySelected)));
    qualityList.add(new CameraSettingItems(CAMERA_SETTING_QUALITY_32_ID,
        context.getString(R.string.good_quality_name),  qualityNames.get(CAMERA_SETTING_QUALITY_32_ID).equals(qualitySelected)));
    qualityList.add(new CameraSettingItems(CAMERA_SETTING_QUALITY_50_ID,
        context.getString(R.string.high_quality_name),  qualityNames.get(CAMERA_SETTING_QUALITY_50_ID).equals(qualitySelected)));
    preferenceList.add(new CameraSettingSelectable(context.getString(R.string.quality), qualityList,
        isCameraSettingAvailable(currentProject)));

    return preferenceList;
  }

  private boolean isCameraSettingAvailable(Project project) {
    return !project.getVMComposition().hasVideos();
  }

}
