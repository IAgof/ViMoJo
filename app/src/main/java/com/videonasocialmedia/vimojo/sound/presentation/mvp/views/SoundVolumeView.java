package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by ruth on 19/09/16.
 */
public interface SoundVolumeView {

    void bindVideoList(List<Video> movieList);
    void resetPreview();
    void goToEditActivity();
    void setMusic(Music music);
}
