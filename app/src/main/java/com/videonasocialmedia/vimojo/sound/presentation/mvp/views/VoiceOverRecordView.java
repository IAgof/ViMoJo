package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

/**
 * Created by ruth on 15/09/16.
 */
public interface VoiceOverRecordView {
    void initVoiceOverView(int startTime, int maxSeekBar);
    void navigateToVoiceOverVolumeActivity(String voiceOverRecordedPath);
    void showError(String errorMessage);
    void updateProject();
    void resetVoiceOverRecorded();
    void disableRecordButton();
    void showProgressDialog();
    void hideProgressDialog();
    void disablePlayerPlayButton();
}
