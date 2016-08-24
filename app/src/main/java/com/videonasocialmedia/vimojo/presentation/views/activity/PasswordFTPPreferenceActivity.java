package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.os.Bundle;
import android.text.InputType;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.PasswordFTPPreferencePresenter;

/**
 * Created by ruth on 24/08/16.
 */
public class PasswordFTPPreferenceActivity extends EditTextPreferenceActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new PasswordFTPPreferencePresenter(this, sharedPreferences);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        toolbarTitle.setText(R.string.password_FTP);
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
        infoText.setText(R.string.removePassword_FTP);
    }
}
