package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public interface MusicListView {
    void showVideoList(List<Music> musicList);
    void bindVideoList(List<Video> movieList);
    void resetPreview();
    void goToDetailActivity(String mediaPath);
}
