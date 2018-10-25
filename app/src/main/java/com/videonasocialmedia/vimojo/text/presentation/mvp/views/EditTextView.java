package com.videonasocialmedia.vimojo.text.presentation.mvp.views;

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
}
