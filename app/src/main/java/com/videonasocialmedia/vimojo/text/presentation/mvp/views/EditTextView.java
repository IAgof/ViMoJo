package com.videonasocialmedia.vimojo.text.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;


/**
 * Created by ruth on 1/09/16.
 */
public interface EditTextView {
    void updateProject();
    void updateButtonToThemeDark();
    void updateButtonToThemeLight();
    void updateTextToThemeDark();
    void updateTextToThemeLight();
    void setCheckboxShadow(boolean shadowActivated);
    void setPositionEditText(String position);
    void setEditText(String textSelected);
    void hideKeyboard();
    void showKeyboard();
    void navigateTo(Class cls, int currentVideoIndex);

    // Player Views
    void attachView(Context context);
    void detachView();
    void setVideonaPlayerListener();
    void setAspectRatioVerticalVideos(int height);
    void initSingleClip(VMComposition vmComposition, int clipPosition);
    void setImageText(String text, String textPosition, boolean textWithShadow);
}
