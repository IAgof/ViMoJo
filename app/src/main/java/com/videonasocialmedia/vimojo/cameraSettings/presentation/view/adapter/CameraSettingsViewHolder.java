package com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingValue;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alvaro on 23/11/17.
 */

public class CameraSettingsViewHolder extends RecyclerView.ViewHolder {
  private final Context context;
  @Bind(R.id.camera_setting_title_package)
  TextView settingCameraTitleSelectable;
  @Bind(R.id.camera_setting_radio_group)
  RadioGroup cameraSettingRadioGroup;
  @Bind(R.id.camera_setting_text_not_available)
  TextView textNotAvailable;

  protected CameraSettingsViewHolder(View itemView, final CameraSettingsAdapter
      cameraSettingsAdapter, Context context) {
    super(itemView);
    this.context = context;
    ButterKnife.bind(this, itemView);
    cameraSettingRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        if (radioGroup != null) {
          cameraSettingsAdapter.onCheckedChangeCameraSetting(radioGroup, checkedId);
        }
      }
    });
  }

  public void bindData(CameraSettingViewModel cameraSettingViewModel) {
    settingCameraTitleSelectable
        .setText(cameraSettingViewModel.getSettingTitle());
    if (!cameraSettingViewModel.isAvailable()) {
      showSettingNotAvailable(cameraSettingViewModel);
    } else {
      showSettingsOptions(cameraSettingViewModel);
    }
  }

  private void showSettingsOptions(CameraSettingViewModel cameraSettingViewModel) {
    textNotAvailable.setVisibility(View.GONE);
    cameraSettingRadioGroup.setVisibility(View.VISIBLE);
    for (CameraSettingValue settingItems : cameraSettingViewModel.getSettingsList()) {
      RadioButton settingOption = new RadioButton(context);
      settingOption.setId(settingItems.getId());
      settingOption.setText(settingItems.getName());
      settingOption.setChecked(settingItems.isSelected());
      cameraSettingRadioGroup.addView(settingOption);
    }
  }

  private void showSettingNotAvailable(CameraSettingViewModel cameraSettingViewModel) {
    textNotAvailable.setText(context.getString(R.string.preference_camera_not_available)
        + " " + cameraSettingViewModel.getSettingTitle());
    textNotAvailable.setVisibility(View.VISIBLE);
    cameraSettingRadioGroup.setVisibility(View.GONE);
  }
}
