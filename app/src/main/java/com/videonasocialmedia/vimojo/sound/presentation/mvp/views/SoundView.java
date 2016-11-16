package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public interface SoundView {
    void bindVideoList(List<Video> movieList);
    void resetPreview();
}
