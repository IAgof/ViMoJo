package com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingSelectable;

import java.util.List;

public class CameraSettingsAdapter extends
    RecyclerView.Adapter<CameraSettingsViewHolder> {

  private List<CameraSettingSelectable> cameraSettingsList;
  private Context context;
  private CameraSettingsListClickListener listener;

  public void setCameraSettingsListClickListener(CameraSettingsListClickListener listener) {
    this.listener = listener;
  }

  public void setCameraSettingsItemsList(List<CameraSettingSelectable> cameraSettingsList) {
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
    CameraSettingSelectable cameraSettingSelectable = cameraSettingsList.get(position);
    holder.bindData(cameraSettingSelectable);
  }

  @Override
  public int getItemCount() {
    int result = 0;
    if (cameraSettingsList != null)
      result = cameraSettingsList.size();
    return result;
  }

  public void onCheckedChangeCameraSetting(RadioGroup radioGroup, int checkedId) {
    listener.onCheckedChangeCameraPreference(radioGroup, checkedId);
  }
}
