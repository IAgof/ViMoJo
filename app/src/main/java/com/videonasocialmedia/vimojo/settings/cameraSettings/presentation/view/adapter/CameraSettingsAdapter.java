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
import com.videonasocialmedia.vimojo.settings.cameraSettings.model.CameraSettingsItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CameraSettingsAdapter extends
    RecyclerView.Adapter<CameraSettingsAdapter.CameraSettingsAdapterItemViewHolder> {

  private List<CameraSettingsItem> cameraSettingsList;
  private CameraSettingsListClickListener listener;
  private Context context;

  public void setCameraSettingsListClickListener(CameraSettingsListClickListener listener) {
    this.listener = listener;
  }

  public void setCameraSettingsItemsList(List<CameraSettingsItem> cameraSettingsList) {
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

    CameraSettingsItem cameraSettingsItem = cameraSettingsList.get(position);
    holder.settingCameraTitlePackage.setText(cameraSettingsItem.getTitlePreferencePackage());

    int id = (position+1)*100;
    for (String preference : cameraSettingsItem.getPreferencesList()){
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

    private List<CameraSettingsItem> cameraSettingsList;

    public CameraSettingsAdapterItemViewHolder(View itemView, List<CameraSettingsItem> cameraSettingsList) {
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
      CameraSettingsItem cameraSetting = getData(getAdapterPosition());
      listener.onClickStoreItem();
    }
      CameraSettingsItem getData(int adapterPosition) {
      return cameraSettingsList == null ? null : cameraSettingsList.get(adapterPosition);
    }
  }
}
