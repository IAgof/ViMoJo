package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;

/**
 *
 */
public interface EditNavigatorView {
    void enableNavigatorActions();

    void disableNavigatorActions();

    void goToMusic(Music music);
}
