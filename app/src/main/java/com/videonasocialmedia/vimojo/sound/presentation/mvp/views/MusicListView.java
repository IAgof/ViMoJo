package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public interface MusicListView {
    void showMusicList(List<Music> musicList);
    void goToDetailActivity(String mediaPath);
    void setVideoFadeTransitionAmongVideos();
    void updateProject();
}
