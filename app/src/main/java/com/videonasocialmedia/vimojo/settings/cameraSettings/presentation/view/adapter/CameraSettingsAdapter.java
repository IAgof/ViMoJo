package com.videonasocialmedia.vimojo.settings.cameraSettings.presentation.view.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsPackage;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CameraSettingsAdapter extends
    RecyclerView.Adapter<CameraSettingsAdapter.CameraSettingsAdapterItemViewHolder> {

  private List<CameraSettingsPackage> cameraSettingsList;
  private CameraSettingsListClickListener listener;
  private Context context;

  public void setCameraSettingsListClickListener(CameraSettingsListClickListener listener) {
    this.listener = listener;
  }

  public void setCameraSettingsItemsList(List<CameraSettingsPackage> cameraSettingsList) {
    this.cameraSettingsList = cameraSettingsList;
    notifyDataSetChanged();
  }

  @Override
  public CameraSettingsAdapterItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
      View rowView = LayoutInflater.from(viewGroup.getContext()).
          inflate(R.layout.camera_settings_list_view_holder, viewGroup, false);
      this.context = viewGroup.getContext();
      return new CameraSettingsAdapterItemViewHolder(rowView, cameraSettingsList);
  }

  @Override
  public void onBindViewHolder(CameraSettingsAdapterItemViewHolder holder, int position) {

    CameraSettingsPackage cameraSettingsPackage = cameraSettingsList.get(position);
    holder.settingCameraTitlePackage.setText(cameraSettingsPackage.getTitlePreferencePackage());
    if(!cameraSettingsPackage.isAvailable()) {
      holder.textNotAvailable.setText(context.getString(R.string.preference_camera_not_available)
          + " " + cameraSettingsPackage.getTitlePreferencePackage());
      holder.textNotAvailable.setVisibility(View.VISIBLE);
      holder.cameraSettingGroup.setVisibility(View.GONE);
    } else {
      holder.textNotAvailable.setVisibility(View.GONE);
      holder.cameraSettingGroup.setVisibility(View.VISIBLE);
    }

    int id = (position+1)*100;

    for (String preference : cameraSettingsPackage.getPreferencesList()){
      RadioButton preferenceOption = new RadioButton(CameraSettingsAdapter.this.context);
      preferenceOption.setId(id++);
      preferenceOption.setText(preference);

      holder.cameraSettingGroup.addView(preferenceOption);
    }

  }

  @Override
  public int getItemCount() {
    int result = 0;
    if (cameraSettingsList != null)
      result = cameraSettingsList.size();
    return result;
  }

  class CameraSettingsAdapterItemViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.camera_setting_title_package)
    TextView settingCameraTitlePackage;
    @Bind(R.id.camera_setting_radio_group)
    RadioGroup cameraSettingGroup;
    @Bind(R.id.camera_setting_text_not_available)
    TextView textNotAvailable;

    private List<CameraSettingsPackage> cameraSettingsList;

    public CameraSettingsAdapterItemViewHolder(View itemView, List<CameraSettingsPackage> cameraSettingsList) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      this.cameraSettingsList = cameraSettingsList;

      cameraSettingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {

          if (radioGroup!= null) {
            Toast.makeText(CameraSettingsAdapter.this.context,
                "Radio button clicked " + radioGroup.getCheckedRadioButtonId(),
                Toast.LENGTH_SHORT).show();
          }
        }
      });
    }

    @OnClick({})
    public void onClick() {
      CameraSettingsPackage cameraSetting = getData(getAdapterPosition());
      listener.onClickCameraPreferencesItem();
    }
      CameraSettingsPackage getData(int adapterPosition) {
      return cameraSettingsList == null ? null : cameraSettingsList.get(adapterPosition);
    }
  }
}
