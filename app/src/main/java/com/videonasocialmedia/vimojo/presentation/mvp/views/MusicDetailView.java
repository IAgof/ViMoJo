package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;

/**
 *
 */
public interface MusicDetailView {

    void musicSelectedOptions(boolean isMusicInProject);

    void setMusic(Music music, boolean scene);

    void goToSoundActivity();

    void showError(String message);

    void updateProject();
}
