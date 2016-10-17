package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by ruth on 24/08/16.
 */
public class EditedVideoDestinationPreferencePresenter extends EditTextPreferencePresenter {
    private String keyEditedVideoDestination;

    public EditedVideoDestinationPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                    SharedPreferences sharedPreferences, String keyEditedVideoDestination) {
        super(editTextPreferenceView, sharedPreferences);
        this.keyEditedVideoDestination=keyEditedVideoDestination;
    }

    @Override
    public void setPreference(String text) {
        editor.putString(keyEditedVideoDestination, text);
        editor.commit();
        editTextPreferenceView.goBack();
    }


    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(keyEditedVideoDestination, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterEditedVideoDestination_FTP;
    }

    @Override
    public void removeData() {
        editor.putString(keyEditedVideoDestination, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }

}
