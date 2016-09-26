package com.videonasocialmedia.vimojo.sound.domain;

/**
 * Created by alvaro on 16/09/16.
 */
public interface OnMergeVoiceOverAudiosListener {

    void onMergeVoiceOverAudioSuccess(String outputPath);
    void onMergeVoiceOverAudioError(String message);
}
