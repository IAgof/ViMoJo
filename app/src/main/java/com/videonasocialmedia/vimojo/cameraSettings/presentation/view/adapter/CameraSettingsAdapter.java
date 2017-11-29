package com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingViewModel;

import java.util.List;

public class CameraSettingsAdapter extends
    RecyclerView.Adapter<CameraSettingsViewHolder> {

  private List<CameraSettingViewModel> cameraSettingsList;
  private Context context;
  private CameraSettingsListClickListener settingsClickListener;

  public void setCameraSettingsListClickListener(CameraSettingsListClickListener listener) {
    this.settingsClickListener = listener;
  }

  public void setCameraSettingsItemsList(List<CameraSettingViewModel> cameraSettingsList) {
    this.cameraSettingsList = cameraSettingsList;
    notifyDataSetChanged();
  }

  @Override
  public CameraSettingsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View rowView = LayoutInflater.from(viewGroup.getContext()).
        inflate(R.layout.camera_settings_list_view_holder, viewGroup, false);
    this.context = viewGroup.getContext();
    return new CameraSettingsViewHolder(rowView, this, context);
  }

  @Override
  public void onBindViewHolder(CameraSettingsViewHolder holder, int position) {
    CameraSettingViewModel cameraSettingViewModel = cameraSettingsList.get(position);
    holder.bindData(cameraSettingViewModel);
  }

  @Override
  public int getItemCount() {
    int result = 0;
    if (cameraSettingsList != null)
      result = cameraSettingsList.size();
    return result;
  }

  public void onCheckedChangeCameraSetting(RadioGroup radioGroup, int checkedId) {
    settingsClickListener.onCheckedChangeCameraPreference(radioGroup, checkedId);
  }
}
