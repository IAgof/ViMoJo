package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.os.Bundle;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.UsernameFTPPreferencePresenter;

/**
 * Created by ruth on 24/08/16.
 */
public class UserNameFTPPreferenceActivity extends EditTextPreferenceActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new UsernameFTPPreferencePresenter(this, sharedPreferences);
        toolbarTitle.setText(R.string.username_FTP);
    }

    @Override
    public void putIconForEditTextIsNotNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_person);
    }

    @Override
    public void putIconForEditTextIsNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_person_add);
    }

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeUsername_FTP);
    }
}