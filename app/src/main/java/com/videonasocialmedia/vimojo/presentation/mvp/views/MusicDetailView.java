package com.videonasocialmedia.vimojo.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Music;

/**
 *
 */
public interface MusicDetailView {
    void musicSelected();
    void setMusic(Music music);
    void goToSoundActivity();
    void showError(String message);
    void updateProject();
    void sendCrashlyticsLog(String messageLog);

    // Player views
    void attachView(Context context);
    void detachView();
    void setAspectRatioVerticalVideos(int height);
    void init(VMComposition vmComposition);
    void playPreview();

}
