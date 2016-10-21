package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.SharedPreferences;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by ruth on 24/08/16.
 */
public class PasswordFTPPreferencePresenter extends EditTextPreferencePresenter {
    private String keyPasswordFTP;

    public PasswordFTPPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                          SharedPreferences sharedPreferences, String keyPasswordFTP) {
        super(editTextPreferenceView, sharedPreferences);
        this.keyPasswordFTP=keyPasswordFTP;
    }

    @Override
    public void setPreference(String text) {
        editor.putString(keyPasswordFTP, text);
        editor.commit();
        editTextPreferenceView.goBack();
    }

    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(keyPasswordFTP, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterPassword_FTP;
    }

    @Override
    public void removeData() {
        editor.putString(keyPasswordFTP, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }
}
