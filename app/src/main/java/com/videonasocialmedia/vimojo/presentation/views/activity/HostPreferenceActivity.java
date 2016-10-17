package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.HostPreferencePresenter;

/**
 * Created by ruth on 23/08/16.
 */
public class HostPreferenceActivity extends EditTextPreferenceActivity  {
    String keyHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbarTitle.setText(R.string.host_FTP);
        Intent intent = getIntent();
        keyHost=intent.getStringExtra("keyHost");
        presenter = new HostPreferencePresenter(this, sharedPreferences, keyHost);
    }

    @Override
    public void showInfoText() {
        infoText.setText(R.string.removeHost);
    }
    
}
