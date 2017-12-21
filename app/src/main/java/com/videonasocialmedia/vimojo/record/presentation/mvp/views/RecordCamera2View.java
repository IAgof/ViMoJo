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


import android.util.Range;
import android.view.MotionEvent;

import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

public interface RecordCamera2View {

    // Start/Stop record view

    void showRecordButton();

    void showStopButton();

    void startChronometer();

    void stopChronometer();

    void resetChronometer();

    void showRecordPointIndicator();

    void hideRecordPointIndicator();

    void showNavigateToSettingsActivity();

    void hideNavigateToSettingsActivity();

    void enableChangeCameraIcon();

    void disableChangeCameraIcon();

    void setSwitchCameraSupported(boolean b);

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

    // Focus settings

    void showAdvancedAFSelection();

    void hideAdvancedAFSelection();

    // ISO settings

    void showISOSelection();

    void hideISOSelection();

    void setupISOSupportedModesButtons(Range<Integer> supportedISORange);

    // White balance settings

    void showWhiteBalanceSelection();

    void hideWhiteBalanceSelection();

    void setupWhiteBalanceSupportedModesButtons(List<String> values);

    void hideWhiteBalanceSubmenu();

    void deselectAllWhiteBalanceButtons();

    void selectWbSettingAuto();

    // Metering - exposure settings

    void setManualExposure();

    void disableManualExposure();

    void setAutoExposure();

    void resetSpotMeteringSelector();

    void showMetteringModeSelection();

    void hideMetteringModeSelection();

    void hideManualExposureSubmenu();

    void deselectAllISOButtons();

    void setupManualExposureTime(int minimumExposureCompensation);

    void enableExposureTimeSeekBar();

    void disableExposureTimeSeekBar();

    void hideMeteringModeSelectionSubmenu();

    void setupMeteringModeSupportedModesButtons(List<String> values);

    // focus methods

    void setupFocusSelectionSupportedModesButtons(List<String> values);

    void hideAFSelectionSubmenu();

    void deselectAllFocusSelectionButtons();

    void setAutoSettingsFocusModeByDefault();

    void setFocusModeManual(MotionEvent event);

    // Setters camera

    void setFlash(boolean on);

    void setFlashSupported(boolean state);

    void setCameraSettingSelected(String resolution, String quality, String frameRate);

    void setZoom(float value);

    void hideZoomSelectionSubmenu();

    void disableGrid();

    // Others

    void showError(String message);

    void navigateTo(Class cls);

    void showBatteryStatus(Constants.BATTERY_STATUS statusBattery, int batteryPercent);

    void showAlertDialogBattery();

    void showFreeStorageSpace(Constants.MEMORY_STATUS memoryStatus, int memoryPercent,
                              String freeMemoryInBytes, String totalMemoryInBytes);

    void showAlertDialogStorage();

    /* Audio gain methods */

    void updateAudioGainSeekbarDisability();

    void disableAudioGainControls();

    void setAudioGain(int defaultAudioGain);

    void hideSoundVolumeSubmenu();

    void showAudioGainButton();

    void hideAudioGainButton();
    /**
     * Sets recordview picometer value and color
     * @param progress value of picometer progress meassured from 0 to 100
     * @param color android color for picometer seekbar
     */
    void showProgressPicometer(int progress, int color);

    void showExternalMicrophoneConnected();

    void showSmartphoneMicrophoneWorking();

    void deselectAllMeteringModeButtons();

    void hideExposureCompensationSubmenu();

    void disableSpotMeteringControl();

    void selectMeteringModeAutoButton();

    void deselectExposureCompensation();

    void resetManualExposure();

    void exposureTimeChanged(long exposureTime);

    void setCameraDefaultSettings();

    // Show/hide default button
    void showDefaultButton();

    void hideDefaultButton();

    void showAudioGainButton();

    void hideAudioGainButton();


}
