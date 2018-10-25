package com.videonasocialmedia.vimojo.split.presentation.mvp.views;

/**
 * Created by jca on 8/7/15.
 */
public interface SplitView {

    void initSplitView(int maxSeekBar);

    void showError(int stringResourceId);

    void updateSplitSeekbar(int progress);

    void refreshTimeTag(int currentPosition);

    void updateProject();
}
