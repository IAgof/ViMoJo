package com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.mvp.presenters;


import android.content.Context;

import com.videonasocialmedia.vimojo.settings.cameraSettings.domain.GetCameraSettingsListUseCase;
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsPackage;
import com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.mvp.views.CameraSettingsView;

import java.util.List;

import javax.inject.Inject;

public class CameraSettingsPresenter {
  private Context context;
  private List<CameraSettingsPackage> preferencesList;
  private CameraSettingsView preferenceListView;
  private GetCameraSettingsListUseCase getSettingListUseCase;

  @Inject
  public CameraSettingsPresenter(CameraSettingsView preferenceListView, Context context, GetCameraSettingsListUseCase getSettingListUseCase) {
    this.context = context;
    preferencesList = getSettingListUseCase.getCameraSettingsList();
    this.preferenceListView = preferenceListView;
  }

  public void getCameraSettingsList() {
    preferenceListView.showCameraSettingsList(preferencesList);
  }

  public void setCameraInterfacePreference(int checkedId) {
  }

  public void setCameraResolutionPreference(int checkedId) {
  }

  public void setCameraQualityPreference(int checkedId) {
  }

  public void setCameraFrameRatePreference(int checkedId) {
  }
}
