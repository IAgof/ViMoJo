package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by jliarte on 13/05/16.
 */
public interface VideonaPlayerView {
    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void seekToClip(int position);

    void setMusic(Music music);

    void bindVideoList(List<Video>videoList);

}
