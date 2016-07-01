/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.presentation.mvp.views;

/**
 * This interface is used to show the setting menu.
 */
public interface EditTextPreferenceView {
    /**
     * Sets the actual user account data
     *
     * @param propertie
     * @param value
     */
    void setUserPropertyToMixpanel(String propertie, String value);

    void showMessage(int messageId);

    void showInfoText();

    void hideInfoText();

    void removeEditText();

    void goBack();
}
