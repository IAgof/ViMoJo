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

package com.videonasocialmedia.vimojo.record.presentation.mvp.views;


public interface RecordCamera2View {


    // Start/Stop record view

    void showRecordButton();

    void showStopButton();

    void showChronometer();

    void hideChronometer();

    void startChronometer();

    void stopChronometer();

    void showNavigateToSettingsActivity();

    void hideNavigateToSettingsActivity();

    void showChangeCamera();

    void hideChangeCamera();

    void showRecordedVideoThumb(String path);

    void hideRecordedVideoThumb();

    void showVideosRecordedNumber(int numberOfVideos);

    void hideVideosRecordedNumber();


    // UI Views showed/hidden by user

    void hidePrincipalViews();

    void showPrincipalViews();

    void hideRightControlsView();

    void showRightControlsView();

    void showBottomControlsView();

    void hideBottomControlsView();


    // Setters camera

    void setFlash(boolean on);

    void setFlashSupported(boolean state);

    void setResolutionSelected(int height);


    // Others

    void showError(int stringResourceId);

    void finishActivityForResult(String path);

}