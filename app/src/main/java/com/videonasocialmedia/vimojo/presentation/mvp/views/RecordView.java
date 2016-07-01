/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.vimojo.presentation.mvp.views;


import com.videonasocialmedia.vimojo.model.entities.editor.effects.Effect;

import java.util.List;

public interface RecordView {

    void showRecordButton();

    void showStopButton();

    void showMenuOptions();

    void hideMenuOptions();

    void showChronometer();

    void hideChronometer();

    void startChronometer();

    void stopChronometer();

    void lockScreenRotation();

    void unlockScreenRotation();

    void reStartScreenRotation();

    void lockNavigator(); //en VideonaView

    void unLockNavigator(); //en VideonaView

    void showFlashOn(boolean on);

    void showFlashSupported(boolean state);

    void showFrontCameraSelected();

    void showBackCameraSelected();

    void showError(String errorMessage); //videonaView

    void showError(int stringResourceId); //videonaView

    void goToShare(String videoToSharePath);

    void showProgressDialog();

    void hideProgressDialog();

    void finishActivityForResult(String path);
}