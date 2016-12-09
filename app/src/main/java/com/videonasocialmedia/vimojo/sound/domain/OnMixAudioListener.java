package com.videonasocialmedia.vimojo.sound.domain;

/**
 * Created by alvaro on 22/09/16.
 */

public interface OnMixAudioListener {

    void onMixAudioSuccess(String path);
    void onMixAudioError();
}
