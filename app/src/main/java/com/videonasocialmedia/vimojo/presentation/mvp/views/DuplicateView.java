package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by jca on 8/7/15.
 */
public interface DuplicateView {

    void initDuplicateView(Video video);

    void updateProject();

    void showError(String errorMessage);
}
