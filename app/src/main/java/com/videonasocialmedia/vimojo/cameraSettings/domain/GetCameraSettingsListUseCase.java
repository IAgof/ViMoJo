package com.videonasocialmedia.vimojo.cameraSettings.domain;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingsItem;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingsPackage;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraPrefRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 14/11/17.
 */

public class GetCameraSettingsListUseCase {
  private CameraPreferences cameraPreferences;
  private Context context;
  private boolean DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE = true;
  private Project currentProject;

  @Inject
  public GetCameraSettingsListUseCase(Context context, CameraPrefRepository cameraPrefRepository) {
    this.context = context;
    currentProject = loadCurrentProject();
    cameraPreferences = cameraPrefRepository.getCameraPreferences();
  }

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public void checkCameraSettingsList(CameraSettingListUseCaseListener listener) {
    WeakReference<GetCameraSettingsListUseCase.CameraSettingListUseCaseListener>
            cameraSettingListListener = new WeakReference<>(listener);
    ResolutionPreference resolutionPreference = cameraPreferences.getResolutionPreference();
    FrameRatePreference frameRatePreference = cameraPreferences.getFrameRatePreference();

    List <CameraSettingsPackage> preferenceList = new ArrayList<>();

    List<CameraSettingsItem> interfaceProList = new ArrayList<>();
    interfaceProList.add(new CameraSettingsItem(Constants.CAMERA_PREF_INTERFACE_PRO_ID,
        context.getString(R.string.camera_pro),
        cameraPreferences.isInterfaceProSelected()));
    interfaceProList.add(new CameraSettingsItem(Constants.CAMERA_PREF_INTERFACE_BASIC_ID,
        context.getString(R.string.camera_basic),
            !cameraPreferences.isInterfaceProSelected()));
    DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE = true;
    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.camera_pro_o_basic),
        interfaceProList, DEFAULT_INTERFACE_PRO_BASIC_AVAILABLE));

    List<CameraSettingsItem> resolutionList = new ArrayList<>();
    String resolutionSelected = resolutionPreference.getResolution();
    if(resolutionPreference.isResolutionBack720pSupported()) {
      resolutionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_RESOLUTION_720_ID,
          context.getString(R.string.low_resolution_name),
          isResolution720Selected(resolutionSelected)));
    }
    if(resolutionPreference.isResolutionBack1080pSupported()) {
      resolutionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_RESOLUTION_1080_ID,
          context.getString(R.string.good_resolution_name),
          isResolution1080Selected(resolutionSelected)));
    }
    if(resolutionPreference.isResolutionBack2160pSupported()) {
      resolutionList.add(new CameraSettingsItem(Constants.CAMERA_PREF_RESOLUTION_2160_ID,
          context.getString(R.string.high_resolution_name),
          isResolution2160Selected(resolutionSelected)));
    }
    if(resolutionList.size() > 0) {
      preferenceList.add(new CameraSettingsPackage(context.getString(R.string.resolution),
          resolutionList, isCameraSettingAvailable(currentProject)));
    }

    List<CameraSettingsItem> frameRateList = new ArrayList<>();
    String frameRateSelected = frameRatePreference.getFrameRate();
    if(frameRatePreference.isFrameRate24FpsSupported()) {
      frameRateList.add(new CameraSettingsItem(Constants.CAMERA_PREF_FRAME_RATE_24_ID,
          context.getString(R.string.low_frame_rate_name), frameRateSelected.compareTo(Constants.CAMERA_PREF_FRAME_RATE_24) == 0));
    }
    if(frameRatePreference.isFrameRate25FpsSupported()) {
      frameRateList.add(new CameraSettingsItem(Constants.CAMERA_PREF_FRAME_RATE_25_ID,
          context.getString(R.string.good_frame_rate_name), frameRateSelected.compareTo(Constants.CAMERA_PREF_FRAME_RATE_25) == 0));
    }
    if(frameRatePreference.isFrameRate30FpsSupported()) {
      frameRateList.add(new CameraSettingsItem(Constants.CAMERA_PREF_FRAME_RATE_30_ID,
          context.getString(R.string.high_frame_rate_name), frameRateSelected.compareTo(Constants.CAMERA_PREF_FRAME_RATE_30) == 0));
    }
    if(frameRateList.size() > 0) {
      preferenceList.add(new CameraSettingsPackage(context.getString(R.string.frame_rate), frameRateList,
          isCameraSettingAvailable(currentProject)));
    }

    List<CameraSettingsItem> qualityList = new ArrayList<>();
    String qualitySelected = cameraPreferences.getQuality();
    qualityList.add(new CameraSettingsItem(Constants.CAMERA_PREF_QUALITY_16_ID,
        context.getString(R.string.low_quality_name), qualitySelected.compareTo(Constants.CAMERA_PREF_QUALITY_16) == 0));
    qualityList.add(new CameraSettingsItem(Constants.CAMERA_PREF_QUALITY_32_ID,
        context.getString(R.string.good_quality_name), qualitySelected.compareTo(Constants.CAMERA_PREF_QUALITY_32) == 0));
    qualityList.add(new CameraSettingsItem(Constants.CAMERA_PREF_QUALITY_50_ID,
        context.getString(R.string.high_quality_name), qualitySelected.compareTo(Constants.CAMERA_PREF_QUALITY_50) == 0));
    preferenceList.add(new CameraSettingsPackage(context.getString(R.string.quality), qualityList,
        isCameraSettingAvailable(currentProject)));

    CameraSettingListUseCaseListener listUseCaseListener = cameraSettingListListener.get();
    if(preferenceList.size() > 0) {
      listUseCaseListener.onSuccessGettingList(preferenceList);
    } else {
      listUseCaseListener.onErrorGettingList();
    }
  }

  private boolean isResolution2160Selected(String resolutionSelected) {
    return resolutionSelected.compareTo(Constants.CAMERA_PREF_RESOLUTION_2160) == 0;
  }

  private boolean isResolution1080Selected(String resolutionSelected) {
    return resolutionSelected.compareTo(Constants.CAMERA_PREF_RESOLUTION_1080) == 0;
  }

  private boolean isResolution720Selected(String resolutionSelected) {
    return resolutionSelected.compareTo(Constants.CAMERA_PREF_RESOLUTION_720) == 0;
  }

  private boolean isCameraSettingAvailable(Project project) {
    return !project.getVMComposition().hasVideos();
  }

  public interface CameraSettingListUseCaseListener {
    void onSuccessGettingList(List<CameraSettingsPackage> cameraSettingsPackages);
    void onErrorGettingList();
  }

}
