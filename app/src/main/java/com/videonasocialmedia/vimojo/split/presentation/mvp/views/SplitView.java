package com.videonasocialmedia.vimojo.split.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by jca on 8/7/15.
 */
public interface SplitView {

    void initSplitView(int startTime, int maxSeekBar);

    void playPreview();

    void pausePreview();

    void showPreview(List<Video> movieList);

    void showError(int stringResourceId);

    void showText(String text, String position);

    void updateSplitSeekbar(int progress);

}
