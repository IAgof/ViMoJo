package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by ruth on 19/09/16.
 */
public interface SoundVolumeView {

    void bindVideoList(List<Video> movieList);
    void resetPreview();
    void goToEditActivity();
    void setVideoFadeTransitionAmongVideos();
    void setAudioFadeTransitionAmongVideos();
}
