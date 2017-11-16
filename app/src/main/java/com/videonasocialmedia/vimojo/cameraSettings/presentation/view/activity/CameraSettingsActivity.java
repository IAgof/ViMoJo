package com.videonasocialmedia.vimojo.cameraSettings.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.RadioGroup;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingsPackage;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters.CameraSettingsPresenter;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter.CameraSettingsAdapter;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter.CameraSettingsListClickListener;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CameraSettingsActivity extends VimojoActivity implements CameraSettingsListClickListener, CameraSettingsView {

  @Inject
  CameraSettingsPresenter presenter;
  @Bind(R.id.camera_setting_recycler_view)
  RecyclerView recyclerCameraSettingsList;
  @Bind(R.id.camera_setting_ok)
  Button okButton;

  private final int NUM_COLUMNS_GRID_RECYCLER = 2;
  private CameraSettingsAdapter adapter;
  private List<CameraSettingsPackage> cameraSettingPackageList;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera_settings);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initCameraSettingsRecycler();
  }

  private void initCameraSettingsRecycler() {
    int orientation = LinearLayoutManager.VERTICAL;
    int num_grid_columns = NUM_COLUMNS_GRID_RECYCLER;

    adapter = new CameraSettingsAdapter();
    adapter.setCameraSettingsListClickListener(this);
    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, num_grid_columns,
        orientation, false);
    recyclerCameraSettingsList.setLayoutManager(layoutManager);
    recyclerCameraSettingsList.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    if(cameraSettingPackageList == null || cameraSettingPackageList.size() == 0) {
      presenter.getCameraSettingsList();
    }
    super.onResume();
  }


  @Override
  public void onCheckedChangeCameraPreference(RadioGroup radioGroup, int checkedId) {
    switch (checkedId){
      case Constants.CAMERA_PREF_INTERFACE_PRO_ID:
      case Constants.CAMERA_PREF_INTERFACE_BASIC_ID:
        presenter.setCameraInterfacePreference(checkedId);
        break;
      case Constants.CAMERA_PREF_RESOLUTION_720_ID:
      case Constants.CAMERA_PREF_RESOLUTION_1080_ID:
      case Constants.CAMERA_PREF_RESOLUTION_2160_ID:
        presenter.setCameraResolutionPreference(checkedId);
        break;
      case Constants.CAMERA_PREF_QUALITY_16_ID:
      case Constants.CAMERA_PREF_QUALITY_32_ID:
      case Constants.CAMERA_PREF_QUALITY_50_ID:
        presenter.setCameraQualityPreference(checkedId);
        break;
      case Constants.CAMERA_PREF_FRAME_RATE_24_ID:
      case Constants.CAMERA_PREF_FRAME_RATE_25_ID:
      case Constants.CAMERA_PREF_FRAME_RATE_30_ID:
        presenter.setCameraFrameRatePreference(checkedId);
        break;
    }
  }

  @Override
  public void showCameraSettingsList(List<CameraSettingsPackage> list) {
    cameraSettingPackageList = list;
    adapter.setCameraSettingsItemsList(list);
  }

  @OnClick (R.id.camera_setting_ok)
  public void navigatetoRecordCamera2(){
    Intent intent = new Intent(this, RecordCamera2Activity.class);
    startActivity(intent);
  }
}
