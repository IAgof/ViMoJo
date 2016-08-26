package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by ruth on 24/08/16.
 */
public class EditedVideoDestinationPreferencePresenter extends EditTextPreferencePresenter {

    public EditedVideoDestinationPreferencePresenter(EditTextPreferenceView editTextPreferenceView,
                                    SharedPreferences sharedPreferences) {
        super(editTextPreferenceView, sharedPreferences);
    }

    @Override
    public void setPreference(String text) {
        editor.putString(ConfigPreferences.EDITED_VIDEO_DESTINATION, text);
        editor.commit();
        editTextPreferenceView.goBack();
    }


    @Override
    public String getPreviousText() {
        return sharedPreferences.getString(ConfigPreferences.EDITED_VIDEO_DESTINATION, null);
    }

    @Override
    public int getHintText() {
        return R.string.enterEditedVideoDestination_FTP;
    }

    @Override
    public void removeData() {
        editor.putString(ConfigPreferences.EDITED_VIDEO_DESTINATION, null);
        editor.commit();
        editTextPreferenceView.removeEditText();
        editTextPreferenceView.hideInfoText();
    }

}
