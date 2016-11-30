package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by jca on 8/7/15.
 */
public interface DuplicateView {

    void initDuplicateView(String videoPath);

    void playPreview();

    void pausePreview();

    void showPreview(List<Video> movieList);

    void showError(String message);

}
