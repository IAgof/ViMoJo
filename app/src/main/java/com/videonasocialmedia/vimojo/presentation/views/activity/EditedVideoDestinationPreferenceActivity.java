package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.os.Bundle;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditedVideoDestinationPreferencePresenter;

/**
 * Created by ruth on 24/08/16.
 */
public class EditedVideoDestinationPreferenceActivity extends EditTextPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new EditedVideoDestinationPreferencePresenter(this, sharedPreferences);

        toolbarTitle.setText(R.string.edited_video_destination);
    }

    @Override
    public void putIconForEditTextIsNotNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_email);
    }

    @Override
    public void putIconForEditTextIsNull() {
        editTextImage.setImageResource(R.drawable.activity_settings_icon_email_add);
    }

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeEditedVideoDestination_FTP);
    }

}
