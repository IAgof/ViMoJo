package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 *
 */
public interface MusicDetailView {

    void setupScene(boolean isMusicInProject);

    void bindVideoList(List<Video> movieList);

    void setMusic(Music music, boolean scene);

    void goToEdit(String musicTitle);
}
