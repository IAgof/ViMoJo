package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 *
 */
public interface MusicDetailView {

    void musicSelectedOptions(boolean isMusicInProject);

    void bindVideoList(List<Video> movieList);

    void setMusic(Music music, boolean scene);

    void goToSoundActivity();
    
    void setVideoFadeTransitionAmongVideos();
}
