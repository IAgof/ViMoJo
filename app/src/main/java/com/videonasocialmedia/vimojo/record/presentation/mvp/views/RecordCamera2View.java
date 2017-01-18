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

    void showRecordButton();

    void showStopButton();

    void showSettingsOptions();

    void hideSettingsOptions();

    void showChronometer();

    void hideChronometer();

    void startChronometer();

    void stopChronometer();

    void showFlashOn(boolean on);

    void showFlashSupported(boolean state);

    void showFrontCameraSelected();

    void showBackCameraSelected();

    void showError(int stringResourceId);

    void finishActivityForResult(String path);

    void hidePrincipalViews();

    void showPrincipalViews();

    void showRecordedVideoThumb(String path);

    void hideRecordedVideoThumb();

    void showVideosRecordedNumber(int numberOfVideos);

    void hideVideosRecordedNumber();

    void showResolutionSelected(int height);

}