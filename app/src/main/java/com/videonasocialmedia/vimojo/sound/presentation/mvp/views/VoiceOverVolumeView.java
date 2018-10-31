package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;

/**
 * Created by ruth on 19/09/16.
 */
public interface VoiceOverVolumeView {
    void goToSoundActivity();
    void showError(String message);
    void updateTagVolume(String percentageVolume);

    // Player Views
    void attachView(Context context);
    void detachView();
    void setAspectRatioVerticalVideos(int height);
    void init(VMComposition vmComposition);
    void setVoiceOverVolume(float volume);
}
