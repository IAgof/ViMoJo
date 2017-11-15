package com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsPackage;
import com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.mvp.presenters.CameraSettingsPresenter;
import com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.view.adapter.CameraSettingsAdapter;
import com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.view.adapter.CameraSettingsListClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CameraSettingsActivity extends VimojoActivity implements CameraSettingsListClickListener, CameraSettingsView {

  @Inject
  CameraSettingsPresenter presenter;
  @Bind(R.id.camera_setting_recycler_view)
  RecyclerView preferenceList;
  @Bind(R.id.camera_setting_ok)
  Button okButton;

  private final int NUM_COLUMNS_GRID_RECYCLER = 2;
  private CameraSettingsAdapter adapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera_settings);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initCameraSettingsRecycler();
    presenter.getCameraSettingsList();
  }

  private void initCameraSettingsRecycler() {
    int orientation = LinearLayoutManager.VERTICAL;
    int num_grid_columns = NUM_COLUMNS_GRID_RECYCLER;

    adapter = new CameraSettingsAdapter();
    adapter.setCameraSettingsListClickListener(this);
    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, num_grid_columns,
        orientation, false);
    preferenceList.setLayoutManager(layoutManager);
    preferenceList.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void onClickCameraPreferencesItem() {

  }

  @Override
  public void showCameraSettingsList(List<CameraSettingsPackage> list) {
    adapter.setCameraSettingsItemsList(list);
  }

  @OnClick (R.id.camera_setting_ok)
  public void navigatetoRecordCamera2(){
    Intent intent = new Intent(this, RecordCamera2Activity.class);
    startActivity(intent);
  }
}
