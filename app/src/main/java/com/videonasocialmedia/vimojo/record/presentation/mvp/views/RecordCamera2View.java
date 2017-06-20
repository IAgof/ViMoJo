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


import android.view.MotionEvent;

import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

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

    void showRecordedVideoThumbWithText(String path);

    void hideRecordedVideoThumbWithText();

    void showVideosRecordedNumber(int numberOfVideos);

    void hideVideosRecordedNumber();

    void stopMonitoringRotation();

    // UI Views showed/hidden by user

    void hidePrincipalViews();

    void showPrincipalViews();

    void hideRightControlsView();

    void showRightControlsView();

    void showSettingsCameraView();

    void hideSettingsCameraView();

    void hideAdvancedAFSelection();

    void hideISOSelection();

    void hideWhiteBalanceSelection();

    void setupWhiteBalanceSupportedModesButtons(List<String> values);

    void hideMetteringModeSelection();

    // Setters camera

    void setFlash(boolean on);

    void setFlashSupported(boolean state);

    void setResolutionSelected(int height);

    void setFocus(MotionEvent event);


    void setZoom(float value);

    // Others

    void showError(String message);

    void showProgressAdaptingVideo();

    void hideProgressAdaptingVideo();

    void navigateTo(Class cls);

    void showBatteryStatus(Constants.BATTERY_STATUS statusBattery, int batteryPercent);

    void showAlertDialogBattery();

    void showFreeStorageSpace(Constants.MEMORY_STATUS memoryStatus, int memoryPercent,
                              String freeMemoryInBytes, String totalMemoryInBytes);

    void showAlertDialogStorage();
}