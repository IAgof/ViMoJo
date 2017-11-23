package com.videonasocialmedia.vimojo.cameraSettings.presentation.view.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingItems;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettingSelectable;

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

  public void bindData(CameraSettingSelectable cameraSettingSelectable) {
    settingCameraTitleSelectable
        .setText(cameraSettingSelectable.getTitleCameraSettingsSelectable());
    if (!cameraSettingSelectable.isAvailable()) {
      showSettingNotAvailable(cameraSettingSelectable);
    } else {
      showSettingsOptions(cameraSettingSelectable);
    }
  }

  private void showSettingsOptions(CameraSettingSelectable cameraSettingSelectable) {
    textNotAvailable.setVisibility(View.GONE);
    cameraSettingRadioGroup.setVisibility(View.VISIBLE);
    for (CameraSettingItems preference : cameraSettingSelectable.getPreferencesList()) {
      RadioButton preferenceOption = new RadioButton(context);
      preferenceOption.setId(preference.getId());
      preferenceOption.setText(preference.getTitleCameraSettingsItem());
      preferenceOption.setChecked(preference.isSelected());
      cameraSettingRadioGroup.addView(preferenceOption);
    }
  }

  private void showSettingNotAvailable(CameraSettingSelectable cameraSettingSelectable) {
    textNotAvailable.setText(context.getString(R.string.preference_camera_not_available)
        + " " + cameraSettingSelectable.getTitleCameraSettingsSelectable());
    textNotAvailable.setVisibility(View.VISIBLE);
    cameraSettingRadioGroup.setVisibility(View.GONE);
  }
}
