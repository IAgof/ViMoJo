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


import com.videonasocialmedia.videonamediaframework.model.media.effects.Effect;

public interface RecordView {

    void showRecordButton();

    void showStopButton();

    void showSettingsOptions();

    void hideSettingsOptions();

    void showChronometer();

    void hideChronometer();

    void startChronometer();

    void stopChronometer();

    void lockScreenRotation();

    void unlockScreenRotation();

    void reStartScreenRotation();

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

    void hidePrincipalViews ();

    void showPrincipalViews ();

    void showRecordedVideoThumb(String path);

    void hideRecordedVideoThumb();

    void showVideosRecordedNumber(int numberOfVideos);

    void hideVideosRecordedNumber();

    void showResolutionSelected(int height);

    void showGridLayout();

}