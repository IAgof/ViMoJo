package com.videonasocialmedia.vimojo.cameraSettings.domain;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingValue;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingViewModel;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings.CAMERA_SETTING_QUALITY_16_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings.CAMERA_SETTING_QUALITY_32_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings.CAMERA_SETTING_QUALITY_50_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.*;

/**
 * Created by ruth on 14/11/17.
 */

/**
 * {@link CameraSettingViewModel} mapper.
 *
 * This class maps from {@link CameraSettings} model to {@link CameraSettingViewModel} for
 * representing user selected camera settings in {@link com.videonasocialmedia.vimojo.cameraSettings.presentation.view.activity.CameraSettingsActivity}
 */
public class GetCameraSettingsMapperSupportedListUseCase {
  public static final int MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST = 1;
  private static boolean DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE = true;
  private CameraSettings cameraSettings;
  private Context context;
  private Project currentProject;

  @Inject
  public GetCameraSettingsMapperSupportedListUseCase(Context context,
                                                     CameraSettingsRepository cameraSettingsRepository) {
    this.context = context;
    currentProject = loadCurrentProject();
    cameraSettings = cameraSettingsRepository.getCameraSettings();
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public List<CameraSettingViewModel> getCameraSettingsList(
          HashMap<Integer, String> resolutionNames, HashMap<Integer, String> qualityNames,
          HashMap<Integer, String> frameRateNames, HashMap<Integer, String> interfaceNames) {
    List <CameraSettingViewModel> preferenceList = new ArrayList<>();
    addProInterfaceSettingsToList(interfaceNames, preferenceList);
    addResolutionSettingsToList(
            resolutionNames, cameraSettings.getResolutionSetting(), preferenceList);
    addFrameRateSettingsToList(
            frameRateNames, cameraSettings.getFrameRateSetting(), preferenceList);
    addQualitySettingsToList(qualityNames, preferenceList);
    return preferenceList;
  }

  private void addProInterfaceSettingsToList(HashMap<Integer, String> interfaceNames,
                                             List<CameraSettingViewModel> preferenceList) {
    String interfaceSelected = cameraSettings.getInterfaceSelected();
    List<CameraSettingValue> interfaceList = new ArrayList<>();
    interfaceList.add(new CameraSettingValue(
            CAMERA_SETTING_INTERFACE_PRO_ID, context.getString(R.string.camera_pro),
            interfaceNames.get(CAMERA_SETTING_INTERFACE_PRO_ID).equals(interfaceSelected)));
    interfaceList.add(new CameraSettingValue(
            CAMERA_SETTING_INTERFACE_BASIC_ID, context.getString(R.string.camera_basic),
            interfaceNames.get(CAMERA_SETTING_INTERFACE_BASIC_ID).equals(interfaceSelected)));
    String settingTitle = context.getString(R.string.camera_pro_o_basic);
    CameraSettingViewModel proInterfaceSetting = new CameraSettingViewModel(
            settingTitle, interfaceList, DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE);
    preferenceList.add(proInterfaceSetting);
  }

  private void addResolutionSettingsToList(HashMap<Integer, String> resolutionNames,
                                           ResolutionSetting resolutionSetting,
                                           List<CameraSettingViewModel> preferenceList) {
    String resolutionSelected = resolutionSetting.getResolution();
    int cameraIdSelected = cameraSettings.getCameraIdSelected();
    List<CameraSettingValue> resolutionList = new ArrayList<>();

    if(cameraIdSelected == BACK_CAMERA_ID) {
      if (resolutionSetting.deviceSupports(CAMERA_SETTING_RESOLUTION_720_BACK_ID)) {
        resolutionList.add(new CameraSettingValue(
            CAMERA_SETTING_RESOLUTION_720_BACK_ID,
            context.getString(R.string.low_resolution_name),
            resolutionNames.get(CAMERA_SETTING_RESOLUTION_720_BACK_ID)
                .equals(resolutionSelected)));
      }
      if (resolutionSetting.deviceSupports(CAMERA_SETTING_RESOLUTION_1080_BACK_ID)) {
        resolutionList.add(new CameraSettingValue(CAMERA_SETTING_RESOLUTION_1080_BACK_ID,
            context.getString(R.string.good_resolution_name),
            resolutionNames.get(CAMERA_SETTING_RESOLUTION_1080_BACK_ID).equals(resolutionSelected)));
      }
      if (resolutionSetting.deviceSupports(CAMERA_SETTING_RESOLUTION_2160_BACK_ID)) {
        resolutionList.add(new CameraSettingValue(CAMERA_SETTING_RESOLUTION_2160_BACK_ID,
            context.getString(R.string.high_resolution_name),
            resolutionNames.get(CAMERA_SETTING_RESOLUTION_2160_BACK_ID).equals(resolutionSelected)));
      }
    } else {
      if(cameraIdSelected == FRONT_CAMERA_ID) {
        if (resolutionSetting.deviceSupports(CAMERA_SETTING_RESOLUTION_720_FRONT_ID)) {
          resolutionList.add(new CameraSettingValue(
              CAMERA_SETTING_RESOLUTION_720_FRONT_ID,
              context.getString(R.string.low_resolution_name),
              resolutionNames.get(CAMERA_SETTING_RESOLUTION_720_FRONT_ID)
                  .equals(resolutionSelected)));
        }
        if (resolutionSetting.deviceSupports(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID)) {
          resolutionList.add(new CameraSettingValue(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID,
              context.getString(R.string.good_resolution_name),
              resolutionNames.get(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID).equals(resolutionSelected)));
        }
        if (resolutionSetting.deviceSupports(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID)) {
          resolutionList.add(new CameraSettingValue(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID,
              context.getString(R.string.high_resolution_name),
              resolutionNames.get(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID).equals(resolutionSelected)));
        }
      }
    }
    if (resolutionList.size() > MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST) {
      preferenceList.add(new CameraSettingViewModel(context.getString(R.string.resolution),
          resolutionList, isCameraSettingAvailable(currentProject)));
    }
  }

  private void addFrameRateSettingsToList(HashMap<Integer, String> frameRateNames,
                                          FrameRateSetting frameRateSetting,
                                          List<CameraSettingViewModel> preferenceList) {
    List<CameraSettingValue> frameRateList = new ArrayList<>();
    String frameRateSelected = frameRateSetting.getFrameRate();
    if (frameRateSetting.deviceSupports(CAMERA_SETTING_FRAME_RATE_24_ID)) {
      frameRateList.add(new CameraSettingValue(
              CAMERA_SETTING_FRAME_RATE_24_ID, context.getString(R.string.low_frame_rate_name),
              frameRateNames.get(CAMERA_SETTING_FRAME_RATE_24_ID).equals(frameRateSelected)));
    }
    if (frameRateSetting.deviceSupports(CAMERA_SETTING_FRAME_RATE_25_ID)) {
      frameRateList.add(new CameraSettingValue(
              CAMERA_SETTING_FRAME_RATE_25_ID, context.getString(R.string.good_frame_rate_name),
              frameRateNames.get(CAMERA_SETTING_FRAME_RATE_25_ID).equals(frameRateSelected)));
    }
    if (frameRateSetting.deviceSupports(CAMERA_SETTING_FRAME_RATE_30_ID)) {
      frameRateList.add(new CameraSettingValue(
              CAMERA_SETTING_FRAME_RATE_30_ID, context.getString(R.string.high_frame_rate_name),
              frameRateNames.get(CAMERA_SETTING_FRAME_RATE_30_ID).equals(frameRateSelected)));
    }
    if (frameRateList.size() > MINIMUM_OPTIONS_SUPPORTED_TO_SHOW_LIST) {
      preferenceList.add(new CameraSettingViewModel(
              context.getString(R.string.frame_rate), frameRateList,
              isCameraSettingAvailable(currentProject)));
    }
  }

  private void addQualitySettingsToList(HashMap<Integer, String> qualityNames,
                                        List<CameraSettingViewModel> preferenceList) {
    List<CameraSettingValue> qualityList = new ArrayList<>();
    String qualitySelected = cameraSettings.getQuality();
    qualityList.add(new CameraSettingValue(
            CAMERA_SETTING_QUALITY_16_ID, context.getString(R.string.low_quality_name),
            qualityNames.get(CAMERA_SETTING_QUALITY_16_ID).equals(qualitySelected)));
    qualityList.add(new CameraSettingValue(
            CAMERA_SETTING_QUALITY_32_ID, context.getString(R.string.good_quality_name),
            qualityNames.get(CAMERA_SETTING_QUALITY_32_ID).equals(qualitySelected)));
    qualityList.add(new CameraSettingValue(
            CAMERA_SETTING_QUALITY_50_ID, context.getString(R.string.high_quality_name),
            qualityNames.get(CAMERA_SETTING_QUALITY_50_ID).equals(qualitySelected)));
    preferenceList.add(new CameraSettingViewModel(context.getString(R.string.quality), qualityList,
        isCameraSettingAvailable(currentProject)));
  }

  // TODO(jliarte): 29/11/17 seems that this should not be modeled here, but enabled or disabled
  // by the presenter...
  private boolean isCameraSettingAvailable(Project project) {
    return !project.getVMComposition().hasVideos();
  }
}
