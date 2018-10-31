package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;

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

    // Player Views
    void attachView(Context context);
    void detachView();
    void setAspectRatioVerticalVideos(int height);
    void init(VMComposition vmComposition);
    void playPreview();
    void pausePreview();
    void setVideoVolume(float volume);
    void setVoiceOverVolume(float volume);
    void setMusicVolume(float volume);
}
