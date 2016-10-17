package com.videonasocialmedia.vimojo.presentation.mvp.presenters;


import android.content.SharedPreferences;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by ruth on 23/08/16.
 */
public class HostPreferencePresenter extends EditTextPreferencePresenter {
    private String keyHost;

    public HostPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                   SharedPreferences sharedPreferences, String keyHost) {
        super(editTextPreferenceView, sharedPreferences);
        this.keyHost=keyHost;
    }

    @Override
    public void setPreference(String text) {
        editor.putString(keyHost, text);
        editor.commit();
        editTextPreferenceView.goBack();
    }

    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(keyHost, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterHost;
    }

    @Override
    public void removeData() {
        editor.putString(keyHost, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }
}
