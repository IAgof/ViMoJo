package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.os.Bundle;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.HostPreferencePresenter;

/**
 * Created by ruth on 23/08/16.
 */
public class HostPreferenceActivity extends EditTextPreferenceActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new HostPreferencePresenter(this, sharedPreferences);
        toolbarTitle.setText(R.string.host_FTP);
    }

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeHost);
    }


}
