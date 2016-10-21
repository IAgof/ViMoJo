package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.SharedPreferences;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by ruth on 24/08/16.
 */
public class UsernameFTPPreferencePresenter extends EditTextPreferencePresenter {
    private String keyUsernameFTP;

    public UsernameFTPPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                          SharedPreferences sharedPreferences, String keyUsernameFTP) {
        super(editTextPreferenceView, sharedPreferences);
        this.keyUsernameFTP=keyUsernameFTP;
    }

    @Override
    public void setPreference(String text) {
        editor.putString(keyUsernameFTP, text);
        editor.commit();
        editTextPreferenceView.goBack();
    }

    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(keyUsernameFTP, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterUsername_FTP;
    }

    @Override
    public void removeData() {
        editor.putString(keyUsernameFTP, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }

}
