package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by ruth on 15/09/16.
 */
public interface VoiceOverView {

    void initVoiceOverView(int startTime, int maxSeekBar);
    void bindVideoList(List<Video> movieList);
    void resetPreview();
    void playVideo();
    void pauseVideo();
    void updateSeekBar(int progress);
    void navigateToSoundVolumeActivity();
}
