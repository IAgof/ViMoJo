package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Music;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public interface MusicListView {
    void showMusicList(List<Music> musicList);
    void updateProject();
    void navigateToDetailMusic(String musicPath);

    //Player Views
    void attachView(Context context);
    void detachView();
    void setAspectRatioVerticalVideos(int height);
    void init(VMComposition vmComposition);
}
