package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;

/**
 * Created by jca on 7/7/15.
 */
public interface OnReorderMediaListener {

    void onMediaReordered(Media media, int newPosition);

    void onErrorReorderingMedia();
}
