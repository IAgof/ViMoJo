package com.videonasocialmedia.vimojo.sound.domain;

/**
 * Created by alvaro on 23/10/16.
 */
public interface OnGetAudioFadeInFadeOutFromVideoListener {
    void onGetAudioFadeInFadeOutFromVideoSuccess(String audioFile);
    void onGetAudioFadeInFadeOutFromVideoError(String message);
}
