package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;

/**
 *
 */
public interface EditNavigatorView {
    void enableNavigatorActions();

    void disableNavigatorActions();

    void goToMusic(Music music);
}
