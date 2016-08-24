package com.videonasocialmedia.vimojo.presentation.mvp.presenters;


import android.content.SharedPreferences;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by ruth on 23/08/16.
 */
public class HostPreferencePresenter extends EditTextPreferencePresenter {

    public HostPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                   SharedPreferences sharedPreferences) {
        super(editTextPreferenceView, sharedPreferences);
    }

    @Override
    public void setPreference(String text) {
        editor.putString(ConfigPreferences.HOST, text);
        editor.commit();
        editTextPreferenceView.setUserPropertyToMixpanel("$host", text);
        editTextPreferenceView.goBack();
    }

    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(ConfigPreferences.HOST, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterHost;
    }

    @Override
    public void removeData() {
        editor.putString(ConfigPreferences.HOST, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }
}
