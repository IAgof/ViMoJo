package com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.mvp.presenters;


import android.content.Context;

import com.videonasocialmedia.vimojo.settings.cameraSettings.domain.GetCameraSettingListUseCase;
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsItem;
import com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.mvp.views.CameraSettingsView;

import java.util.List;

import javax.inject.Inject;

public class CameraSettingsPresenter {
  private Context context;
  private List<CameraSettingsItem> preferencesList;
  private CameraSettingsView preferenceListView;
  private GetCameraSettingListUseCase getSettingListUseCase;

  @Inject
  public CameraSettingsPresenter(CameraSettingsView preferenceListView, Context context, GetCameraSettingListUseCase getSettingListUseCase) {
    this.context = context;
    preferencesList = getSettingListUseCase.getCameraSettingsList();
    this.preferenceListView = preferenceListView;
  }

  public void getCameraSettingsList() {
    preferenceListView.showCameraSettingsList(preferencesList);
  }
}
