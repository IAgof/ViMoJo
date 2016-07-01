package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public interface VideoPreviewView {

    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void updateVideoList();

    void showPreview(List<Video> movieList);

    void showError(String message);

    void updateSeekBarDuration(int projectDuration);

    void updateSeekBarSize();

    //void changeMusicSource(Music music);

    //void removeMusic();

}
