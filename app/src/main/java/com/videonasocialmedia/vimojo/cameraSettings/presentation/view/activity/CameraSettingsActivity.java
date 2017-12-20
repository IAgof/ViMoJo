package com.videonasocialmedia.vimojo.cameraSettings.presentation.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingViewModel;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.presenters.CameraSettingsPresenter;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.mvp.views.CameraSettingsView;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter.CameraSettingsAdapter;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter.CameraSettingsListClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CameraSettingsActivity extends VimojoActivity implements
    CameraSettingsListClickListener, CameraSettingsView {

  @Inject
  CameraSettingsPresenter presenter;
  @Bind(R.id.camera_setting_recycler_view)
  RecyclerView recyclerCameraSettingsList;
  @Bind(R.id.camera_setting_ok)
  Button okButton;

  private CameraSettingsAdapter adapter;
  private List<CameraSettingViewModel> cameraSettingPackageList;
  public static final int NUM_COLUMNS_GRID_RECYCLER = 2;

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
    adapter = new CameraSettingsAdapter();
    adapter.setCameraSettingsListClickListener(this);
    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
        NUM_COLUMNS_GRID_RECYCLER, orientation, false);
    recyclerCameraSettingsList.setLayoutManager(layoutManager);
    recyclerCameraSettingsList.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    if (cameraSettingPackageList == null || cameraSettingPackageList.size() == 0) {
      presenter.getCameraSettingsList();
    }
    super.onResume();
  }

  @Override
  public void onCheckedChangeCameraPreference(RadioGroup radioGroup, int checkedId) {
    presenter.settingChanged(checkedId);
  }

  @Override
  public void showCameraSettingsList(List<CameraSettingViewModel> list) {
    cameraSettingPackageList = list;
    adapter.setCameraSettingsItemsList(list);
  }

  @Override
  public void showDialogResolutionNotSupportedInBothCameras(final int resolutionSelectedId) {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.VideonaDialog);
    dialog.setTitle(getString(R.string.dialog_title_resolution_not_supported_back_front_camera));
    dialog.setMessage(getString(R.string.dialog_title_message_not_supported_back_front_camera));
    dialog.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        presenter.setCameraResolutionSetting(resolutionSelectedId);
      }
    });
    dialog.show();
  }

  @Override
  public void onBackPressed() {
    navigateToRecord();
  }

  @OnClick(R.id.camera_setting_ok)
  public void navigateToRecord() {
    Intent intent = new Intent(this, RecordCamera2Activity.class);
    startActivity(intent);
  }
}